package org.grajagan.jtrak2xml.converters;

/*-
 * #%L
 * JTrak to XML Converter
 * %%
 * Copyright (C) 2002 - 2018 Grajagan Org.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import de.frobese.jtrak.dc.base.LoggedDive;
import de.frobese.jtrak.dc.smartcom.SmartComDive;
import de.frobese.jtrak.dc.smartpro.SmartProDive;
import de.frobese.jtrak.model.Activation;
import de.frobese.jtrak.model.Partner;
import de.frobese.jtrak.model.Profile;
import de.frobese.jtrak.model.Type;
import de.frobese.jtrak.util.TCUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.grajagan.xml.model.Person;
import org.grajagan.xml.model.macdive.Dive;
import org.grajagan.xml.model.macdive.Gas;
import org.grajagan.xml.model.macdive.Item;
import org.grajagan.xml.model.macdive.Manufacturer;
import org.grajagan.xml.model.macdive.RootElement;
import org.grajagan.xml.model.macdive.Sample;
import org.grajagan.xml.model.macdive.Site;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JTrak2MacDiveXmlConverter extends AbstractJTrak2XMLConverter {
    private static final Logger logger = Logger.getLogger(JTrak2MacDiveXmlConverter.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();

        JTrak2MacDiveXmlConverter exporter = new JTrak2MacDiveXmlConverter();
        File outFile = new File(System.getProperty("user.home") + "/Desktop/jtrak-export.xml");

        Person owner = new Person();
        owner.setFirstname("JTrak");
        owner.setLastname("Diver");

        exporter.export(outFile, owner);
    }

    @Override
    public String getFileExtension() {
        return "xml";
    }

    @Override
    public void export(File outputFile, Person owner) {
        File outputXMLFile = JTrak2XMLConverter.setExtension(outputFile, getFileExtension());

        RootElement root = new RootElement();
        List<Dive> dives = new ArrayList<>();
        root.setDives(dives);

        Profile profile = getLogbuchManager().getProfile(0);

        int repetitiveDive = 0;

        Map<String, Item> diveComputerMap = new HashMap<>();
        int diveNumber = 0;

        for (de.frobese.jtrak.dc.base.Dive jtrakDive : profile.getTauchgangList()) {
            if (jtrakDive.getInterval() == 0) {
                repetitiveDive = 0;
            }

            Dive dive = new Dive();
            dives.add(dive);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dive.setDate(format.format(jtrakDive.getDate()));

            dive.setIdentifier("dive_" + jtrakDive.getDate().getTime()/1000);
            dive.setSurfaceInterval(jtrakDive.getInterval());
            dive.setRating(jtrakDive.getVote());
            dive.setRepetitiveDive(++repetitiveDive);
            dive.setDiver(owner);
            dive.setDiveNumber(++diveNumber);

            Item computer;
            Long computerSerial = jtrakDive.getDeviceID();
            Byte computerId = jtrakDive.getDeviceType();
            String id = computerId + "-" + computerSerial;
            if (!diveComputerMap.containsKey(id)) {
                computer = new Item();
                computer.setName(TCUtils.getDeviceName(computerId) + " SN" + computerSerial);
                computer.setSerial(computerSerial.toString());
                computer.setType("Computer");

                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setName("Uwatec");
                computer.setManufacturer(manufacturer);

                diveComputerMap.put(id, computer);
            }

            computer = diveComputerMap.get(id);
            dive.setComputer(computer.getName());
            dive.setSerial(computer.getSerial());

            Set<Item> itemSet = new HashSet<>();
            dive.setGear(itemSet);
            itemSet.add(computer);

            dive.setMaxDepth(jtrakDive.getMaxDepth() / 100f);

            // this value is not reliable
            // dive.setAverageDepth(jtrakDive.getAverageDepth() / 100f);

            dive.setDuration(jtrakDive.getDiveTime() * 60);
            dive.setTempAir(jtrakDive.getAirTemp() / 10f);
            dive.setTempHigh(jtrakDive.getMaxTemp() / 10f);
            dive.setTempLow(jtrakDive.getMinTemp() / 10f);
            dive.setVisibility(jtrakDive.getVisibility() / 10f + " ft/m");
            dive.setNotes(jtrakDive.getComments());
            dive.setWeather(jtrakDive.getWeather().replace("db.weather.", ""));

            Set<String> tags = new HashSet<>();
            dive.setTags(tags);
            for (Activation tag : jtrakDive.getActivities()) {
                tags.add(tag.getValue());
            }

            Set<String> types = new HashSet<>();
            dive.setTypes(types);
            for (Type type : jtrakDive.getTypes()) {
                types.add(type.getValue());
            }

            Set<String> buddies = new HashSet<>();
            dive.setBuddies(buddies);
            for (Partner buddy : jtrakDive.getPartners()) {
                buddies.add(buddy.getName());
            }

            Set<Gas> gasSet = new HashSet<>();
            List<Gas> gasList = new ArrayList<>();
            dive.setGases(gasSet);
            Gas gas = new Gas();
            gas.setTankName("Tank 1");
            gas.setPressureStart(jtrakDive.getTankStartPressure() / 1000f);
            gas.setPressureEnd(jtrakDive.getTankEndPressure() / 1000f);
            gas.setOxygen(jtrakDive.getO2mixture());
            gas.setTankSize(jtrakDive.getTankVolume() / 1000);

            if (jtrakDive.getO2mixture() != 21) {
                dive.setGasModel("Nitrox");
            } else {
                dive.setGasModel("Air");
            }

            if (gasSet.add(gas)) {
                gasList.add(gas);
            }

            if (jtrakDive.getTank2Volume() != 0) {
                gas = new Gas();
                gas.setTankName("Tank 2");
                gas.setPressureStart(jtrakDive.getTank2StartPressure() / 1000f);
                gas.setPressureEnd(jtrakDive.getTank2EndPressure() / 1000f);
                gas.setOxygen(jtrakDive.getO2mixture2());
                gas.setTankSize(jtrakDive.getTank2Volume());

                if (gasSet.add(gas)) {
                    gasList.add(gas);
                }
            }

            if (jtrakDive.getTankDVolume() != 0) {
                gas = new Gas();
                gas.setTankName("Tank D");
                gas.setPressureStart(jtrakDive.getTankDStartPressure() / 1000f);
                gas.setPressureEnd(jtrakDive.getTankDEndPressure() / 1000f);
                gas.setOxygen(jtrakDive.getO2mixtureD());
                gas.setTankSize(jtrakDive.getTankDVolume());

                if (gasSet.add(gas)) {
                    gasList.add(gas);
                }
            }

            if (jtrakDive.getLocation() != null) {
                Site site = new Site();
                site.setCountry(jtrakDive.getCountry());
                site.setName(jtrakDive.getLocation());
                dive.setSite(site);
            }

            if (jtrakDive instanceof LoggedDive) {
                LoggedDive loggedDive = (LoggedDive) jtrakDive;
                int offset = loggedDive.isApneaOn() ? 60 : 15;

                dive.setSampleInterval(60 / offset);

                int[] depthData = loggedDive.getDepth_1mbar_Range();

                int[] tempData = null;
                boolean haveTemperature = loggedDive.hasTempTrace();
                if (haveTemperature) {
                    tempData = ((SmartProDive) jtrakDive).getTempTrend_01C_Range();
                }

                int[] tankPressureData = null;
                boolean haveAirTrend = loggedDive.hasAirConsumption() && !loggedDive.isApneaOn();
                if (haveAirTrend) {
                    tankPressureData = ((SmartComDive) jtrakDive).getTank_250mb_res();
                }

                List<Sample> samples = new ArrayList<>();
                dive.setSamples(samples);

                Float averageDepth = 0f;
                Float depthSeconds = 0f;

                for (int i = 0; i < depthData.length; i++) {
                    Sample sample = new Sample();

                    Float secs = (float) i * 60 / offset;
                    sample.setTime(secs);

                    Float depth = (depthData[i] - depthData[0]) / 1000f * 10;
                    sample.setDepth((depthData[i] - depthData[0]) / 1000f * 10);

                    depthSeconds += (60 / offset) * depth;
                    averageDepth = depthSeconds / secs;

                    if (haveTemperature) {
                        sample.setTemperature(tempData[i] / 10f);
                    }

                    if (haveAirTrend && tankPressureData[i] != 0) {
                        sample.setPressure(tankPressureData[i] / 4f);
                    } else if (haveAirTrend) {
                        int t = ((SmartComDive) jtrakDive).getTankAtPosition(i) - 1;
                        sample.setPressure(gasList.get(t).getPressureStart());
                    }

                    samples.add(sample);
                }

                dive.setAverageDepth(averageDepth);
            } else {
                dive.setAverageDepth(jtrakDive.getAverageDepth() / 100f);
            }
        }

        try {
            JAXBContext jc = JAXBContext.newInstance(RootElement.class);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty("com.sun.xml.internal.bind.xmlHeaders",
                    "\n<!DOCTYPE dives SYSTEM  \"http://www.mac-dive.com/macdive_logbook.dtd\">");

            FileOutputStream out = new FileOutputStream(outputXMLFile);
            m.marshal(root, out);
        } catch (Exception e) {
            logger.fatal("Cannot generate XML output", e);
        }
    }

    @Override
    public String getSupportedFormat() {
        return "MacDive XML";
    }
}

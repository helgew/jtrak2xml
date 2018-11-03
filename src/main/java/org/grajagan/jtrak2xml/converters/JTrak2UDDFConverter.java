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
import de.frobese.jtrak.model.Partner;
import de.frobese.jtrak.model.Profile;
import de.frobese.jtrak.util.TCUtils;
import org.apache.log4j.Logger;
import org.grajagan.xml.XmlUtil;
import org.grajagan.xml.model.IDiver;
import org.grajagan.xml.model.Person;
import org.grajagan.xml.model.uddf.Address;
import org.grajagan.xml.model.uddf.Buddy;
import org.grajagan.xml.model.uddf.Dive;
import org.grajagan.xml.model.uddf.DiveComputer;
import org.grajagan.xml.model.uddf.DiveSite;
import org.grajagan.xml.model.uddf.Equipment;
import org.grajagan.xml.model.uddf.Generator;
import org.grajagan.xml.model.uddf.Geography;
import org.grajagan.xml.model.uddf.InformationAfterDive;
import org.grajagan.xml.model.uddf.InformationBeforeDive;
import org.grajagan.xml.model.uddf.Link;
import org.grajagan.xml.model.uddf.Manufacturer;
import org.grajagan.xml.model.uddf.Mix;
import org.grajagan.xml.model.uddf.Notes;
import org.grajagan.xml.model.uddf.Owner;
import org.grajagan.xml.model.uddf.Rating;
import org.grajagan.xml.model.uddf.RepetitionGroup;
import org.grajagan.xml.model.uddf.RootElement;
import org.grajagan.xml.model.uddf.Site;
import org.grajagan.xml.model.uddf.Suit;
import org.grajagan.xml.model.uddf.SurfaceInterval;
import org.grajagan.xml.model.uddf.Tank;
import org.grajagan.xml.model.uddf.TankData;
import org.grajagan.xml.model.uddf.WayPoint;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JTrak2UDDFConverter extends AbstractJTrak2XMLConverter {
    private static final Logger logger = Logger.getLogger(JTrak2UDDFConverter.class);

    public static void main(String[] args) {
        JTrak2UDDFConverter exporter = new JTrak2UDDFConverter();
        File outFile = new File(System.getProperty("user.home") + "/Desktop/jtrak-export.uddf");

        Person owner = new Person();
        owner.setFirstname("JTrak");
        owner.setLastname("Diver");

        exporter.export(outFile, owner);
    }

    @Override
    public String getFileExtension() {
        return "uddf";
    }

    @Override
    public void export(File outputFile, Person person) {
        File outputXMLFile = JTrak2XMLConverter.setExtension(outputFile, getFileExtension());

        RootElement root = new RootElement();

        Generator generator = new Generator();
        generator.setName(this.getClass().getSimpleName());
        generator.setDatetime(new Date());
        root.setGenerator(generator);

        List<IDiver> divers = new ArrayList<>();
        root.setDivers(divers);

        Owner owner = new Owner(person.toString());
        divers.add(owner);

        Profile profile = getLogbuchManager().getProfile(0);

        Map<String, Buddy> buddyMap = new HashMap<>();
        for (Partner partner : profile.getPartnerList()) {
            String name = partner.getName();
            Buddy buddy = new Buddy(name);
            divers.add(buddy);
            buddyMap.put(name, buddy);
        }

        DiveSite diveSite = new DiveSite();
        root.setDivesite(diveSite);

        List<Site> sites = new ArrayList<>();

        diveSite.setSiteList(sites);

        Map<String, Site> diveSiteMap = new HashMap<>();
        for (String siteName : profile.getAllLocations()) {
            Site site = new Site();
            site.setName(siteName);
            sites.add(site);
            diveSiteMap.put(siteName, site);
        }

        RepetitionGroup group = new RepetitionGroup();

        List<Dive> diveList = new ArrayList<>();
        group.setDives(diveList);

        Equipment equipment = owner.getEquipment();
        List<Tank> tankList = equipment.getTankList();
        List<Suit> suitList = equipment.getSuitList();
        Map<String, Suit> suitMap = new HashMap<>();

        List<DiveComputer> diveComputerList = equipment.getDiveComputerList();
        Map<String, DiveComputer> diveComputerMap = new HashMap<>();

        List<RepetitionGroup> repetitionGroups = new ArrayList<>();
        root.setRepetitionGroups(repetitionGroups);

        List<Mix> mixList = new ArrayList<>();
        Map<Integer, Mix> mixMap = new HashMap<>();
        root.setMixList(mixList);

        int diveCounter = 0;
        int numDiveOfDay = 1;
        for (de.frobese.jtrak.dc.base.Dive jtrakDive : profile.getTauchgangList()) {
            if (jtrakDive.getInterval() == 0) {
                diveList = new ArrayList<>();

                group = new RepetitionGroup();
                group.setDives(diveList);
                group.setId("dive_group_" + repetitionGroups.size());

                repetitionGroups.add(group);
            }

            Dive dive = new Dive();
            dive.setId("dive_" + ++diveCounter);

            diveList.add(dive);

            Set<Link> equipmentUsed = new HashSet<>();
            InformationBeforeDive informationbeforedive = new InformationBeforeDive();
            dive.setInformationbeforedive(informationbeforedive);

            informationbeforedive.setDivenumber(diveCounter);

            Set<Link> infoBeforeLinks = new HashSet<>();
            infoBeforeLinks.add(new Link(owner.getId()));

            informationbeforedive.setLinks(infoBeforeLinks);

            SurfaceInterval surfaceInterval = new SurfaceInterval();
            informationbeforedive.setSurfaceintervalbeforedive(surfaceInterval);

            if (jtrakDive.getInterval() == 0) {
                surfaceInterval.setInfinity("");
                numDiveOfDay = 1;
            } else {
                surfaceInterval.setIntervalInSeconds(jtrakDive.getInterval() * 60);
                numDiveOfDay++;
            }

            informationbeforedive.setDivenumberofday(numDiveOfDay);

            informationbeforedive.setDatetime(jtrakDive.getDate());

            informationbeforedive.setAirtemperature(getTempInK(jtrakDive.getAirTemp()));

            List<TankData> tankDataList = new ArrayList<>();
            dive.setTankDataList(tankDataList);

            TankData tankdata = new TankData();
            tankDataList.add(tankdata);

            tankdata.setTankvolume((float) jtrakDive.getTankVolume() / 1000);
            tankdata.setTankpressurebegin((float) jtrakDive.getTankStartPressure() * 100);
            tankdata.setTankpressureend((float) jtrakDive.getTankEndPressure() * 100);

            if (jtrakDive.getTank2Volume() != 0) {
                tankdata = new TankData();
                tankDataList.add(tankdata);
                tankdata.setTankvolume((float) jtrakDive.getTank2Volume() / 1000);
                tankdata.setTankpressurebegin((float) jtrakDive.getTank2StartPressure() * 100);
                tankdata.setTankpressureend((float) jtrakDive.getTank2EndPressure() * 100);
            }

            if (jtrakDive.getTankDVolume() != 0) {
                tankdata = new TankData();
                tankDataList.add(tankdata);
                tankdata.setTankvolume((float) jtrakDive.getTankDVolume() / 1000);
                tankdata.setTankpressurebegin((float) jtrakDive.getTankDStartPressure() * 100);
                tankdata.setTankpressureend((float) jtrakDive.getTankDEndPressure() * 100);
            }

            InformationAfterDive informationafterdive = new InformationAfterDive();
            dive.setInformationafterdive(informationafterdive);
            informationafterdive.setLinks(equipmentUsed);
            if (!jtrakDive.getComments().equals("")) {
                Notes notes = new Notes();
                notes.setPara(jtrakDive.getComments());
                informationafterdive.setNotes(notes);
            }

            informationafterdive.setGreatestdepth((float) jtrakDive.getMaxDepth() / 100);
            informationafterdive.setAveragedepth((float) jtrakDive.getAverageDepth() / 100);
            informationafterdive.setDiveduration((float) jtrakDive.getDiveTime() * 60);
            informationafterdive.setVisibility((float) jtrakDive.getVisibility() / 100);
            informationafterdive.setLowesttemperature(getTempInK(jtrakDive.getMinTemp()));

            if (jtrakDive.getVote() != 0) {
                Rating rating = new Rating();
                rating.setRatingvalue(jtrakDive.getVote());
                informationafterdive.setRating(rating);
            }

            if (jtrakDive.getLocation() != null) {
                Site site = diveSiteMap.get(jtrakDive.getLocation());
                String country = jtrakDive.getCountry();
                Address address = new Address();
                address.setCountry(country);

                Geography geography = new Geography();
                geography.setAddress(address);
                geography.setLocation(jtrakDive.getLocation());

                site.setGeography(geography);
                infoBeforeLinks.add(new Link(site.getId()));
            }

            for (Partner partner : jtrakDive.getPartners()) {
                infoBeforeLinks.add(new Link(buddyMap.get(partner.getName()).getId()));
            }

            Mix mix;
            Integer o2 = jtrakDive.getO2mixture();
            if (!mixMap.containsKey(o2)) {
                mix = Mix.getO2Mix(o2);
                mixList.add(mix);
                mixMap.put(o2, mix);
            }

            mix = mixMap.get(o2);
            tankDataList.get(0).setLink(new Link(mix.getId()));
            Tank tank = new Tank();

            String id = XmlUtil.generateId();
            tank.setId(id);
            equipmentUsed.add(new Link(id));

            tank.setTankmaterial(jtrakDive.getBottle().replace("db.bottle.", ""));
            tank.setTankvolume((float) jtrakDive.getTankVolume() / 1000);
            tankList.add(tank);

            if (jtrakDive.getO2mixture2() != 0) {
                o2 = jtrakDive.getO2mixture2();
                if (!mixMap.containsKey(o2)) {
                    mix = Mix.getO2Mix(o2);
                    mixList.add(mix);
                    mixMap.put(o2, mix);
                }

                mix = mixMap.get(o2);
                tankDataList.get(1).setLink(new Link(mix.getId()));

                tank = new Tank();
                id = XmlUtil.generateId();
                tank.setId(id);
                equipmentUsed.add(new Link(id));

                tank.setTankmaterial(jtrakDive.getBottle2().replace("db.bottle.", ""));
                tank.setTankvolume((float) jtrakDive.getTank2Volume() / 1000);
                tankList.add(tank);
            }

            if (jtrakDive.getO2mixtureD() != 0) {
                o2 = jtrakDive.getO2mixtureD();
                if (!mixMap.containsKey(o2)) {
                    mix = Mix.getO2Mix(o2);
                    mixList.add(mix);
                    mixMap.put(o2, mix);
                }

                mix = mixMap.get(o2);
                tankDataList.get(2).setLink(new Link(mix.getId()));

                tank = new Tank();
                id = XmlUtil.generateId();
                tank.setId(id);
                equipmentUsed.add(new Link(id));

                tank.setTankmaterial(jtrakDive.getBottleD().replace("db.bottle.", ""));
                tank.setTankvolume((float) jtrakDive.getTankDVolume() / 1000);
                tankList.add(tank);
            }

            String suitType = jtrakDive.getSuit().replace("suite", "suit").replaceAll("\\.", "_")
                    .replaceAll("-", "");
            if (!suitMap.containsKey(suitType)) {
                Suit suit = new Suit();
                Suit.SuitType type;
                switch (suitType) {
                    case "db_suit_wet":
                    case "db_suit_semidry":
                        type = Suit.SuitType.WETSUIT;
                        break;
                    case "db_suit_dry":
                        type = Suit.SuitType.DRYSUIT;
                        break;
                    case "db_suit_shorty":
                        type = Suit.SuitType.HOTWATERSUIT;
                        break;
                    default:
                        type = Suit.SuitType.OTHER;
                }

                suit.setSuitType(type);
                suit.setId(suitType);
                suitMap.put(suitType, suit);
                suitList.add(suit);
            }

            Suit suit = suitMap.get(suitType);
            equipmentUsed.add(new Link(suit.getId()));

            DiveComputer computer;
            Long computerSerial = jtrakDive.getDeviceID();
            Byte computerId = jtrakDive.getDeviceType();
            id = computerId + "-" + computerSerial;
            if (!diveComputerMap.containsKey(id)) {
                computer = new DiveComputer();
                computer.setName(TCUtils.getDeviceName(computerId));
                computer.setSerialnumber(computerSerial.toString());
                computer.setId(XmlUtil.generateId());

                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setName("Uwatec");
                computer.setManufacturer(manufacturer);

                diveComputerMap.put(id, computer);

                diveComputerMap.put(id, computer);
                diveComputerList.add(computer);
            }

            computer = diveComputerMap.get(id);
            equipmentUsed.add(new Link(computer.getId()));

            LoggedDive loggedDive = (LoggedDive) jtrakDive;
            int offset = loggedDive.isApneaOn() ? 60 : 15;
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

            List<WayPoint> wayPoints = new ArrayList<>();
            dive.setWayPoints(wayPoints);

            for (int i = 0; i < depthData.length; i++) {
                WayPoint wayPoint = new WayPoint();
                wayPoint.setDivetime((float) i * 60 / offset);
                wayPoint.setDepth((depthData[i] - depthData[0]) / 1000f * 10);

                if (haveTemperature) {
                    wayPoint.setTemperature(getTempInK(tempData[i]));
                }

                if (haveAirTrend && tankPressureData[i] != 0) {
                    wayPoint.setTankpressure(tankPressureData[i] * 25000f);
                } else if (haveAirTrend) {
                    int t = ((SmartComDive) jtrakDive).getTankAtPosition(i) - 1;
                    wayPoint.setTankpressure(tankDataList.get(t).getTankpressurebegin());
                }

                wayPoints.add(wayPoint);
            }
        }

        try {
            JAXBContext jc = JAXBContext
                    .newInstance(RootElement.class, Owner.class, Buddy.class, DiveSite.class,
                            RepetitionGroup.class, Dive.class, Link.class, Mix.class);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            FileOutputStream out = new FileOutputStream(outputXMLFile);
            m.marshal(root, out);
        } catch (Exception e) {
            logger.fatal("Cannot generate XML output", e);
        }
    }

    private Float getTempInK(int temp) {
        return (float) temp / 10 + 273.15f;
    }

    @Override
    public String getSupportedFormat() {
        return "UDDF";
    }
}

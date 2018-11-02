package org.grajagan.xml.model.macdive;

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

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.grajagan.xml.model.Person;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.Set;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Dive {
    @XmlElement
    String date;

    @XmlElement
    String identifier;

    @XmlElement
    Integer diveNumber = 0;

    @XmlElement(nillable = true)
    Integer rating;

    @XmlElement
    Integer repetitiveDive;

    // a bit of voodoo to keep <diver> in the right order while using Person
    @XmlElement(nillable = true, name = "diver")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    String diverName;

    @XmlTransient
    Person diver;

    public void setDiver(Person diver) {
        this.diver = diver;
        diverName = diver.toString();
    }

    @XmlElement(nillable = true)
    String computer;

    @XmlElement(nillable = true)
    String serial;

    @XmlElement(nillable = true)
    Float maxDepth;

    @XmlElement(nillable = true)
    Float averageDepth;

    @XmlElement
    Float cns = 0.00f;

    @XmlElement(nillable = true)
    String decoModel;

    @XmlElement(nillable = true)
    Integer duration;

    @XmlElement(nillable = true)
    String gasModel;

    @XmlElement(nillable = true)
    Integer surfaceInterval;

    @XmlElement(nillable = true)
    Integer sampleInterval;

    @XmlElement(nillable = true)
    Float tempAir;

    @XmlElement(nillable = true)
    Float tempHigh;

    @XmlElement(nillable = true)
    Float tempLow;

    @XmlElement(nillable = true)
    String visibility;

    @XmlElement(nillable = true)
    Float weight;

    @XmlElement(nillable = true)
    String notes;

    @XmlElement(nillable = true)
    String diveMaster;

    @XmlElement(nillable = true)
    String diveOperator;

    @XmlElement(nillable = true)
    String skipper;

    @XmlElement(nillable = true)
    String boat;

    @XmlElement(nillable = true)
    String weather;

    @XmlElement(nillable = true)
    String current;

    @XmlElement(nillable = true)
    String surfaceConditions;

    @XmlElement(nillable = true)
    String entryType;

    @XmlElement
    Site site;

    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tag")
    Set<String> tags;

    @XmlElementWrapper(name = "types")
    @XmlElement(name = "type")
    Set<String> types;

    @XmlElementWrapper(name = "buddies")
    @XmlElement(name = "buddy")
    Set<String> buddies;

    @XmlElementWrapper(name = "gear")
    @XmlElement(name = "item")
    Set<Item> gear;

    @XmlElementWrapper(name = "gases")
    @XmlElement(name = "gas")
    Set<Gas> gases;

    @XmlElementWrapper(name = "samples")
    @XmlElement(name = "sample")
    List<Sample> samples;

    @XmlElementWrapper(name = "events", nillable = true)
    @XmlElement(name = "event")
    List<String> events;
}

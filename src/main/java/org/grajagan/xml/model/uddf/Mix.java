package org.grajagan.xml.model.uddf;

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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Mix {
    private Mix() {}

    @NonNull
    @XmlAttribute(required = true)
    String id;

    @XmlElement
    String name;

    @NonNull
    @XmlElement(required = true)
    Float o2;

    @XmlElement
    Float n2;

    @XmlElement
    Float he;

    @XmlElement
    Float ar;

    @XmlElement
    Float h2;

    @XmlElement
    Float equivalentairdepth;

    @XmlElement
    Float maximumoperationdepth;

    @XmlElement
    Float maximumpo2;

    @XmlElement
    String notes;

    public Mix(String id, Float o2) {
        this.id = id;
        this.o2 = o2;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "priceperlitre")
    class PricePerLitre {
        @XmlAttribute(required = true)
        String currency;

        @XmlValue
        Float value;
    }

    public static Mix getAirMix() {
        Mix air = new Mix("air", 0.21f);
        air.setN2(0.79f);
        air.setName("air");

        return air;
    }

    public static Mix getO2Mix(int o2) {
        if (o2 == 21) {
            return getAirMix();
        }

        Mix mix = new Mix("o2-" + o2, o2/100f);
        mix.setN2((100 - o2)/100f);
        mix.setName("o2 " + o2 + "%");

        return mix;
    }
}

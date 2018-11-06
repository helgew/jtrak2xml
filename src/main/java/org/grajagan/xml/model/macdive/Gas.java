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

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Gas {
    @XmlElement(nillable = true)
    Float pressureStart;

    @XmlElement(nillable = true)
    Float pressureEnd;

    Integer oxygen;
    Integer helium = 0;

    @XmlElement(name = "double")
    Integer doubleTanks = 0;

    @XmlElement(nillable = true)
    Integer tankSize;

    Integer workingPressure = 0;
    String supplyType = "Open Circuit";

    @XmlElement(nillable = true)
    Integer duration;

    String tankName;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Gas)) {
            return false;
        }

        Gas that = (Gas) obj;

        return this.tankName != null && this.tankName.equals(that.tankName);
    }

    @Override
    public int hashCode() {
        return tankName != null ? tankName.hashCode() : 0;
    }
}

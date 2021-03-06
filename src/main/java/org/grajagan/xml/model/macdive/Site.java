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
import java.util.Objects;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Site {
    @XmlElement(nillable = true)
    String country;

    @XmlElement(nillable = true)
    String location;

    @XmlElement(nillable = true)
    String name;

    @XmlElement(nillable = true)
    String bodyOfWater;

    @XmlElement(nillable = true)
    String waterType;

    @XmlElement(nillable = true)
    String difficulty;

    @XmlElement(nillable = true)
    Integer altitude = 0;

    @XmlElement(nillable = true)
    Float lat = 0f;

    @XmlElement(nillable = true)
    Float lon = 0f;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Site)) {
            return false;
        }

        Site that = (Site) obj;

        return ((this.country != null && this.country.equals(that.country)) || (
                this.country == null && that.country == null)) && (
                (this.location != null && this.location.equals(that.location)) || (
                        this.location == null && that.location == null)) && (
                (this.name != null && this.name.equals(that.name)) || (this.name == null
                        && that.name == null));
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, location, name);
    }
}

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

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Objects;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Suit {
    @XmlAttribute
    String id;

    @XmlTransient
    SuitType suitType;

    @XmlElement
    public String getName() {
        return suitType.name();
    }

    @XmlElement
    public String getSuittype() {
        return suitType.toString();
    }

    @XmlTransient
    public enum SuitType {
        DIVESKIN    ("dive-skin"),
        WETSUIT     ("wet-suit"),
        DRYSUIT     ("dry-suit"),
        HOTWATERSUIT("hot-water-suit"),
        OTHER       ("other");

        String type;

        SuitType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Suit)) {
            return false;
        }

        Suit that = (Suit) obj;

        return (id != null && id.equals(that.id)) ||
                (id == null && that.id == null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

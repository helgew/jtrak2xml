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
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "tankmaterial", "tankvolume" })
public class Tank {
    @XmlTransient
    String id;

    @XmlAttribute
    public String getId() {
        if (id == null) {
            id = "tank-" + tankmaterial + "-" + ((int) (0.00353147 * tankvolume)) * 10;
        }

        return id;
    }

    @XmlTransient
    String name;

    @XmlElement
    public String getName() {
        if (name == null) {
            name = tankmaterial + " tank " + id;
        }
        return name;
    }

    String tankmaterial;
    Float tankvolume;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Tank)) {
            return false;
        }

        Tank that = (Tank) obj;

        return getId() != null && getId().equals(that.id);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}

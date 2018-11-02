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
import org.grajagan.xml.XmlUtil;
import org.grajagan.xml.model.IDiver;
import org.grajagan.xml.model.Person;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Diver implements IDiver {
    @XmlAttribute
    String id;

    @XmlElement(name = "personal")
    Person person;

    @XmlElement
    Equipment equipment = new Equipment();

    public Diver(String name) {
        person = new Person();
        person.setFirstname(name.replaceAll(" .*", ""));
        person.setLastname(name.replaceFirst("\\w+\\s+", ""));

        id = XmlUtil.generateId();
    }

    public Diver() {}
}

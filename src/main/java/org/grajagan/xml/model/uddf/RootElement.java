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
import org.grajagan.xml.model.IDiver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "uddf")
@XmlAccessorType(XmlAccessType.FIELD)
public class RootElement {
    @XmlAttribute
    private static final String version = "3.2.1";

    @XmlElement
    private Generator generator;

    @XmlElementWrapper(name = "diver")
    @XmlAnyElement
    private List<IDiver> divers;

    @XmlElement
    DiveSite divesite;

    @XmlElementWrapper(name = "gasdefinitions")
    @XmlAnyElement
    private List<Mix> mixList;

    @XmlElementWrapper(name = "profiledata")
    @XmlAnyElement
    private List<RepetitionGroup> repetitionGroups;
}

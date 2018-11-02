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

import org.apache.commons.io.FilenameUtils;
import org.grajagan.xml.model.Person;

import java.io.File;

public interface JTrak2XMLConverter {
    void export(File outputXMLFile, Person owner);
    String getSupportedFormat();
    String getFileExtension();

    static File setExtension(File file, String extension) {
        return new File(file.getParentFile(),
                FilenameUtils.getBaseName(file.getName()) + "." + extension);
    }
}

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

import de.frobese.jtrak.model.LogbuchManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;

public abstract class AbstractJTrak2XMLConverter implements JTrak2XMLConverter {
    private static final Logger logger = LogManager.getLogger(AbstractJTrak2XMLConverter.class);

    private static LogbuchManager logbuchManager;

    public LogbuchManager getLogbuchManager() {
        if (logbuchManager == null) {
            try {
                logbuchManager = LogbuchManager.createLogbuchManager();
            } catch (IOException e) {
                logger.fatal("Cannot open the JTrak log book", e);
                System.exit(1);
            }

            logger.info("Found " + logbuchManager.getProfile(0).getTauchgangList().size()
                    + " dives to export");
        }

        return logbuchManager;
    }
}

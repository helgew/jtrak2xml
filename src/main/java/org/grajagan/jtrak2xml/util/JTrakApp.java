package org.grajagan.jtrak2xml.util;

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

import de.frobese.jtrak.JTrakConstants;
import de.frobese.jtrak.gui.MainFrame;
import de.frobese.jtrak.gui.StartPane;
import de.frobese.jtrak.logging.JTrakLog;
import de.frobese.jtrak.model.LogbuchManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/*
A simple wrapper to run JTrak within your favorite IDE.
 */
public class JTrakApp {
    private static Logger logger = LogManager.getLogger(JTrakApp.class);

    public static void main(String[] args) {
        try {
            JTrakConstants.init();
            JTrakLog.initLog();
            StartPane sp = new StartPane();
            LogbuchManager lv_manager = LogbuchManager.createLogbuchManager();
            new MainFrame(lv_manager);
            sp.setVisible(false);
        } catch (Exception e) {
            logger.fatal(e.getMessage(), e);
        }
    }
}

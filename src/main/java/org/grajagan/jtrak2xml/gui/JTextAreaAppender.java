package org.grajagan.jtrak2xml.gui;

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
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import java.util.ArrayList;

@Getter
public class JTextAreaAppender extends AppenderSkeleton {
    JTextArea logConsole;

    public JTextAreaAppender() {
        logConsole = new JTextArea(5, 50);
    }

    @Override
    protected void append(LoggingEvent event) {
        // we can't seem to prevent JTrak from logging otherwise
        if (event.getLoggerName().startsWith("org.grajagan") || event.getLevel()
                .isGreaterOrEqual(Level.WARN)) {

            event.getLocationInformation();
            logConsole.append(getLayout().format(event));

            if (!layout.ignoresThrowable()) {
                String[] s = event.getThrowableStrRep();
                if (s != null) {
                    int len = s.length;
                    for (int i = 0; i < len; i++) {
                        logConsole.append(s[i]);
                        logConsole.append(Layout.LINE_SEP);
                    }
                }
            }
        }
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

}

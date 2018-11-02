package org.grajagan.jtrak2xml;

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

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.grajagan.jtrak2xml.converters.JTrak2XMLConverter;
import org.grajagan.jtrak2xml.gui.JTextAreaAppender;
import org.grajagan.jtrak2xml.gui.StartUpDialog;
import org.grajagan.xml.model.Person;
import org.reflections.Reflections;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JTrak2XML implements PropertyChangeListener, Runnable {
    private static final Logger logger = Logger.getLogger(JTrak2XML.class);
    private static JTextAreaAppender appender;

    static {
        Enumeration<Appender> e = logger.getParent().getAllAppenders();
        while (e.hasMoreElements()) {
            Appender a = e.nextElement();
            if (a instanceof JTextAreaAppender) {
                appender = (JTextAreaAppender) a;
            } else {
                logger.getParent().removeAppender(a.getName());
            }
        }
    }

    ;

    StartUpDialog dialog;

    final Map<String, JTrak2XMLConverter> converterMap = new HashMap<>();

    private void init() {
        Reflections reflections = new Reflections("org.grajagan.jtrak2xml.converters");
        Set<Class<? extends JTrak2XMLConverter>> converterClassSet =
                reflections.getSubTypesOf(JTrak2XMLConverter.class);
        for (Class<? extends JTrak2XMLConverter> clazz : converterClassSet) {
            if (!Modifier.isAbstract(clazz.getModifiers())) {
                try {
                    JTrak2XMLConverter converter = clazz.newInstance();
                    converterMap.put(converter.getSupportedFormat(), converter);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private void addToClassPath(File file) throws Exception {
        URL u = file.toURI().toURL();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[] { u });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new JTrak2XML());
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String prop = event.getPropertyName();

        JOptionPane optionPane = dialog.getOptionPane();

        if (dialog.isVisible() && (event.getSource() == optionPane) && (
                JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY
                        .equals(prop))) {

            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            // Reset the JOptionPane's value.
            // If you don't do this, then if the user
            // presses the same button next time, no
            // property change event will be fired.
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

            if (dialog.CONVERT_BUTTON_PRESSED.equals(value)) {
                String firstName = dialog.getFirstNameField().getText();
                String lastName = dialog.getLastNameField().getText();

                JTrak2XMLConverter converter = null;
                String selectedFormat = dialog.getFormatBox().getSelectedItem().toString();
                for (String knownFormat : converterMap.keySet()) {
                    if (knownFormat.equals(selectedFormat)) {
                        converter = converterMap.get(knownFormat);
                        break;
                    }
                }

                // this should not happen ever
                if (converter == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a valid format.\n",
                            "Try again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Person person = new Person();
                person.setFirstname(firstName);
                person.setLastname(lastName);

                try {
                    addToClassPath(dialog.getJTrakJarFile());
                } catch (Exception e) {
                    logger.fatal("Cannot add JTrak.app's Java directory to classpath", e);
                    System.exit(1);
                }

                Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
                dialog.setCursor(waitCursor);

                converter.export(dialog.getOutputFile(), person);

                for (LoggingEvent e : appender.getEventsList()) {
                    System.out.print(appender.getLayout().format(e));
                }

                System.exit(0);
            } else { //user closed dialog or clicked cancel
                System.exit(0);
            }
        }
    }

    @Override
    public void run() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            logger.error("Cannot set look and feel properly.", ex);
        }

        init();

        UIManager.put("swing.boldMetal", Boolean.FALSE);
        JFrame frame = new JFrame("JTrak2XML");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        dialog = new StartUpDialog(frame, converterMap.keySet());
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.getOptionPane().addPropertyChangeListener(this);
        dialog.setVisible(true);
    }
}

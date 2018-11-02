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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

public class StartUpDialog extends JDialog {

    private static final Logger logger = LogManager.getLogger(StartUpDialog.class);

    public final static String CONVERT_BUTTON_PRESSED = "the convert button was pressed";

    private final static String INPUT_HAS_CHANGED = "input has changed";
    private final static String FIRST_NAME_PREF = "first name";
    private final static String LAST_NAME_PREF = "last name";
    private final static String FORMAT_PREF = "xml format";
    private final static String JTRAK_LOCATION_PREF = "JTrak location";

    @Getter
    JOptionPane optionPane;

    @Getter
    private JTextField firstNameField;

    @Getter
    private JTextField lastNameField;

    @Getter
    private JComboBox<String> formatBox;

    @Getter
    private File jTrakJarFile;

    @Getter
    private File outputFile;

    private JTextField appLocationField;
    private JTextField outputFileField;

    private String convertButtonText = "Convert";
    private String cancelButtonText = "Cancel";

    private final JButton convertButton;
    private final JButton cancelButton;

    public StartUpDialog(final Frame frame, Set<String> converterFormats) {
        super(frame, true);
        setTitle("JTrak2XML");

        DocumentListener changeListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                checkForm();
            }

            public void removeUpdate(DocumentEvent e) {
                checkForm();
            }

            public void insertUpdate(DocumentEvent e) {
                checkForm();
            }
        };

        firstNameField = new JTextField(20);

        lastNameField = new JTextField(20);

        appLocationField = new JTextField(20);
        appLocationField.setEditable(false);

        outputFileField = new JTextField(20);
        outputFileField.setEditable(false);

        formatBox = new JComboBox<>(converterFormats.toArray(new String[0]));

        populateWithPreferences();

        firstNameField.getDocument().addDocumentListener(changeListener);
        lastNameField.getDocument().addDocumentListener(changeListener);

        JComponent[] fields =
                { firstNameField, lastNameField, formatBox, appLocationField, outputFileField };

        for (JComponent field : fields) {
            if (field instanceof JTextField) {
                ((JTextField) field).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        optionPane.setValue(INPUT_HAS_CHANGED);
                    }
                });
            }
        }

        String[] labelTexts = { "First Name:", "Last Name:", "Format:" };
        JLabel[] labels = new JLabel[labelTexts.length];
        for (int i = 0; i < labelTexts.length; i++) {
            labels[i] = new JLabel(labelTexts[i]);
        }

        addMnemonics(labels, fields);

        final JButton getAppButton = new JButton("Locate JTrak.app");
        getAppButton.setVerticalTextPosition(AbstractButton.CENTER);
        getAppButton.setHorizontalTextPosition(AbstractButton.CENTER);
        getAppButton.setMnemonic(KeyEvent.VK_L);

        getAppButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findJTrakApp();
            }
        });

        JButton outFileButton = new JButton("Save To...");
        outFileButton.setVerticalTextPosition(AbstractButton.CENTER);
        outFileButton.setHorizontalTextPosition(AbstractButton.CENTER);
        outFileButton.setMnemonic(KeyEvent.VK_S);

        outFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setOutfile();
            }
        });

        JComponent[] leftColumn = new JComponent[labels.length + 2];
        for (int i = 0; i < labels.length; i++) {
            leftColumn[i] = labels[i];
        }

        leftColumn[labels.length] = getAppButton;
        leftColumn[labels.length + 1] = outFileButton;

        JComponent labelsAndFields = getTwoColumnLayout(leftColumn, fields);
        JComponent form = new JPanel(new BorderLayout(5, 8));

        JComponent panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 5));
        panel.add(new JLabel("<html><body>JTrak2XML converts all your dives to an XML format "
                        + "<br/>compatible with MacDive and other software.</body></html>", JLabel.LEFT),
                BorderLayout.NORTH);
        panel.add(new JLabel(" "), BorderLayout.CENTER);
        panel.add(new JLabel("<html><body>Please locate the JTrak app, enter the dive log "
                + "owner's name, <br/>pick a format, and specify the output file.</body></html>",
                JLabel.LEFT), BorderLayout.SOUTH);

        form.add(panel, BorderLayout.PAGE_START);
        form.add(labelsAndFields, BorderLayout.CENTER);

        //Create an array specifying the number of dialog buttons
        //and their text.
        convertButton = new JButton(convertButtonText);
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionPane.setValue(CONVERT_BUTTON_PRESSED);
            }
        });
        convertButton.setEnabled(false);

        cancelButton = new JButton(cancelButtonText);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionPane.setValue(cancelButton);
            }
        });

        Object[] buttons = { convertButton, cancelButton };

        // Create the JOptionPane.
        optionPane = new JOptionPane(form, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION,
                null, buttons, buttons[0]);

        // Make this dialog display it.
        setContentPane(optionPane);

        // Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                optionPane.setValue(JOptionPane.CLOSED_OPTION);
            }
        });

        // Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                if (StringUtils.isEmpty(firstNameField.getText())) {
                    firstNameField.requestFocusInWindow();
                }
            }
        });
    }

    private void populateWithPreferences() {
        Preferences preferences = Preferences.userNodeForPackage(getClass());
        firstNameField.setText(preferences.get(FIRST_NAME_PREF, ""));
        lastNameField.setText(preferences.get(LAST_NAME_PREF, ""));

        String pref = preferences.get(FORMAT_PREF, "");
        if (!StringUtils.isEmpty(pref)) {
            for (int i = 0; i < formatBox.getItemCount(); i++) {
                String item = formatBox.getItemAt(i);
                if (pref.equals(item)) {
                    formatBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        pref = preferences.get(JTRAK_LOCATION_PREF, "/Applications/JTrak.app");
        if (checkJTrakLocation(new File(pref))) {
            appLocationField.setText(pref);
        }
    }

    private void setPreferences() {
        Preferences preferences = Preferences.userNodeForPackage(getClass());
        preferences.put(FIRST_NAME_PREF, firstNameField.getText());
        preferences.put(LAST_NAME_PREF, lastNameField.getText());
        preferences.put(FORMAT_PREF, formatBox.getSelectedItem().toString());
        preferences.put(JTRAK_LOCATION_PREF, appLocationField.getText());
        try {
            preferences.sync();
        } catch (Exception e) {
            logger.error("Cannot sync preferences", e);
        }
    }

    private boolean checkJTrakLocation(File file) {
        jTrakJarFile = "jar".equals(FilenameUtils.getExtension(file.getName())) ? file :
                new File(file, "Contents/Resources/Java/JTrak.jar");

        if (!jTrakJarFile.exists() || !jTrakJarFile.canRead()) {
            jTrakJarFile = null;
            return false;
        }

        logger.debug("Found JTrak.jar at " + jTrakJarFile.getAbsolutePath());
        return true;
    }

    private JComponent getTwoColumnLayout(JComponent[] labels, JComponent[] fields) {
        if (labels.length != fields.length) {
            throw new IllegalArgumentException("All arrays must be of the same length!");
        }

        JComponent panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        // Turn on automatically adding gaps between components
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        //Horizontal arrangement
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

        GroupLayout.ParallelGroup labelGroup =
                layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
        for (JComponent label : labels) {
            labelGroup.addComponent(label);
        }
        hGroup.addGroup(labelGroup);

        GroupLayout.ParallelGroup fieldGroup =
                layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        for (JComponent field : fields) {
            fieldGroup.addComponent(field);
        }
        hGroup.addGroup(fieldGroup);

        layout.setHorizontalGroup(hGroup);

        layout.linkSize(SwingConstants.HORIZONTAL, labels);

        //Vertical arrangement
        GroupLayout.Group vGroup = layout.createSequentialGroup();
        layout.setVerticalGroup(vGroup);

        for (int i = 0; i < labels.length; i++) {
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labels[i]).addComponent(fields[i]));
        }

        return panel;
    }

    private void addMnemonics(JLabel[] labels, JComponent[] fields) {
        Map<Character, Object> m = new HashMap<>();
        for (int ii = 0; ii < labels.length; ii++) {
            labels[ii].setLabelFor(fields[ii]);
            String lwr = labels[ii].getText().toLowerCase();
            for (int jj = 0; jj < lwr.length(); jj++) {
                char ch = lwr.charAt(jj);
                if (m.get(ch) == null && Character.isLetterOrDigit(ch)) {
                    m.put(ch, ch);
                    labels[ii].setDisplayedMnemonic(ch);
                    break;
                }
            }
        }
    }

    private void findJTrakApp() {
        JFileChooser fileChooser = new JFileChooser("/Applications");

        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Applications", "app");
        fileChooser.addChoosableFileFilter(fileFilter);
        fileChooser.setFileFilter(fileFilter);

        fileChooser.setDialogTitle("Where is the JTrak.app?");

        int returnValue = fileChooser.showOpenDialog(optionPane);
        File file = null;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        } else {
            return;
        }

        if (checkJTrakLocation(file)) {
            appLocationField.setText(file.getAbsolutePath());
        } else {
            // TODO
        }
        checkForm();
    }

    private void setOutfile() {
        JFileChooser fileChooser = new JFileChooser();
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        fileChooser.setCurrentDirectory(desktop);

        int returnVal = fileChooser.showSaveDialog(optionPane);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            outputFile = fileChooser.getSelectedFile();
        } else {
            return;
        }

        try {
            if ((outputFile.exists() && !outputFile.canWrite()) || (!outputFile.exists()
                    && !outputFile.getParentFile().createNewFile())) {
                outputFile = null;
                // TODO
                return;
            }
        } catch (Exception e) {
            logger.warn("Cannot check if output file can be created!", e);
        }

        logger.debug("Set output file to " + outputFile.getAbsolutePath());
        outputFileField.setText(
                outputFile.getAbsolutePath().replace(System.getProperty("user.home"), "~"));
        checkForm();
    }

    private void checkForm() {
        if (!StringUtils.isEmpty(firstNameField.getText()) && !StringUtils
                .isEmpty(lastNameField.getText()) && jTrakJarFile != null && outputFile != null) {
            setPreferences();
            convertButton.setEnabled(true);
        } else {
            convertButton.setEnabled(false);
        }
    }
}

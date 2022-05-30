package GUI.panels;

import GUI.ButtonType;
import GUI.listener.ActionFactory;
import GUI.listener.HashListener;
import GUI.buttons.ButtonFactory;
import GUI.buttons.ProgramButton;

import javax.swing.*;
import java.awt.*;

public class HashInputPanel extends ProgramPanel {

    private final ButtonFactory buttonFactory;

    private final ActionFactory actionFactory;

    private final JLabel[] panelLabels = new JLabel[3];
    private final JTextField[] panelTextFields = new JTextField[3];

    private final ProgramButton[] panelButtons = new ProgramButton[2];
    private final JTextArea panelTextArea;

    HashInputPanel() {
        buttonFactory = new ButtonFactory();
        actionFactory = new ActionFactory();
        panelTextArea = new JTextArea();
        setPanelDetails();
    }
    /**
     * Sets attribute details for the respective panel.
     */
    @Override
    void setPanelDetails() {
        setLayout(null);
        setBackground(new Color(100, 100, 100));
        initLabelsAndTextFields();
        setPassphraseComponents();
        setCustomStrComponents();
        setBitLengthComponents();
        setGUIInputComponents();
        initProgramButtons();
        addComponents();
    }

    private void initLabelsAndTextFields() {
        for (int i = 0; i < panelLabels.length; i++) {
            panelLabels[i] = new JLabel();
            panelTextFields[i] = new JTextField();
        }
    }

    private void initProgramButtons() {
        // index 0: GUI.buttons.FileSelectButton
        // index 1: GUI.buttons.HashButton
        panelButtons[0] = buttonFactory.makeProgramButton(ButtonType.SELECT_FILE);
        final var selectFileList = actionFactory.makeProgramListener(ButtonType.SELECT_FILE);
        // TODO share needed components
        panelButtons[0].addActionListener(selectFileList);
        panelButtons[0].setBounds(0, 100, 100, 25);

        panelButtons[1] = buttonFactory.makeProgramButton(ButtonType.HASH);
        final HashListener hashListener = (HashListener) actionFactory.makeProgramListener(ButtonType.HASH);
        hashListener.setPassphrase(panelTextFields[0]);
        hashListener.setCustomStr(panelTextFields[1]);
        hashListener.setBitLength(panelTextFields[2]);
        hashListener.setUserInput(panelTextArea);
        panelButtons[1].addActionListener(hashListener);
        panelButtons[1].setBounds(0, 150, 100, 25);

    }

    private void setPassphraseComponents() {
        // index 0
        var label = panelLabels[0];
        var textField = panelTextFields[0];

        label.setText("Passphrase:");
        label.setBounds(0, 0, 80, 25);

        textField.setBounds(75, 0, 100, 25);
    }

    private void setCustomStrComponents() {
        // index 1
        var label = panelLabels[1];
        var textField = panelTextFields[1];

        label.setText("Custom Str:");
        label.setBounds(0, 25, 80, 25);

        textField.setBounds(75, 25, 100, 25);

    }

    private void setBitLengthComponents() {
        // index 2
        var label = panelLabels[2];
        var textField = panelTextFields[2];

        label.setText("Bit Length:");
        label.setBounds(0, 50, 80, 25);

        textField.setBounds(75, 50, 100, 25);

    }

    private void setGUIInputComponents() {
        // GUI input
        final var inputLabel = new JLabel("User Input");
        inputLabel.setBounds(300, 0, 100, 25);
        add(inputLabel);

        panelTextArea.setLineWrap(true);
        panelTextArea.setWrapStyleWord(true);

        final var scroll = new JScrollPane(panelTextArea);
        scroll.setBounds(180, 25, 302, 175);
        add(scroll);
    }

    private void addComponents() {
        for (int i = 0; i < panelLabels.length; i++) {
            add(panelLabels[i]);
            add(panelTextFields[i]);
        }

        // TODO add buttons
        for (ProgramButton panelButton : panelButtons) {
            add(panelButton);
        }

    }




}

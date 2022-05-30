package GUI.panels;

import GUI.listener.ActionFactory;
import GUI.PanelType;
import GUI.buttons.ButtonFactory;
import GUI.ButtonType;
import GUI.buttons.ButtonFactory;

import javax.swing.*;
import java.awt.*;

class InputPanel extends ProgramPanel {

    private final ActionFactory actionFactory;
    private final ButtonFactory buttonFactory;
    private final PanelFactory panelFactory;

    private final ProgramPanel hashInputPanel1;
    public InputPanel() {
        actionFactory = new ActionFactory();
        buttonFactory = new ButtonFactory();
        panelFactory = new PanelFactory();
        hashInputPanel1 = panelFactory.makeProgramPanel(PanelType.HASH_INPUT);
        setPanelDetails();
    }

    /**
     * Sets attribute details for the respective panel.
     */
    @Override
    void setPanelDetails() {
        setBackground(new Color(123, 0, 123));
        setBounds(0, 0, 490, 238);
        setLayout(new BorderLayout());

        buildModePanel();
        addSubInputPanels();

        // TODO set this attributes to be access by controller

    }

    private void buildModePanel() {
        final var modePanel = new JPanel(); // TODO might be a class that extends GUI.Program Panel

        final var hashModeButton = buttonFactory.makeProgramButton(ButtonType.HASH_MODE); // TODO might have to pass different input panels
        hashModeButton.addActionListener(actionFactory.makeProgramListener(ButtonType.HASH_MODE));

        final var encryptModeButton = buttonFactory.makeProgramButton(ButtonType.HASH_MODE); // TODO temp
        final var decryptModeButton = buttonFactory.makeProgramButton(ButtonType.HASH_MODE); // TODO temp

        modePanel.add(hashModeButton);
        modePanel.add(encryptModeButton);
        modePanel.add(decryptModeButton);

        add(modePanel, BorderLayout.NORTH);

    }

    private void addSubInputPanels() {
        // TODO make necessary components accessible, in this case HashModeListener

        add(hashInputPanel1, BorderLayout.CENTER);
    }
    // TODO needs way to switch between modes
}

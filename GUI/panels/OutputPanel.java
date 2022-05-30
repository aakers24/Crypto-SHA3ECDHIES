package GUI.panels;

import java.awt.*;

class OutputPanel extends ProgramPanel {

    public OutputPanel() {
        setPanelDetails();
    }

    /**
     * Sets attribute details for the respective panel.
     */
    @Override
    void setPanelDetails() {
        setBackground(new Color(123, 0, 250));
        setBounds(0, 238, 490, 225);

    }
}

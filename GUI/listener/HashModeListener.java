package GUI.listener;

import java.awt.event.ActionEvent;

public class HashModeListener extends ProgramListener {
    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Hash mode was clicked");
    }
}

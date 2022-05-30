package GUI.listener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class FileSelectListener extends ProgramListener {
    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        // TODO change functionality for different file selects if necessary example below
        // if (e.getSource() == aButton) { code }

        final var fileChooser = new JFileChooser();
        final int response = fileChooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION){
            final var file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            System.out.println("Implement file action for Hash");
        }
    }
}

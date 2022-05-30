package GUI.listener;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HashListener extends ProgramListener {

    private JTextField passphrase;
    private JTextField customStr;
    private JTextField bitLength;
    private JTextArea userInput;

    HashListener() {}
    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        System.out.println("Passphrase: " + passphrase.getText());
        System.out.println("Custom String: " + customStr.getText());
        System.out.println("Bit Length: " + bitLength.getText());
        System.out.println("User input: " + userInput.getText());

    }

    public void setPassphrase(JTextField passphrase) {
        this.passphrase = passphrase;
    }

    public void setCustomStr(JTextField customStr) {
        this.customStr = customStr;
    }

    public void setBitLength(JTextField bitLength) {
        this.bitLength = bitLength;
    }

    public void setUserInput(JTextArea userInput) {
        this.userInput = userInput;
    }
}

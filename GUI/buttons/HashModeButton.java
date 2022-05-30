package GUI.buttons;

/**
 * Changes the input portion of the GUI to whats needed to conduct the Cryptographic hash
 * of a given file or user input.
 */
public class HashModeButton extends ProgramButton {

    HashModeButton() {
        setButtonDetails();
    }



    /**
     * Sets the details of the button.
     */
    @Override
    void setButtonDetails() {
        setText("Crypto Hash");
    }



}

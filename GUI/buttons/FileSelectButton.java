package GUI.buttons;

public class FileSelectButton extends ProgramButton {
    FileSelectButton() {
        setButtonDetails();
    }
    /**
     * Sets the details of the button.
     */
    @Override
    void setButtonDetails() {
        setText("File Input");
    }
}

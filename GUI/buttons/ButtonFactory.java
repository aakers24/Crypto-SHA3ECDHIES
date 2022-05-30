package GUI.buttons;

import GUI.ButtonType;

public class ButtonFactory {

    public ProgramButton makeProgramButton(final ButtonType type) {
        ProgramButton output = null;
        switch (type) {
            case HASH_MODE:
                output = getHashModeButton();
                break;
            case HASH:
                output = getHashButton();
                break;
            case ENCRYPT_MODE:
                System.out.println("button");
                break;
            case DECRYPT_MODE:
                System.out.println("other button");
                break;
            case SELECT_FILE:
                output = getFileSelectButton();
                break;
            default:
                System.err.println("Improper argument in GUI.buttons.ButtonFactory.makeProgramButton()");
                break;
        }
        return output;
    }

// TODO remove private methods and just assign the object

    private ProgramButton getHashModeButton () {
        return new HashModeButton();
    }

    private ProgramButton getFileSelectButton() {
        return new FileSelectButton();
    }

    private ProgramButton getHashButton() {
        return new HashButton();
    }
}

package GUI.listener;

import GUI.ButtonType;

/**
 * Makes the action listeners need for the program.
 */
public class ActionFactory {


    public ProgramListener makeProgramListener(final ButtonType type) {
        ProgramListener output = null;
        switch (type) {
            case HASH_MODE:
                output = new HashModeListener();
                break;
            case HASH:
                output = new HashListener();
                break;
            case SELECT_FILE:
                output = new FileSelectListener();
                break;
            default:
                System.err.println("ActionFactory failed in makeProgramListener()");
                break;
        }
        return output;
    }

}
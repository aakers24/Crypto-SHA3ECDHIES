package GUI.panels;

import GUI.PanelType;

/**
 * Builds the necessary panels that makes the User Interface
 */
public class PanelFactory {


    /**
     * Selects the requested panel
     * @param type of panel
     * @return the requested panel
     */
    public ProgramPanel makeProgramPanel(final PanelType type) {
        ProgramPanel output = null;
        switch (type){
            case HOUSING:
                output = getHousingPanel();
                break;
            case INPUT:
                output = getInputPanel();
                break;
            case OUTPUT:
                output = getOutputPanel();
                break;
            case HASH_INPUT:
                output = getHashInputPanel();
                break;
            default:
                System.err.println("Improper argument in PanelBuilder.getProgramPanel()");
                break;
        }
        return output;
    }

    private ProgramPanel getHousingPanel() {
        return new HousingPanel();
    }
    private ProgramPanel getInputPanel() {
        return new InputPanel();
    }
    private ProgramPanel getOutputPanel() {
        return new OutputPanel();
    }

    private ProgramPanel getHashInputPanel() {
        return new HashInputPanel();
    }
}

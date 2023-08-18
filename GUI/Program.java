package GUI;

import GUI.panels.PanelFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Entry point for the User Interface
 */
class Program extends JFrame {

    private final PanelFactory panelFactory;

    /**
     * Constructor to instantiate the UI
     */
    private Program() {
        panelFactory = new PanelFactory();
        buildUI();
    }

    private void buildUI() {
        frameDetails();
        addPanels();
    }

    private void frameDetails() {
        setVisible(true);
        setSize(500, 500);
        setTitle("Practical Cryptographic App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        final var image = new ImageIcon("src/res/triforce.png");
        setIconImage(image.getImage());
    }

    private void addPanels() {
        // TODO de-couple creation and adding the components
        final var housingP = panelFactory.makeProgramPanel(PanelType.HOUSING);
        add(housingP, BorderLayout.CENTER);

        final var inputP = panelFactory.makeProgramPanel(PanelType.INPUT);
        housingP.add(inputP);

        final var outputP = panelFactory.makeProgramPanel(PanelType.OUTPUT);
        housingP.add(outputP);
    }

    /**
     * Point of entry to run the program
     * @param args N/A
     */
    public static void main(final String[] args) {
        final var program = new Program();
        program.buildUI();

    }
}

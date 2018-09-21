package ui;

import implementation.logic.SimulationLogic;
import parsing.Parser;
import ui.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class StartScreen {
    private JFrame form;

    private JPanel pnlRootPanel;
    private JLabel lblHeader;
    private JLabel lblFooter;
    private JPanel pnlCenterPanel;

    private JTextField txtFileName;
    private JButton btnSelect;
    private JButton btnStart;

    private File selectedFile;

    public StartScreen(JFrame form) {
        this.form = form;
        createMainPart();
    }

    private void createMainPart() {
        pnlCenterPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(Constants.INSETS_VALUE, Constants.INSETS_VALUE, Constants.INSETS_VALUE, Constants.INSETS_VALUE);

        constraints.gridx = 0;
        constraints.gridy = 0;
        txtFileName = new JTextField(Constants.FILE_NAME_COLUMNS_NUMBER);
        txtFileName.setEditable(false);
        pnlCenterPanel.add(txtFileName, constraints);


        btnStart = new JButton(Constants.BUTTON_START_TEXT);

        constraints.gridx++;
        btnSelect = new JButton(Constants.BUTTON_SELECT_TEXT);
        btnSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(pnlCenterPanel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fc.getSelectedFile();
                    txtFileName.setText(selectedFile.getName());
                    btnStart.setEnabled(true);
                }
            }
        });
        pnlCenterPanel.add(btnSelect, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        btnStart.setEnabled(false);
        btnStart.addActionListener(e -> {
            try {
                Parser.init(selectedFile);
                if (Parser.parseFile()) {

                    SimulationLogic simulationLogic = new SimulationLogic();
                    simulationLogic.initSimulation(Parser.getDescriptors(), Parser.getQueueDescriptors(), Parser.getSimulationDescriptor());

                    Scheme scheme = new Scheme();
                    if (scheme.init(simulationLogic)) {
                        scheme.setExtendedState(scheme.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                        scheme.setResizable(false);
                        scheme.setVisible(true);
                    } else {
                        Scheme.handleSimulationStart(simulationLogic);
                    }

                    form.dispose();
                } else {
                    JOptionPane.showMessageDialog(form, Constants.PARSING_ERROR);
                }
            } catch (Exception ee) {
                ee.printStackTrace();
                JOptionPane.showMessageDialog(form, Constants.PARSING_FATAL_ERROR);
            }
        });
        pnlCenterPanel.add(btnStart, constraints);
    }

    public static void displayStartScreen() {
        JFrame startScreen = new JFrame(Constants.START_SCREEN_TITLE);
        startScreen.setPreferredSize(new Dimension(Constants.START_SCREEN_WIDTH, Constants.START_SCREEN_HEIGHT));
        startScreen.setResizable(false);
        startScreen.setContentPane(new StartScreen(startScreen).pnlRootPanel);
        startScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startScreen.pack();
        startScreen.setLocationRelativeTo(null);
        startScreen.setVisible(true);
    }

    public static void main(String[] args) {
        displayStartScreen();
    }
}
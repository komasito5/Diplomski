package ui;
import implementation.logic.SimulationLogic;
import implementation.components.Component;
import ui.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class Scheme extends JFrame {

    private Surface surface;
    private JButton startSimulation;
    private JButton back;

    public boolean init(SimulationLogic simulationLogic) {
        Map<String, Component> components = simulationLogic.getComponents();

        setLayout(new GridBagLayout());
        GridBagConstraints c  = new GridBagConstraints();

        JPanel schemePanel = new JPanel();
        schemePanel.setLayout(new CardLayout());

        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.95;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(schemePanel), c);

        surface = new Surface();
        schemePanel.add(surface);
        boolean canBeDrawn = surface.init(components);

        if (!canBeDrawn) {
            return false;
        }

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.05;
        c.weightx = 1;

        JPanel bottomPanel = new JPanel();
        add(bottomPanel, c);
        bottomPanel.setLayout(new GridBagLayout());

        startSimulation = new JButton(Constants.START_SIMULATION);
        startSimulation.addActionListener(e -> {
            Scheme.this.dispose();
            Scheme.handleSimulationStart(simulationLogic);
        });

        back = new JButton(Constants.BACK);
        back.addActionListener(e -> {
            Scheme.this.dispose();
            StartScreen.displayStartScreen();
        });

        bottomPanel.add(startSimulation, new GridBagConstraints());
        bottomPanel.add(Box.createRigidArea(new Dimension(Constants.RIGID_AREA_WIDTH, Constants.RIGID_AREA_HEIGHT)));
        bottomPanel.add(back, new GridBagConstraints());

        setTitle(Constants.SCHEME_TITLE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return true;
    }

    public static void handleSimulationStart(SimulationLogic simulationLogic) {
        Thread simulationThread = new Thread(() -> simulationLogic.startSimulation());
        simulationThread.start();

        ProgressBarPanel panel = new ProgressBarPanel();
        JFrame progressFrame = new JFrame();
        progressFrame.setUndecorated(true);
        progressFrame.setContentPane(panel);
        progressFrame.pack();
        progressFrame.setLocationRelativeTo(null);
        progressFrame.setVisible(true);

        Thread updateThread = new Thread() {
            @Override
            public void run() {
                int completed = simulationLogic.getCurrentPercentage();
                while (completed < Constants.PROGRESS_BAR_MAX_VALUE) {
                    panel.updateBar(completed);
                    try {
                        Thread.sleep(Constants.PROGRESS_BAR_THREAD_SLEEP_DURATION);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    completed = simulationLogic.getCurrentPercentage();
                }

                panel.updateBar(Constants.PROGRESS_BAR_MAX_VALUE);
                try {
                    Thread.sleep(Constants.PROGRESS_BAR_THREAD_SLEEP_DURATION_END);
                    progressFrame.dispose();
                    simulationLogic.showSimulationResults();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        updateThread.start();
    }

    public static class ProgressBarPanel extends JPanel {
        JProgressBar pbar;
        static final int MY_MINIMUM = Constants.PROGRESS_BAR_MIN_VALUE;
        static final int MY_MAXIMUM = Constants.PROGRESS_BAR_MAX_VALUE;

        public ProgressBarPanel() {
            pbar = new JProgressBar();
            pbar.setMinimum(MY_MINIMUM);
            pbar.setMaximum(MY_MAXIMUM);
            pbar.setStringPainted(true);
            add(pbar);
        }

        public void updateBar(int newValue) {
            pbar.setValue(newValue);
        }
    }

}

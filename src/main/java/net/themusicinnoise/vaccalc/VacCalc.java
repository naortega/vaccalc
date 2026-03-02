package net.themusicinnoise.vaccalc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.YearMonth;
import java.util.stream.Collectors;

public class VacCalc extends JFrame {
    private CalendarPanel calendarPanel;
    private JLabel monthLabel;
    private JLabel pointsLabel;
    private PointEngine pointEngine;

    public VacCalc() {
        setTitle("VacCalc");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu appMenu = new JMenu("VacCalc");
        JMenuItem importItem = new JMenuItem("Import points");
        importItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                JFileChooser fileChooser = new JFileChooser();
                int ret = fileChooser.showOpenDialog(VacCalc.this);
                if(ret == JFileChooser.APPROVE_OPTION) {
                    try {
                        pointEngine.importPointsFile(fileChooser.getSelectedFile());
                    } catch(RuntimeException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(VacCalc.this, ex.getMessage(), "Parsing Error",
                                JOptionPane.ERROR_MESSAGE);
                        pointEngine.reset();
                    }
                }
                calendarPanel.repaint();
            }
        });
        appMenu.add(importItem);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        appMenu.add(exitItem);
        menuBar.add(appMenu);
        JMenu helpMenu = new JMenu("Help");
        JMenuItem manualItem = new JMenuItem("Usage Manual");
        manualItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("manual.html")));
                String manualText = br.lines().collect(Collectors.joining());
                JEditorPane textArea = new JEditorPane("text/html", manualText);
                textArea.setEditable(false);
                JFrame manualFrame = new JFrame("VacCalc Manual");
                manualFrame.getContentPane().add(new JScrollPane(textArea));
                manualFrame.setSize(500, 500);
                manualFrame.setVisible(true);
            }
        });
        helpMenu.add(manualItem);
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("about.html")));
                String aboutText = br.lines().collect(Collectors.joining());
                JOptionPane.showMessageDialog(VacCalc.this,
                        aboutText,
                        "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        pointEngine = new PointEngine();
        calendarPanel = new CalendarPanel(pointEngine);

        JPanel headerPanel = createHeaderPanel();
        JPanel footerPanel = createFooterPanel();
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(calendarPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();

        calendarPanel.addPropertyChangeListener("selectedDates", e -> updatePointsLabel());
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(230, 230, 230));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        monthLabel = new JLabel();
        monthLabel.setFont(new Font("Arial", Font.BOLD, 18));
        monthLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton prevButton = new JButton("< Previous");
        prevButton.addActionListener(e -> {
            calendarPanel.previousMonth();
            updateMonthLabel();
        });

        JButton nextButton = new JButton("Next >");
        nextButton.addActionListener(e -> {
            calendarPanel.nextMonth();
            updateMonthLabel();
        });

        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(e -> {
            calendarPanel.setCurrentMonth(YearMonth.now());
            updateMonthLabel();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(230, 230, 230));
        buttonPanel.add(prevButton);
        buttonPanel.add(todayButton);
        buttonPanel.add(nextButton);

        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.SOUTH);

        updateMonthLabel();
        return headerPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(230, 230, 230));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pointsLabel = new JLabel();
        pointsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pointsLabel.setHorizontalAlignment(JLabel.CENTER);

        updatePointsLabel();
        footerPanel.add(pointsLabel, BorderLayout.CENTER);

        return footerPanel;
    }

    private void updateMonthLabel() {
        YearMonth current = calendarPanel.getCurrentMonth();
        monthLabel.setText(String.format("%s %d",
            current.getMonth().toString(),
            current.getYear()));
    }

    private void updatePointsLabel() {
        double points = calendarPanel.getTotalPoints();
        pointsLabel.setText(String.format("Total Points: %.3f", points));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VacCalc app = new VacCalc();
            app.setVisible(true);
        });
    }
}

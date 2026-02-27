package net.themusicinnoise.vaccalc;

import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;

public class VacCalc extends JFrame {
    private CalendarPanel calendarPanel;
    private JLabel monthLabel;

    public VacCalc() {
        setTitle("VacCalc");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        calendarPanel = new CalendarPanel();

        JPanel headerPanel = createHeaderPanel();
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(calendarPanel, BorderLayout.CENTER);

        add(mainPanel);
        pack();
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

    private void updateMonthLabel() {
        YearMonth current = calendarPanel.getCurrentMonth();
        monthLabel.setText(String.format("%s %d", 
            current.getMonth().toString(), 
            current.getYear()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VacCalc app = new VacCalc();
            app.setVisible(true);
        });
    }
}

package net.themusicinnoise.vaccalc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

public class CalendarPanel extends JPanel {
    private YearMonth currentMonth;
    private final int cellHeight = 60;
    private final int cellWidth = 80;
    private final Set<LocalDate> selectedDates = new HashSet<>();

    public CalendarPanel() {
        this.currentMonth = YearMonth.now();
        setPreferredSize(new Dimension(7 * cellWidth, 8 * cellHeight));
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LocalDate clickedDate = getDateAtPoint(e.getX(), e.getY());
                if (clickedDate != null) {
                    toggleSelection(clickedDate);
                }
            }
        });
    }

    public void previousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        repaint();
    }

    public void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        repaint();
    }

    public YearMonth getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(YearMonth month) {
        currentMonth = month;
        repaint();
    }

    public Set<LocalDate> getSelectedDates() {
        return new HashSet<>(selectedDates);
    }

    public void setSelectedDates(Set<LocalDate> dates) {
        selectedDates.clear();
        selectedDates.addAll(dates);
        firePropertyChange("selectedDates", null, selectedDates);
        repaint();
    }

    public void clearSelection() {
        selectedDates.clear();
        firePropertyChange("selectedDates", null, selectedDates);
        repaint();
    }

    private void toggleSelection(LocalDate date) {
        if (selectedDates.contains(date)) {
            selectedDates.remove(date);
        } else {
            selectedDates.add(date);
        }
        firePropertyChange("selectedDates", null, selectedDates);
        repaint();
    }

    public double getTotalPoints() {
        return selectedDates.size() * 1.0;
    }

    private LocalDate getDateAtPoint(int x, int y) {
        int row = y / cellHeight;
        int col = x / cellWidth;

        if (row == 0 || col < 0 || col >= 7 || row < 1 || row > 6) {
            return null;
        }

        LocalDate firstDay = currentMonth.atDay(1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentMonth.lengthOfMonth();

        int dayNumber = (row - 1) * 7 + col - firstDayOfWeek + 1;

        if (dayNumber < 1 || dayNumber > daysInMonth) {
            return null;
        }

        return currentMonth.atDay(dayNumber);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawDayHeaders(g2);
        drawDays(g2);
    }

    private void drawDayHeaders(Graphics2D g) {
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        g.setColor(new Color(70, 130, 180));
        g.fillRect(0, 0, 7 * cellWidth, cellHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();

        for (int i = 0; i < 7; i++) {
            String day = dayNames[i];
            int x = i * cellWidth;
            int textX = x + (cellWidth - fm.stringWidth(day)) / 2;
            int textY = (cellHeight - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(day, textX, textY);
        }
    }

    private void drawDays(Graphics2D g) {
        LocalDate firstDay = currentMonth.atDay(1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();

        int row = 1;
        int col = firstDayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            int x = col * cellWidth;
            int y = row * cellHeight;

            LocalDate cellDate = currentMonth.atDay(day);
            boolean isToday = cellDate.equals(today);
            boolean isSelected = selectedDates.contains(cellDate);

            if (isSelected && isToday) {
                g.setColor(new Color(123, 166, 180));
                g.fillRect(x, y, cellWidth, cellHeight);
            } else if (isSelected) {
                g.setColor(new Color(173, 216, 230));
                g.fillRect(x, y, cellWidth, cellHeight);
            } else if (isToday) {
                g.setColor(new Color(255, 200, 100));
                g.fillRect(x, y, cellWidth, cellHeight);
            }

            g.setColor(Color.BLACK);
            g.drawRect(x, y, cellWidth, cellHeight);

            String dayStr = String.valueOf(day);
            int textX = x + (cellWidth - fm.stringWidth(dayStr)) / 2;
            int textY = y + (cellHeight - fm.getHeight()) / 2 + fm.getAscent() - 8;
            g.drawString(dayStr, textX, textY);

            String pointsStr = "(1.0)";
            Font smallFont = new Font("Arial", Font.PLAIN, 10);
            FontMetrics smallFm = g.getFontMetrics(smallFont);
            g.setFont(smallFont);
            int pointsX = x + (cellWidth - smallFm.stringWidth(pointsStr)) / 2;
            int pointsY = textY + fm.getHeight();
            g.drawString(pointsStr, pointsX, pointsY);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }
}

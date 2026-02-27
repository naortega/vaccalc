package net.themusicinnoise.vaccalc;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;

public class CalendarPanel extends JPanel {
    private YearMonth currentMonth;
    private final int cellHeight = 60;
    private final int cellWidth = 80;

    public CalendarPanel() {
        this.currentMonth = YearMonth.now();
        setPreferredSize(new Dimension(7 * cellWidth, 8 * cellHeight));
        setBackground(Color.WHITE);
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

            if (isToday) {
                g.setColor(new Color(255, 200, 100));
                g.fillRect(x, y, cellWidth, cellHeight);
            }

            g.setColor(Color.BLACK);
            g.drawRect(x, y, cellWidth, cellHeight);

            String dayStr = String.valueOf(day);
            int textX = x + (cellWidth - fm.stringWidth(dayStr)) / 2;
            int textY = y + (cellHeight - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(dayStr, textX, textY);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }
}

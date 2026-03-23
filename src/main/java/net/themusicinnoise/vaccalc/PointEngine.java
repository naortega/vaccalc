package net.themusicinnoise.vaccalc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PointEngine {
    private double defaultPoints;

    static private class PointRule {
        private enum RuleType {
            DAY_OF_WEEK,
            MONTH,
            DATE,
        }

        static final Pattern DOW_PATTERN = Pattern.compile("dow=(sun|mon|tue|wed|thu|fri|sat)\\s+(\\d+\\.\\d+)", Pattern.CASE_INSENSITIVE);
        static final Pattern MONTH_PATTERN = Pattern.compile("m=(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\\s+(\\d+\\.\\d+)", Pattern.CASE_INSENSITIVE);
        static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})\\s+(\\d+\\.\\d+)", Pattern.CASE_INSENSITIVE);

        private RuleType type;
        private DayOfWeek dow;
        private Month mon;
        private LocalDate date;
        private double points;

        public PointRule(String def) {
            Matcher dowMatcher = DOW_PATTERN.matcher(def);
            Matcher monthMatcher = MONTH_PATTERN.matcher(def);
            Matcher dateMatcher = DATE_PATTERN.matcher(def);

            if (dowMatcher.find()) {
                this.type = RuleType.DAY_OF_WEEK;
                this.points = Double.parseDouble(dowMatcher.group(2));
                this.dow = DayOfWeek.from(DateTimeFormatter.ofPattern("EEE").parse(dowMatcher.group(1)));
            } else if (monthMatcher.find()) {
                this.type = RuleType.MONTH;
                this.points = Double.parseDouble(monthMatcher.group(2));
                this.mon = Month.from(DateTimeFormatter.ofPattern("MMM").parse(monthMatcher.group(1)));
            } else if (dateMatcher.find()) {
                this.type = RuleType.DATE;
                this.points = Double.parseDouble(dateMatcher.group(2));
                this.date = LocalDate.parse(dateMatcher.group(1), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else {
                throw new IllegalArgumentException("Invalid line: '" + def + "'");
            }
        }

        public boolean applies(LocalDate date) {
            switch (type) {
                case DAY_OF_WEEK:
                    return this.dow == date.getDayOfWeek();
                case MONTH:
                    return this.mon == date.getMonth();
                case DATE:
                    return this.date.isEqual(date);
            }
            return false;
        }

        public double getPoints() { return points; }
    }
    private List<PointRule> rules;

    public PointEngine() {
        defaultPoints = 1.0;
        rules = new ArrayList<>();
    }

    public void importPointsFile(File pointsFile) {
        Pattern defaultPattern = Pattern.compile("default\\s+(\\d+\\.\\d+)", Pattern.CASE_INSENSITIVE);

        try (BufferedReader br = new BufferedReader(new FileReader(pointsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#')
                    continue;

                Matcher defaultMatcher = defaultPattern.matcher(line);
                if (defaultMatcher.find()) {
                    defaultPoints = Double.parseDouble(defaultMatcher.group(1));
                } else {
                    rules.addFirst(new PointRule(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read points file: " + pointsFile, e);
        }
    }

    public void reset() { rules.clear(); }

    public double getPointsOfDay(LocalDate date) {
        for (PointRule rule : rules) {
            if (rule.applies(date)) {
                return rule.getPoints();
            }
        }
        return defaultPoints;
    }

    public void exportSelectedDates(File exportFile, Set<LocalDate> selectedDates) throws IOException {
        try (FileWriter writer = new FileWriter(exportFile)) {
            selectedDates.stream()
                    .sorted()
                    .forEach(date -> {
                        try {
                            writer.write(String.format("%s%n", date));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    public Set<LocalDate> importSelectedDates(File importFile) throws IOException {
        Set<LocalDate> dates = new HashSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new FileReader(importFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.charAt(0) == '#')
                    continue;

                try {
                    LocalDate date = LocalDate.parse(line, formatter);
                    dates.add(date);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format in line: '" + line + "'");
                }
            }
        }

        return dates;
    }
}

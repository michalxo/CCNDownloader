package sk.mtoth.ccnscenariodownloader;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TableGenerator {

    static final Logger logger = Logger.getLogger(ScenarioDownloader.class.toString());

    public TableGenerator(List<Campaign> campaignsList) {

        for (Campaign c : campaignsList) {
            String html = generateHTMLTable(c);

            int bracket = c.getName().indexOf("(");
            int strLen = c.getName().length() > 20 ? 20 : c.getName().length();
            String fileName = c.getName().substring(0, bracket == -1 ? strLen : bracket - 1) + ".html";
            String filePath = ScenarioDownloader.writeFile("generated_html", fileName, html);

            File htmlFile = new File(filePath);
            try {
                Desktop.getDesktop().browse(htmlFile.toURI());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String generateHTMLTable(Campaign campaign) {

        /**
         * | Scenario code | Scenario name | Expansion | Play count | Top stats | Bottom stats | Campaign | Map | scenario_url |
         */

        StringBuilder buf = new StringBuilder();
        buf.append("<html>\n<head><meta charset=\"utf-8\"></head>\n" +
                "<body>\n" +
                "<table>\n" +
                "<tr>" +
                "<th>Scenario code</th>" +
                "<th>Scenario name</th>" +
                "<th>Expansion</th>" +
                "<th>Play count</th>" +
                "<th>Top stats</th>" +
                "<th>Bottom stats</th>" +
                "<th>Campaign</th>" +
                "<th>Map</th>" +
                "<th>scenario_url</th>" +
                "</tr>\n");
        for (Scenario sc : campaign.getScenarios()) {
            WinColor color = defineBackgroundColor(sc);
            buf.append("<tr style=\"background: ").append(color.getColorCode());
            if (color.equals(WinColor.RED) || color.equals(WinColor.BLUE)) {
                buf.append("; color: #FFF;");
            }
            buf.append("\"><td>")
                    .append(sc.getScenarioCode())
                    .append("</td><td>")
                    .append(sc.getName())
                    .append("</td><td>")
                    .append(sc.getExpansion().toString())
                    .append("</td><td>")
                    .append(sc.getTotalPlaysStats())
                    .append("</td><td>")
                    .append(sc.getTopArmyStats())
                    .append("</td><td>")
                    .append(sc.getBottomArmyStats())
                    .append("</td><td>")
                    .append(campaign.getName())
                    .append("</td><td>")
                    .append("<Map-Image>")
                    .append("</td><td>")
                    .append(sc.getUrl())
                    .append("</td></tr>\n")
            ;
        }
        buf.append("</table>\n" +
                "</body>\n" +
                "</html>");
        return buf.toString();
    }

    static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    enum WinColor {
        BLACK("#000000"),
        RED("#990000"),
        DARK_R("#FF6666"),
//        DARK_R("#FF0000"),
        MEDIUM_R("#FF9999"),
        LIGHT_R("#FFCCCC"),
        WHITE("#FFFFFF"),
        LIGHT_B("#CCE5FF"),
        MEDIUM_B("#99CCFF"),
        DARK_B("#3399FF"),
        BLUE("#004C99");

        private String colorCode;

        WinColor(String colorCode) {
            this.colorCode = colorCode;
        }

        public String getColorCode() {
            return colorCode;
        }
    }

    static WinColor defineBackgroundColor(Scenario scenario) {
        int bottomWinPercStats = scenario.getBottomArmyStats(); // 100 = blue, 50 = white, 0 = red
        if (isBetween(bottomWinPercStats, 0, 29)) {
            return WinColor.RED;
        } else if (isBetween(bottomWinPercStats, 30, 39)) {
            return WinColor.DARK_R;
        } else if (isBetween(bottomWinPercStats, 40, 44)) {
            return WinColor.MEDIUM_R;
        } else if (isBetween(bottomWinPercStats, 45, 48)) {
            return WinColor.LIGHT_R;
        } else if (isBetween(bottomWinPercStats, 49, 51)) {
            return WinColor.WHITE;
        } else if (isBetween(bottomWinPercStats, 52, 55)) {
            return WinColor.LIGHT_B;
        } else if (isBetween(bottomWinPercStats, 56, 59)) {
            return WinColor.MEDIUM_B;
        } else if (isBetween(bottomWinPercStats, 60, 69)) {
            return WinColor.DARK_B;
        } else if (isBetween(bottomWinPercStats, 70, 100)) {
            return WinColor.BLUE;
        } else {
            logger.log(Level.SEVERE, "Incorrect percentage (" + bottomWinPercStats + ")!");
            return WinColor.BLACK;
        }
    }

}

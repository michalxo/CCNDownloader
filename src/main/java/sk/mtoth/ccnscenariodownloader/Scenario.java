package sk.mtoth.ccnscenariodownloader;

import javax.swing.*;
import java.io.Serializable;

/**
 * Created by mtoth on 1/15/15.
 */
public class Scenario implements Serializable {

    private static final long serialVersionUID = 3853225519227612346L;

    private boolean parsedData;
    private String url;
    private int topArmyStats = 0;
    private int bottomArmyStats = 0;
    private int totalPlaysStats = 0;
    private String name;

    private String imageUrl;
    // BufferedImage serialization
    int width;
    int height;
    int[] pixels;
    int imageRGB;
    ImageIcon image;

    static int playsSize = "Total plays".length();
    private String scenarioCode;
    private Enum<Expansion> expansion;
    private static String imageMatchGameSubstring = "/images/CCN/scenarios/";  // todo unstaticate for other games

    public Scenario(String url, String name) {
        this.url = url;
        this.name = name;
        this.parsedData = false;
    }

    Scenario(String url) {
        ScenarioDownloader.getScenarioData(this, url);
        System.out.println(this.toString());
        this.parsedData = true;
    }

    public int getTopArmyStats() {
        return topArmyStats;
    }

    public void setTopArmyStats(int topArmyStats) {
        this.topArmyStats = topArmyStats;
    }

    public int getBottomArmyStats() {
        return bottomArmyStats;
    }

    public void setBottomArmyStats(int bottomArmyStats) {
        this.bottomArmyStats = bottomArmyStats;
    }

    public int getTotalPlaysStats() {
        return totalPlaysStats;
    }

    public void setTotalPlaysStats(int totalPlaysStats) {
        this.totalPlaysStats = totalPlaysStats;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ImageIcon getImage() {
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        image.setRGB(0, 0, width, height, pixels, 0, width);
//        image.setRGB(0, 0, imageRGB);
        return image;
    }

    public void setImage(ImageIcon image) {
//        width = image.getWidth();
//        height = image.getHeight();
//        pixels = new int[width * height];
//        imageRGB = image.getRGB(0,0, width, height, pixels, 0, width); // ??
//        imageRGB = image.getRGB(0, 0);
//        this.imageName = name + ".img";

        this.image = image;
    }



    public static int getPlaysSize() {
        return playsSize;
    }

    public static void setPlaysSize(int playsSize) {
        Scenario.playsSize = playsSize;
    }

    public String getScenarioCode() {
        return scenarioCode;
    }

    public void setScenarioCode(String scenarioCode) {
        int number = 8;
        try {
            int numberCode = Integer.parseInt(scenarioCode);
            number = numberCode / 100;
        } catch (NumberFormatException e) {
            number = 8;
        } finally {
            this.setExpansion(Expansion.getExpansion(number));
        }
        this.scenarioCode = scenarioCode;
    }

    public Enum<Expansion> getExpansion() {
        return expansion;
    }

    public void setExpansion(Enum<Expansion> expansion) {
        if (this.expansion == null) {
            this.expansion = expansion;
        }
    }

    public String getImageMatchGameSubstring() {
        return imageMatchGameSubstring;
    }

    public void setImageMatchGameSubstring(String imageMatchGameSubstring) {
        this.imageMatchGameSubstring = imageMatchGameSubstring;
    }

    public boolean isParsedData() {
        return parsedData;
    }

    public void setParsedData(boolean parsedData) {
        this.parsedData = parsedData;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "parsedData=" + parsedData +
                ", url='" + url + '\'' +
                ", topArmyStats=" + topArmyStats +
                ", bottomArmyStats=" + bottomArmyStats +
                ", totalPlaysStats=" + totalPlaysStats +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
//                ", image=" + image. +
                ", scenarioCode='" + scenarioCode + '\'' +
                ", expansion=" + expansion +
                '}';
    }
}

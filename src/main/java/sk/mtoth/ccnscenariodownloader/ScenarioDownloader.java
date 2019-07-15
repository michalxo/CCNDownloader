package sk.mtoth.ccnscenariodownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mtoth on 1/15/15.
 */
class ScenarioDownloader {

    static final Logger logger = Logger.getLogger(ScenarioDownloader.class.toString());

    static void getScenarioData(Scenario scenario, String scenarioUrl) {
        try {
            Document document = Jsoup.connect(scenarioUrl).get();
            Elements names = document.getElementsByClass("history").tagName("h1");

            for (Element name : names) {
                if (name.childNodeSize() == 1) {
                    scenario.setName(name.text());
                    break;
                }
            }

            Element ccv_cont = document.getElementById("ccv_container");
            logger.log(Level.INFO, scenarioUrl);
            scenario.setScenarioCode(document.title().substring(0, document.title().indexOf(" ")));

            scenario.setTopArmyStats(parseStatsValue(ccv_cont.getElementsByClass("ccv_count green").text()));
            scenario.setBottomArmyStats(parseStatsValue(ccv_cont.getElementsByClass("ccv_count red").text()));
            scenario.setTotalPlaysStats(parseTotalPlaysValue(ccv_cont.getElementsByClass("status").text()));

            Element image = document.getElementsByAttributeValueContaining(
                    "src", scenario.getImageMatchGameSubstring()).tagName("img").first();
            if (image == null) {
                logger.log(Level.WARNING, "Unable to find image for " + scenarioUrl);
                // <img src="data:image/png;base64,iVBORw0KGgoAAAA...base64
            } else {
                String imgUrl = ScenarioRunner.baseUrl + image.attr("src");
                scenario.setImageUrl(imgUrl);
                scenario.setImage(downloadScenarioMap(imgUrl));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int parseStatsValue(String statsValue) {
        return Integer.parseInt(statsValue.replace("%", "").trim());
    }

    private static int parseTotalPlaysValue(String playsValue) {
        logger.log(Level.FINER, playsValue);
        if (playsValue.equals("")) {
            return 0;
        }
        return Integer.parseInt(playsValue.substring(Scenario.playsSize, playsValue.indexOf("-")).trim());
    }

    static ImageIcon downloadScenarioMap(String imageUrl) {
        logger.log(Level.INFO, "Going to download " + imageUrl);
        ImageIcon bufImage = null;
        try {
            bufImage = new ImageIcon(new URL(imageUrl));
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to download map " + imageUrl);
            e.printStackTrace();
        }
        return bufImage;
    }



    static void storeObjects(Serializable object, String filename, String folder) {
        FileOutputStream fos;
        ObjectOutputStream oos;
        try {
            Path dataFolder = Paths.get(folder);
            try {
                Files.createDirectories(dataFolder);
            } catch (FileAlreadyExistsException e) {
                logger.log(Level.FINE, "Folder already exists");
            }
            Path dataPath = Paths.get(dataFolder.toString() + File.separator + filename + ".data");
//            Files.deleteIfExists(dataPath); // "overwrite file"
//            Files.createFile(dataPath);

            fos = new FileOutputStream(dataPath.toString(), false);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.flush();
            logger.log(Level.INFO, "Stored data into file " + dataPath.toString());
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String writeFile(String folder, String file, String data) {
        FileOutputStream fos;
        BufferedOutputStream bos;
        String filePath = null;
        try {
            Path folders = Paths.get(folder);
//            Files.deleteIfExists(folders);
            Files.createDirectories(folders);
            filePath = folders + File.separator + file;

            fos = new FileOutputStream(filePath, false);
            bos = new BufferedOutputStream(fos);
            bos.write(data.getBytes());
            bos.flush();
            logger.log(Level.INFO, "Written data into file " + filePath);
            bos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    static List<Campaign> loadCampaigns(String strPath) throws IOException {
        Path path = Paths.get(strPath);
        Stream<Path> pathStream;
        List<Campaign> loadedData = new ArrayList<>();

        if (Files.isDirectory(path)) {
            pathStream = Files.walk(path, 1);
            Collection<Path> files = pathStream.filter(name -> name.toString().endsWith(".data")).collect(Collectors.toList());
            for (Path p : files) {
                loadedData.add(loadFile(p));
            }
        } else {
            loadedData.add(loadFile(path));
        }

        return loadedData;
    }

    /**
     * Load Campaign object with scenario list.
     * @param filePath path to campaign data file with
     * @return loaded Campaign
     */
    private static Campaign loadFile(Path filePath) {
        logger.log(Level.INFO, "Loading file " + filePath);

        FileInputStream fis;
        ObjectInputStream ois;
        Campaign loadedFile = null;

        try {
            fis = new FileInputStream(filePath.toString());
            ois = new ObjectInputStream(fis);

            loadedFile = (Campaign) ois.readObject();
            ois.close();
            fis.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return loadedFile;
    }

}

package sk.mtoth.ccnscenariodownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by mtoth on 1/17/15.
 */
public class ScenarioRunner {

    static String baseUrl;
    private static String dataFolder = System.getProperty("user.dir") + File.separator + "data";


    private static boolean loadScenariosAction() {
        return true;
    }

    private static boolean getScenariosAction() {
        return false;
    }

    private static boolean updateScenariosAction() {
        return false;
    }

    private static boolean testAction() {
        return true;
    }

    private static boolean testActionOfficialScenariosOnly() {
        return true;
    }


    public static void main(String[] args) {
//        System.out.format("%-10s:", "Asda");
//        ScenarioRunnerGUI ScenarioRunnerGUI = new sk.mtoth.ccnscenariodownloader.ScenarioRunnerGUI();
        List<Campaign> campaigns = null;

        if (getScenariosAction()) {
            ScenarioRunner.baseUrl = "https://www.commandsandcolors.net";
            campaigns = getOrUpdateScenarios(ScenarioRunnerGUI.scenariosUrl);

            // store action?
            for (Campaign c : campaigns) {
                ScenarioDownloader.storeObjects(c, c.getName(), dataFolder);
            }
        }

        if (updateScenariosAction()) {
            // todo: Update should only update statistics of plays, not map or anything else (few IFs in main getData?)
            throw new NotImplementedException();
        }

        if (loadScenariosAction()) {
            try {
                campaigns = ScenarioDownloader.loadCampaigns(dataFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (testAction()) {
            String file = dataFolder + File.separator + "First Coalition (1792-1797).data";
            try {
                campaigns = ScenarioDownloader.loadCampaigns(dataFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (campaigns != null) {
                TableGenerator htmlTable = new TableGenerator(campaigns);
//                campaigns.toString();
//                for (Campaign c : campaigns) {
//                    Scenario sc = c.getScenarios().get(0);
//                    System.out.println(sc);
//                    DisplayImage di2 = new DisplayImage(sc.getImage());
//                }
            }
        }

        if (testActionOfficialScenariosOnly()) {
            // load all scenarios action
            List<Campaign> officialScenariosCampaigns = new ArrayList<>(1);
            officialScenariosCampaigns.add(getOfficialScenariosCampaign(campaigns));
            TableGenerator htmlTable = new TableGenerator(officialScenariosCampaigns);
        }
    }

    private static Campaign getOfficialScenariosCampaign(List<Campaign> campaigns) {
        Collection officialExpansions = Collections.unmodifiableCollection(Arrays.asList(Expansion.BASE,
                Expansion.SPANISH, Expansion.RUSSIAN, Expansion.AUSTRIAN, Expansion.PRUSSIAN, Expansion.TMG, Expansion.EPIC, Expansion.LGB));
        Campaign officialScenariosCamp = new Campaign(null, "Official Scenarios only");
        for (Campaign c : campaigns) {
            for (Scenario s : c.getScenarios()) {
                if (officialExpansions.contains(s.getExpansion())) {
                    officialScenariosCamp.addScenario(s);
                }
            }
        }

        Collections.sort(officialScenariosCamp.getScenarios(), new ScenarioComparator());

        return officialScenariosCamp;
    }


    static class ScenarioComparator implements Comparator<Scenario> {

        @Override
        public int compare(Scenario o1, Scenario o2) {
            return o1.getScenarioCode().compareTo(o2.getScenarioCode());
        }
    }

    static List<Campaign> getOrUpdateScenarios(String scenariosUrl) {
        List<Campaign> campaignsList = new ArrayList<>();
        try {
            Document scenarioListDocument = Jsoup.connect(scenariosUrl).get();

            Element campaignsMainElement = scenarioListDocument.getElementById("xmap").addClass("sitemap");
            Elements campaignsElements = campaignsMainElement.getElementsByClass("level_1").addClass("level_2");
            Elements campaigns = campaignsElements.get(0).select("a[href*=/scenarios/]");


            for (Element campaign : campaigns) {
                Campaign campObj = new Campaign(campaign.attr("href"), campaign.text());
                Elements battles = campaign.parent().select("a[href*=maps]");
                for (Element battle : battles) {
                    campObj.addScenario(
                            new Scenario(ScenarioRunner.baseUrl + battle.attr("href"), battle.text()));
                }
                campaignsList.add(campObj);
            }

//            initialize and storeObjects
            for (Campaign c : campaignsList) {
                c.initScenarios();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return campaignsList;
    }
}

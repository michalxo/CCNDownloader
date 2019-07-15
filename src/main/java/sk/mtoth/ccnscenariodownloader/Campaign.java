package sk.mtoth.ccnscenariodownloader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Campaign implements Serializable {

    private static final long serialVersionUID = 3853225519227682853L;
    private final String url;
    private String name;
    List<Scenario> scenarios = new ArrayList<>();

    public Campaign(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }

    public List<Scenario> initScenarios() {
        List<Scenario> initializedScenarios = new ArrayList<>();
        for (Scenario sc : scenarios) {
            if (!sc.isParsedData()) {
                ScenarioDownloader.getScenarioData(sc, sc.getUrl());
                initializedScenarios.add(sc);
            }
        }
        return initializedScenarios;
    }

    public void addScenario(Scenario scenario) {
        this.scenarios.add(scenario);
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", scenarios=" + scenarios +
                '}';
    }
}

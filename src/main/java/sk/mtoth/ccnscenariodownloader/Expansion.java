package sk.mtoth.ccnscenariodownloader;

public enum Expansion {
    BASE(0),
    SPANISH(1),
    RUSSIAN(2),
    AUSTRIAN(3),
    PRUSSIAN(4),
    TMG(5),
    EPIC(6),
    LGB(7),
    OTHER(8);

    private int id;
    private static Expansion[] expansionNumber = Expansion.values();

    Expansion(int expansionNumber) {
        this.id = expansionNumber;
    }

    public static Expansion getExpansion(int number) {
        return expansionNumber[number];
    }
}
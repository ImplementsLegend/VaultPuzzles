package implementslegendkt.vhpuzzles;

public enum PuzzleDifficulty {
    NORMAL("{Name: Normal, Color: 697346}","normal"),
    HARD("{Name: Hard, Color: 14724096}","hard"),
    CHALLENGING("{Name: Challenging, Color: 12729345}","challenging"),
    EXTREME("{Name: Extreme, Color: 13172736}","extreme"),
    IMPOSSIBLE("{Name: Impossible, Color: 6357465}","impossible"),
    ;

    public final String display;
    public final String difficulty;

    PuzzleDifficulty(String display, String difficulty) {

        this.display = display;
        this.difficulty = difficulty;
    }
}

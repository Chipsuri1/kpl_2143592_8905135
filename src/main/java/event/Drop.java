package event;

public class Drop {

    private String input;

    public Drop(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public String toString() {
        return "Event: Drop";
    }
}

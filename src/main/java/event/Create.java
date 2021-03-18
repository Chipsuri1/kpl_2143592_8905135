package event;

public class Create {
    private String input;

    public Create(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public String toString() {
        return "Event: Create";
    }
}

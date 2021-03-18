package event;

public class Register {
    private String input;

    public Register(String input) {
        this.input = input;
    }

    public String toString() {
        return "Event: Register";
    }

    public String getInput() {
        return input;
    }
}

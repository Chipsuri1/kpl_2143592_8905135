package event;

import java.io.File;

public class Send {

    public String toString() {
        return "Event: Show";
    }

    boolean shift;
    boolean rsa;
    String message;
    File file;
    String input;

    public Send(boolean shift, boolean rsa, String message, File file, String input) {
        this.shift = shift;
        this.rsa = rsa;
        this.message = message;
        this.file = file;
        this.input = input;
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isRsa() {
        return rsa;
    }

    public String getMessage() {
        return message;
    }

    public File getFile() {
        return file;
    }

    public String getInput() {
        return input;
    }
}

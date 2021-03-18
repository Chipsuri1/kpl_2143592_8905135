package event;

import java.io.File;

public class Decrypt {

    public String toString() {
        return "Event: DecryptMessage";
    }

    boolean shift;
    boolean rsa;
    String message;
    File file;
    String command;

    public Decrypt(boolean shift, boolean rsa, String message, File file, String command) {
        this.shift = shift;
        this.rsa = rsa;
        this.message = message;
        this.file = file;
        this.command = command;
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

    public String getCommand() {
        return command;
    }
}

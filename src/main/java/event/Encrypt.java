package event;

import java.io.File;

public class Encrypt {
    boolean shift;
    boolean rsa;
    String message;
    File file;
    String command;

    public Encrypt(boolean shift, boolean rsa, String message, File file, String command) {
        this.shift = shift;
        this.rsa = rsa;
        this.message = message;
        this.file = file;
        this.command = command;
    }

    public String toString() {
        return "Event: EncryptMessage";
    }

    public File getFile() {
        return file;
    }

    public String getCommand() {
        return command;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRsa() {
        return rsa;
    }

    public boolean isShift() {
        return shift;
    }
}

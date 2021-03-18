package event;

import java.io.File;

public class CrackEncryptedMessage {
    boolean shift;
    boolean rsa;
    String message;
    File file;

    public CrackEncryptedMessage(boolean shift, boolean rsa, String message, File file) {
        this.shift = shift;
        this.rsa = rsa;
        this.message = message;
        this.file = file;
    }

    public String toString() {
        return "Event: CrackEncryptedMessage";
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
}

import java.io.File;

public class Shift {

    public static final Shift instance = new Shift();

    public Port port;

    public Shift() {
        port = new Port();
    }

    //TODO get key
    public String innerEncrypt(String plainText, File keyfile) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < plainText.length(); i++) {
            char character = (char) (plainText.codePointAt(i) + key);
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

    public String innerDecrypt(String cipherText, File keyfile) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < cipherText.length(); i++) {
            char character = (char) (cipherText.codePointAt(i) - key);
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

    public static Shift getInstance(){
        return instance;
    }

    public class Port implements IShift{
        public String encrypt(String plainText, File keyfile) {
            return innerEncrypt(plainText, keyfile);
        }

        public String decrypt(String encryptedText, File keyfile) {
            return innerDecrypt(encryptedText, keyfile);
        }
    }


}

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Shift {

    public static final Shift instance = new Shift();

    public Port port;
    private StringBuilder stringBuilder;
    private int key;

    public Shift() {
        port = new Port();
    }

    public String innerEncrypt(String plainText, File keyfile) {
        key = getKey(keyfile);
        stringBuilder = new StringBuilder();

        for (int i = 0; i < plainText.length(); i++) {
            char character = (char) (plainText.codePointAt(i) + key);
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

    public String innerDecrypt(String cipherText, File keyfile) {
        key = getKey(keyfile);
        stringBuilder = new StringBuilder();

        for (int i = 0; i < cipherText.length(); i++) {
            char character = (char) (cipherText.codePointAt(i) - key);
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

    public Integer getKey(File keyFile) {
        String json = null;
        try {
            json = new String(Files.readAllBytes(Paths.get(String.valueOf(keyFile))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        try {
            if (keyFile.getName().contains("keyfile")) {

                Integer n = jsonObject.get("key").getAsInt();

                return n;
            } else {
                System.out.println("invalid file");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Shift getInstance() {
        return instance;
    }

    public class Port implements IShift {
        public String encrypt(String plainText, File keyfile) {
            return innerEncrypt(plainText, keyfile);
        }

        public String decrypt(String encryptedText, File keyfile) {
            return innerDecrypt(encryptedText, keyfile);
        }
    }
}

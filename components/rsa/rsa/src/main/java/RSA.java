import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class RSA {
    private static final RSA instance = new RSA();

    public Port port;

    public RSA() {
        port = new Port();
    }

    private BigInteger crypt(BigInteger message, Key key) {
        return message.modPow(key.getE(), key.getN());
    }

    public String innerEncrypt(String plainMessage, File publicKeyfile) {
        Key key = getKey(publicKeyfile);

        byte[] bytes = plainMessage.getBytes(Charset.defaultCharset());
        return Base64.getEncoder().encodeToString(crypt(new BigInteger(bytes), key).toByteArray());
    }

    public String innerDecrypt(String encryptedMessage, File privateKeyfile) {
        Key key = getKey(privateKeyfile);
        byte[] msg = new BigInteger(Base64.getDecoder().decode(encryptedMessage)).modPow(key.getE(), key.getN()).toByteArray();
        return new String(msg);
    }

    public static RSA getInstance() {
        return instance;
    }

    public class Port implements IRSA {

        public String encrypt(String data, File publicKeyfile) {
            return innerEncrypt(data, publicKeyfile);
        }

        public String decrypt(String data, File privateKeyfile) {
            return innerDecrypt(data, privateKeyfile);
        }
    }

    public Key getKey(File keyFile) {
        String json = null;
        try {
            json = new String(Files.readAllBytes(Paths.get(String.valueOf(keyFile))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        try {
            if (keyFile.getName().contains("publicKey")) {

                BigInteger n = new BigInteger(jsonObject.getAsJsonObject("publicKey").get("n").getAsString());
                BigInteger e = new BigInteger(jsonObject.getAsJsonObject("publicKey").get("e").getAsString());

                return new Key(n, e);
            } else if (keyFile.getName().contains("privateKey")) {

                BigInteger n = new BigInteger(jsonObject.getAsJsonObject("privateKey").get("n").getAsString());
                BigInteger d = new BigInteger(jsonObject.getAsJsonObject("privateKey").get("d").getAsString());

                return new Key(n, d);
            } else {
                System.out.println("invalid file");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
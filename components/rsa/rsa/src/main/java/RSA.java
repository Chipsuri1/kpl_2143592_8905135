import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Base64;

public class RSA {

    private BigInteger crypt(BigInteger message, Key key) {
        return message.modPow(key.getE(), key.getN());
    }

    public String innerEncrypt(String plainMessage, File publicKeyfile) {
        Key key = Key.getKey(publicKeyfile);

        byte[] bytes = plainMessage.getBytes(Charset.defaultCharset());
        return Base64.getEncoder().encodeToString(crypt(new BigInteger(bytes), key).toByteArray());
    }

    public String innerDecrypt(String encryptedMessage, File privateKeyfile) {
        Key key = Key.getKey(privateKeyfile);

        byte[] msg = crypt(new BigInteger(encryptedMessage.getBytes()), key).toByteArray();
        return new String(msg);
    }

    public class Port implements IRSA {

        public String encrypt(String data, File publicKeyfile) {
            return innerEncrypt(data, publicKeyfile);
        }

        public String decrypt(String data, File privateKeyfile) {
            return innerDecrypt(data, privateKeyfile);
        }
    }

}
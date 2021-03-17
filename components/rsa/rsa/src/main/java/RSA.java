import java.io.File;
import java.math.BigInteger;
import java.nio.charset.Charset;
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

        byte[] msg = crypt(new BigInteger(encryptedMessage.getBytes()), key).toByteArray();
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
//        JsonParser jsonParser = new JsonParser();
//        JsonObject jsonObject = null;
//        JSONParser parser = new JSONParser();
//        JSONObject jsonObject = null;
//
//        try {
//            if(keyFile.getName().contains("publicKey")){
//                jsonObject = (JSONObject) ((JSONObject) parser.parse(new FileReader(keyFile))).get("publicKey");
//
//                BigInteger n = (BigInteger) jsonObject.get("n");
//                BigInteger e = (BigInteger) jsonObject.get("e");
//
//                return new Key(n, e);
//            }else if(keyFile.getName().contains("privateKey")){
//                jsonObject = (JSONObject) ((JSONObject) parser.parse(new FileReader(keyFile))).get("privateKey");
//
//                BigInteger n = (BigInteger) jsonObject.get("n");
//                BigInteger d = (BigInteger) jsonObject.get("d");
//
//                return new Key(n, d);
//            }else {
//                System.out.println("invalid file");
//                return null;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }}
        return null;
    }
}
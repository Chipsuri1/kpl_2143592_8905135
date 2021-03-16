import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;

public class Key {
    private final BigInteger n;
    private final BigInteger e;

    public Key(BigInteger n, BigInteger e) {
        this.n = n;
        this.e = e;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getE() {
        return e;
    }

    public static Key getKey(File keyFile) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;

        try {
            if(keyFile.getName().contains("publicKey")){
                jsonObject = (JSONObject) ((JSONObject) parser.parse(new FileReader(keyFile))).get("publicKey");

                BigInteger n = (BigInteger) jsonObject.get("n");
                BigInteger e = (BigInteger) jsonObject.get("e");

                return new Key(n, e);
            }else if(keyFile.getName().contains("privateKey")){
                jsonObject = (JSONObject) ((JSONObject) parser.parse(new FileReader(keyFile))).get("privateKey");

                BigInteger n = (BigInteger) jsonObject.get("n");
                BigInteger d = (BigInteger) jsonObject.get("d");

                return new Key(n, d);
            }else {
                System.out.println("invalid file");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
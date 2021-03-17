import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class RSACracker {
    public static final RSACracker instance = new RSACracker();

    public Port port;

    public RSACracker(){
        port = new Port();
    }

    public String innerDecrypt(String cipher, File publicKeyfile){
        Key key = getKey(publicKeyfile);

        try {
            BigInteger p, q, d;
            List<BigInteger> factorList = factorize(key.getN());

            if(factorList == null){
                return "time is over 30 seconds";
            }
            if (factorList.size() != 2) {
                throw new RSACrackerException("cannot determine factors p and q");
            }

            p = factorList.get(0);
            q = factorList.get(1);
            BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
            d = key.getE().modInverse(phi);
            System.out.println("D:" + d);

            return decrypt(Base64.getDecoder().decode(cipher), new Key(key.getN(), d));
        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    public List<BigInteger> factorize(BigInteger n) {
        long unixTimeStart = System.currentTimeMillis() / 1000L;

        BigInteger two = BigInteger.valueOf(2);
        List<BigInteger> factorList = new LinkedList<>();

        if (n.compareTo(two) < 0) {
            throw new IllegalArgumentException("must be greater than one");
        }

        while (n.mod(two).equals(BigInteger.ZERO)) {
            factorList.add(two);
            n = n.divide(two);
        }

        if (n.compareTo(BigInteger.ONE) > 0) {
            BigInteger factor = BigInteger.valueOf(3);
            while (factor.multiply(factor).compareTo(n) <= 0) {
                long unixTimeStampNow = System.currentTimeMillis() / 1000L;
                if(unixTimeStampNow - unixTimeStart >= 30){
                    return null;
                }
                if (n.mod(factor).equals(BigInteger.ZERO)) {
                    factorList.add(factor);
                    n = n.divide(factor);
                } else {
                    factor = factor.add(two);
                }
            }
            factorList.add(n);
        }

        return factorList;
    }

    public String decrypt(byte[] encryptedMessage, Key key) {
        byte[] msg = new BigInteger(encryptedMessage).modPow(key.getE(), key.getN()).toByteArray();
        System.out.println(new String(msg));
        return new String(msg);
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
            if(keyFile.getName().contains("publicKey")){

                BigInteger n = new BigInteger(jsonObject.getAsJsonObject("publicKey").get("n").getAsString());
                BigInteger e = new BigInteger(jsonObject.getAsJsonObject("publicKey").get("e").getAsString());

                return new Key(n, e);
            }else if(keyFile.getName().contains("privateKey")){

                BigInteger n = new BigInteger(jsonObject.getAsJsonObject("publicKey").get("n").getAsString());
                BigInteger d = new BigInteger(jsonObject.getAsJsonObject("publicKey").get("d").getAsString());

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


    public static RSACracker getInstance(){
        return instance;
    }

    public class Port implements IRSACracker{

        public String decrypt(String encryptedMessage, File publicKeyFile) {
            return innerDecrypt(encryptedMessage, publicKeyFile);
        }
    }

}
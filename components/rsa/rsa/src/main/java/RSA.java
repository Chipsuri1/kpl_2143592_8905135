import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;

public class RSA {
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger n;
    private final BigInteger t;
    private final BigInteger e;
    private final BigInteger d;
    private final Key publicKey;
    private final Key privateKey;

    public RSA(int keyLength) {
        SecureRandom randomGenerator = new SecureRandom();

        p = new BigInteger(keyLength, 100, randomGenerator).nextProbablePrime();
        q = new BigInteger(keyLength, 100, randomGenerator).nextProbablePrime();

        n = p.multiply(q);
        t = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = getCoPrime(t);
        d = e.modInverse(t);

        publicKey = new Key(n, e);

        privateKey = new Key(n, d);
    }

    private BigInteger crypt(BigInteger message, Key key) {
        return message.modPow(key.getE(), key.getN());
    }

    public String innerEncrypt(String plainMessage, File publicKeyfile) {
        Key key = getKey(publicKeyfile);

        byte[] bytes = plainMessage.getBytes(Charset.defaultCharset());
        return Base64.getEncoder().encodeToString(crypt(new BigInteger(bytes), key).toByteArray());
    }

    public String innerDecrypt(byte[] cipher, File privateKeyfile) {
        Key key = getKey(privateKeyfile);

        byte[] msg = crypt(new BigInteger(cipher), key).toByteArray();
        return new String(msg);
    }

    public boolean isCoPrime(BigInteger c, BigInteger n) {
        BigInteger one = new BigInteger("1");
        return c.gcd(n).equals(one);
    }

    public BigInteger getCoPrime(BigInteger n) {
        BigInteger result = new BigInteger(n.toString());
        BigInteger one = new BigInteger("1");
        BigInteger two = new BigInteger("2");
        result = result.subtract(two);

        while (result.intValue() > 1) {
            if (result.gcd(n).equals(one))
                break;
            result = result.subtract(one);
        }

        return result;
    }

    public Key getKey (File keyFile) throws IOException {
        String file = "src/test/resources/myFile.json";
        String json = new String(Files.readAllBytes(Paths.get(file)));
        return null;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getT() {
        return t;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getD() {
        return d;
    }

    public Key getPublicKey() {
        return publicKey;
    }

    public Key getPrivateKey() {
        return privateKey;
    }

    public class Port implements IRSA{

        public String encrypt(String data, File publicKeyfile) {
            return innerEncrypt(data, publicKeyfile);
        }

        public String decrypt(String data, File privateKeyfile) {
            return innerDecrypt(data, privateKeyfile);
        }
    }

}
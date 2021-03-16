import java.math.BigInteger;
import java.nio.charset.Charset;

public class Cipher {
    private BigInteger crypt(BigInteger message, Key key) {
        return message.modPow(key.getE(), key.getN());
    }

    public byte[] encrypt(String plainMessage, Key key) {
        byte[] bytes = plainMessage.getBytes(Charset.defaultCharset());
        return crypt(new BigInteger(bytes), key).toByteArray();
    }

    public String decrypt(byte[] cipher, Key key) {
        byte[] msg = crypt(new BigInteger(cipher), key).toByteArray();
        return new String(msg);
    }
}
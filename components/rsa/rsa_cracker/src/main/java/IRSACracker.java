import java.io.File;
import java.math.BigInteger;

public interface IRSACracker {
    String decrypt(String encryptedMessage, File publicKeyFile);
}

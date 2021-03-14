import java.io.File;

public interface IShift {
    String encrypt(String plainText, File keyfile);
    String decrypt(String encryptedText, File keyfile);
}

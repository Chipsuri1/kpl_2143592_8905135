import java.io.File;

public interface IRSA {
    String encrypt(String data, File publicKeyfile);
    String decrypt(String data, File privateKeyfile);
}

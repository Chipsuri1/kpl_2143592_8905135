import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSA {

    public static final RSA instance = new RSA();

    public Port port;

    public RSA(){
        port = new Port();
    }

    //TODO get keys from file
    private static String publicKey = "";
    private static String privateKey = "";

    public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public String innerEncrypt(String plainMessage, File publicKeyfile){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainMessage.getBytes()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String innerDecrypt(String encryptedMessage, File privateKeyfile){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage.getBytes())));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static RSA getInstance() {
        return instance;
    }

    public class Port implements IRSA{

        public String encrypt(String plainMessage, File publicKeyfile) {
            return innerEncrypt(plainMessage, publicKeyfile);
        }

        public String decrypt(String encryptedMessage, File privateKeyfile) {
            return innerDecrypt(encryptedMessage, privateKeyfile);
        }
    }

}

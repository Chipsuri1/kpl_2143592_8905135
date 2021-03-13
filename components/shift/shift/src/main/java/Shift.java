public class Shift {

    public static final Shift instance = new Shift(1);

    public Port port;

    //TODO remove key from constructor
    private final int key;

    public Shift(int key) {
        this.key = key;
        port = new Port();
    }

    public String innerEncrypt(String plainText) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < plainText.length(); i++) {
            char character = (char) (plainText.codePointAt(i) + key);
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

    public String innerDecrypt(String cipherText) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < cipherText.length(); i++) {
            char character = (char) (cipherText.codePointAt(i) - key);
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

    public static Shift getInstance(){
        return instance;
    }

    public class Port implements IShift{
        public String encrypt(String plainText) {
            return innerEncrypt(plainText);
        }

        public String decrypt(String encryptedText) {
            return innerDecrypt(encryptedText);
        }
    }


}

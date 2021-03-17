import java.text.DecimalFormat;

public class ShiftCracker {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00000");

    public static final ShiftCracker instance = new ShiftCracker();

    public Port port;

    public ShiftCracker(){
        port = new Port();
    }

    // rtwumjzx
    public String innerDecrypt(String encryptedMessage) {
        long unixTimeStart = System.currentTimeMillis() / 1000L;
        encryptedMessage = encryptedMessage.toUpperCase();

        if (encryptedMessage.equals("")) {
            System.exit(0);
        }

        char[] sourceText = new char[encryptedMessage.length()];
        int[] unicode = new int[encryptedMessage.length()];
        int[] unicodeCopy = new int[encryptedMessage.length()];

        for (int count = 0; count < encryptedMessage.length(); count++) {
            sourceText[count] = encryptedMessage.charAt(count);
        }

        String hex;
        int dec;

        for (int count = 0; count < sourceText.length; count++) {
            hex = Integer.toHexString(sourceText[count]);
            dec = Integer.parseInt(hex, 16);
            unicode[count] = dec;
            unicodeCopy[count] = dec;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int shift = 1; shift <= 25; shift++) {
            long unixTimeStampNow = System.currentTimeMillis() / 1000L;

            if(unixTimeStampNow - unixTimeStart >= 30){
                return "time is over 30 seconds";
            }

            String output = smartShift(shift, unicode, unicodeCopy);

            if(output != null){
                stringBuilder.append("Reshift: " + shift + " " + output);
                stringBuilder.append(System.getProperty("line.separator"));
            }
        }

        return stringBuilder.toString();
    }

    private static String smartShift(int shift, int[] unicode, int[] unicodeCopy) {
        for (int x = 0; x <= unicode.length - 1; x++) {
            unicodeCopy[x] = unicode[x];

            if (unicode[x] >= 65 && unicode[x] <= 90) {
                unicodeCopy[x] += shift;
                if (unicodeCopy[x] > 90) {
                    unicodeCopy[x] -= 26;
                }
            }
        }

        String[] processed = new String[unicode.length];
        char[] finalProcess = new char[unicode.length];

        for (int count = 0; count < processed.length; count++) {
            processed[count] = Integer.toHexString(unicodeCopy[count]);
            int hexToInt = Integer.parseInt(processed[count], 16);
            char intToChar = (char) hexToInt;
            finalProcess[count] = intToChar;
        }

        double frequency = 0;
        double aFrequency = 0;
        double eFrequency = 0;
        double iFrequency = 0;
        double oFrequency = 0;
        double uFrequency = 0;

        for (char c : finalProcess) {
            frequency++;

            switch (c) {
                case 'A':
                    aFrequency++;
                    break;
                case 'E':
                    eFrequency++;
                    break;
                case 'I':
                    iFrequency++;
                    break;
                case 'O':
                    oFrequency++;
                    break;
                case 'U':
                    uFrequency++;
                    break;
                default:
                    break;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (char character : finalProcess) {
            stringBuilder.append(character);
        }

        if (eFrequency / frequency >= 0.05 || aFrequency / frequency >= 0.05 || iFrequency / frequency >= 0.05 || oFrequency / frequency >= 0.05 || uFrequency / frequency >= 0.05) {
            return stringBuilder.toString();
        }else {
            return null;
        }
    }

    public static ShiftCracker getInstance() {
        return instance;
    }

    public class Port implements IShiftCracker{
        public String decrypt(String encryptedMessage){
            return innerDecrypt(encryptedMessage);
        }
    }
}
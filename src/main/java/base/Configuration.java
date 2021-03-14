package base;

public enum Configuration {
    instance;

    public final String userDirectory = System.getProperty("user.dir");
    public final String fileSeparator = System.getProperty("file.separator");
    public final String lineSeparator = System.getProperty("line.separator");

    public final String dataDirectory = userDirectory + fileSeparator + "data" + fileSeparator;

    public final String commonPathToJavaArchive = userDirectory + fileSeparator + "components" + fileSeparator;

    public final String inputDataFile = dataDirectory + fileSeparator + "input.txt";
    public final String encryptedDataFile = dataDirectory + fileSeparator + "encrypted.txt";

    public final String pathToRSAJavaArchive = commonPathToJavaArchive + "rsa" + fileSeparator+ "rsa" + fileSeparator + "build" + fileSeparator + "libs" + fileSeparator + "boyerMoore.jar";
    public final String pathToRSACrackerJavaArchive = commonPathToJavaArchive + "rsa" + fileSeparator + "rsa_cracker" + fileSeparator + "build" + fileSeparator + "libs" + fileSeparator + "boyerMoore.jar";
    public final String pathToShiftJavaArchive = commonPathToJavaArchive + "shift" + fileSeparator + "shift" + fileSeparator + "build" + fileSeparator + "libs" + fileSeparator + "boyerMoore.jar";
    public final String pathToShiftCrackerJavaArchive = commonPathToJavaArchive + "shift" + fileSeparator + "shift_cracker" + fileSeparator + "build" + fileSeparator + "libs" + fileSeparator + "boyerMoore.jar";

    public boolean debugMode = false;
}
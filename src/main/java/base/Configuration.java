package base;

public enum Configuration {
    instance;

    public String userDirectory = System.getProperty("user.dir");
    public String fileSeparator = System.getProperty("file.separator");

    public String dataDirectory = userDirectory + fileSeparator + "data" + fileSeparator;

    public String commonPathToJavaArchive = userDirectory + fileSeparator + "components" + fileSeparator;

    public String inputDataFile = dataDirectory + fileSeparator + "input.txt";
    public String encryptedDataFile = dataDirectory + fileSeparator + "encrypted.txt";

    public String pathToRSAJavaArchive = commonPathToJavaArchive + "rsa" + fileSeparator+ "rsa" + fileSeparator + "build" + fileSeparator + "libs" + fileSeparator + "boyerMoore.jar";
    public String pathToRSACrackerJavaArchive = commonPathToJavaArchive + "rsa" + fileSeparator + "rsa_cracker" + fileSeparator + "build" + fileSeparator + "libs" + fileSeparator + "boyerMoore.jar";
    public String pathToShiftJavaArchive = commonPathToJavaArchive + "shift" + fileSeparator + "shift" + fileSeparator + "build" + fileSeparator + "libs" + fileSeparator + "boyerMoore.jar";
    public String pathToShiftCrackerJavaArchive = commonPathToJavaArchive + "shift" + fileSeparator + "shift_cracker" + fileSeparator + "build" + fileSeparator + "libs" + fileSeparator + "boyerMoore.jar";
}
public enum Configuration {
    instance;

    public String userDirectory = System.getProperty("user.dir");
    public String fileSeparator = System.getProperty("file.separator");

    public String dataDirectory = userDirectory + fileSeparator + "data" + fileSeparator;

    public String inputDataFile = dataDirectory + fileSeparator + "input.txt";
    public String encryptedDataFile = dataDirectory + fileSeparator + "encrypted.txt";
}
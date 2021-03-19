package base;

import java.io.BufferedReader;
import java.io.File;

public class FileReader {
    private File getLastModified(String directoryFilePath)
    {
        File directory = new File(directoryFilePath);
        File[] files = directory.listFiles(File::isFile);
        int lastUnixTimestamp = 0;
        File chosenFile = null;

        if (files != null)
        {
            for (File file : files)
            {
                String[] content = file.getName().split("_");
                int unixTimestamp = Integer.valueOf(content[2].substring(0, content[2].indexOf(".")));
                if (unixTimestamp > lastUnixTimestamp)
                {
                    chosenFile = file;
                    lastUnixTimestamp = unixTimestamp;
                }
            }
        }

        return chosenFile;
    }

    public String readLogFile() {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br;
        try {
            br = new BufferedReader(new java.io.FileReader(getLastModified(Configuration.instance.logDirectory)));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(Configuration.instance.lineSeparator);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}

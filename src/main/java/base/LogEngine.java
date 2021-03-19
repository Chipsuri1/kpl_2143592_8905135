package base;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public enum LogEngine {
    instance;

    public BufferedWriter bufferedWriter;

    public void init(String logFile) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("log/"+logFile+".txt"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void writeLn(String line) {
        try {
            bufferedWriter.write(line + Configuration.instance.lineSeparator);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public void close() {
        try {
            bufferedWriter.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
package corporateNetwork;

import base.Configuration;
import base.LogEngine;
import entitys.*;
import entitys.Channel;
import entitys.Message;
import event.*;
import factory.RSACrackerFactory;
import factory.RSAFactory;
import factory.ShiftCrackerFactory;
import factory.ShiftFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class AppForGUI {

    private SessionFactory sessionFactory;
    private Session session;
    private CorporateNetwork corporateNetwork = new CorporateNetwork(this);

    public static void main(String[] args) {

        AppForGUI app = new AppForGUI();
        app.setupAlgo();

//        String command1 = "crack encrypted message \"rtwumjzx\" using shift";
//        String command2 = "crack encrypted message \"Yw\" using rsa and keyfile publicKeyfile.json";
        Configuration.instance.debugMode = true;
//        app.executeCommands("crack encrypted message \"Ytgnfx%nxy%qtxy\" using shift");
//        app.executeCommands("crack encrypted message \"dg== ALA= LA== LA== XA== AL0= ZQ== bw== dw== IA== Fw== bw== LA== IA== dg== XA== CQ== bw== XA== ag== bw== ag== AIE= IA== Fw== ZQ== CQ== bw== XA== AL0= bw== ew== Gg== XA== OA== CQ== XA== AL0= Zw== bw== ALA= ew== LA== \" using rsa and keyfile publicKeyfile.json");
        app.executeCommands("register participant branch_hkg with type normal");
        app.executeCommands("register participant branch_cpt with type normal");
        app.executeCommands("register participant branch_sfo with type normal");
        app.executeCommands("register participant branch_syd with type normal");
        app.executeCommands("register participant branch_wuh with type normal");
        app.executeCommands("register participant branch_sfo with type normal");
        app.executeCommands("register participant msa with type intruder");

        app.executeCommands("create channel hkg_wuh from branch_hkg to branch_wuh");
        app.executeCommands("create channel hkg_cpt from branch_hkg to branch_cpt");
        app.executeCommands("create channel cpt_syd from branch_cpt to branch_syd");
        app.executeCommands("create channel syd_sfo from branch_syd to branch_sfo");

        app.executeCommands("register participant branch_hkg with type normal");
        app.executeCommands("register participant branch_cpt with type normal");
        app.executeCommands("register participant branch_sfo with type normal");
        app.executeCommands("register participant branch_syd with type normal");
        app.executeCommands("register participant branch_wuh with type normal");
        app.executeCommands("register participant branch_sfo with type normal");
        app.executeCommands("register participant msa with type intruder");

        app.executeCommands("intrude channel hkg_wuh by msa");

        app.executeCommands("send message \"187\" from branch_hkg to branch_wuh using shift and keyfile keyfile.json");

        app.executeCommands("encrypt message \"y\" using rsa and keyfile publicKeyfile.json");
        app.executeCommands("decrypt message \"ANQ=\" using rsa and keyfile privateKeyfile.json");
        app.executeCommands("encrypt message \"yuhu\" using shift and keyfile keyfile.json");
        app.executeCommands("decrypt message \"yuhu\" using shift and keyfile keyfile.json");
    }


    public String executeCommands(String input) {
        String command = input.split(" ")[0];
        String result = null;
        boolean rsa = false;
        boolean shift = false;
        File file = null;
        String dataPath;
        String message = null;
        if (input.contains("\"")) {
            message = input.split("\"")[1];
            shift = input.substring(input.lastIndexOf("\"")).contains("shift");
            rsa = input.substring(input.lastIndexOf("\"")).contains("rsa");
        }
        if (input.contains("keyfile")) {
            dataPath = "configuration/" + input.split("keyfile", 2)[1].substring(1);
            file = new File(dataPath);
        }
        switch (command) {
            case "encrypt":
                result = corporateNetwork.receive(new Encrypt(shift, rsa, message, file, command));
                break;
            case "decrypt":
                result = corporateNetwork.receive(new Decrypt(shift, rsa, message, file, command));
                break;
            case "crack":
                result = corporateNetwork.receive(new CrackEncryptedMessage(shift, rsa, message, file));
                break;
            case "register":
                result = corporateNetwork.receive(new Register(input));
                break;
            case "create":
                result = corporateNetwork.receive(new Create(input));
                break;
            case "show":
                result = corporateNetwork.receive(new Show());
                break;
            case "drop":
                result = corporateNetwork.receive(new Drop(input));
                break;
            case "intrude":
                result = corporateNetwork.receive(new Intrude(input));
                break;
            case "send":
                result = corporateNetwork.receive(new Send(shift, rsa, message, file, input));
                break;
            case "set":
                result = setMessageToGUI(input);
                break;
            default:
                result = "invalid command, please check your input";
        }
        return result;
    }

    public String decrypt(String algorithm, String message, File file) {
        Object decrypter = null;
        String result = null;
        if (algorithm.equals("shift")) {
            algorithm = "shift";
            decrypter = ShiftFactory.build();
            try {
                Method decryptMethod = decrypter.getClass().getDeclaredMethod("decrypt", String.class, File.class);
                result = (String) decryptMethod.invoke(decrypter, message, file);

                System.out.println(result);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (algorithm.equals("rsa")) {
            algorithm = "rsa";
            decrypter = RSAFactory.build();
            String[] inputs = message.split(" ");
            try {
                Method decryptMethod = decrypter.getClass().getDeclaredMethod("decrypt", String.class, File.class);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < inputs.length; i++) {
                    stringBuilder.append((String) decryptMethod.invoke(decrypter, inputs[i], file));
                }
                result = stringBuilder.toString();

                System.out.println(result);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (Configuration.instance.debugMode) {
            LogEngine.instance.init("decrypt_" + algorithm + "_" + (System.currentTimeMillis() / 1000L));
            LogEngine.instance.writeLn("Command: decrypt, Message: " + message + ", Cipher: " + result + ", algorithm: " + algorithm);
            LogEngine.instance.close();
        }
        return result;
    }

    public String encrypt(String algorithm, String message, File file) {
        Object encryptor = null;
        String result = null;
        if (algorithm.equals("shift")) {
            encryptor = ShiftFactory.build();
            try {
                Method encryptMethod = encryptor.getClass().getDeclaredMethod("encrypt", String.class, File.class);
                result = (String) encryptMethod.invoke(encryptor, message, file);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (algorithm.equals("rsa")) {
            encryptor = RSAFactory.build();
            try {
                String[] inputStrings = message.split("");
                Method encryptMethod = encryptor.getClass().getDeclaredMethod("encrypt", String.class, File.class);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < inputStrings.length; i++) {
                    stringBuilder.append(encryptMethod.invoke(encryptor, inputStrings[i], file) + " ");
                }
                result = stringBuilder.toString();

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (Configuration.instance.debugMode) {
            LogEngine.instance.init("encrypt_" + algorithm + "_" + (System.currentTimeMillis() / 1000L));
            LogEngine.instance.writeLn("Command: encrypt, algorithm: " + algorithm + ",Message: " + message + ", Cipher: " + result);
            LogEngine.instance.close();
        }
        endSession();
        return result;
    }


    void tryToAddParticipantToList(ArrayList<Participant> participants, String participantName) {
        Query query = session.createQuery("From Participant P WHERE P.name = :participantName");
        query.setParameter("participantName", participantName);
        if (!query.list().isEmpty()) {
            participants.add((Participant) query.list().get(0));
        }
    }

    public String crackEncryptedMessage(String algorithm, String message, File file) {
        if (algorithm.equals("shift")) {
            return crackEncryptedMessageShift(message);
        } else if (algorithm.equals("rsa")) {
            return crackEncryptedMessageRSA(message, file);
        }else{
            return "Invalid algorithm. Please try again";
        }
    }

    private String crackEncryptedMessageRSA(String message, File file) {
        Object cracker = RSACrackerFactory.build();
        try {
            Method decryptMethod = cracker.getClass().getDeclaredMethod("decrypt", String.class, File.class);
            String[] inputs = message.split(" ");

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < inputs.length; i++) {
                stringBuilder.append((String) decryptMethod.invoke(cracker, inputs[i], file));
            }
            String encryptedMessage = stringBuilder.toString();

            if (encryptedMessage.contains("time is over 30 seconds")) {
                System.err.println("Calculation took to long");
                return "cracking encrypted message \"" + message + "\" failed";
            } else {
                return encryptedMessage;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String crackEncryptedMessageShift(String message) {
        Object cracker = ShiftCrackerFactory.build();
        try {
            Method decryptMethod = cracker.getClass().getDeclaredMethod("decrypt", String.class);
            String encryptedMessage = (String) decryptMethod.invoke(cracker, message);
            if (encryptedMessage.equals("time is over 30 seconds")) {
                System.err.println("Calculation took to long");
                return "cracking encrypted method \"" + message + "\" failed";
            } else {
                return encryptedMessage;
            }
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String setMessageToGUI(String input){
        String message = null;
        message = input.replaceFirst("set ", "");
        return message;
    }


    public void startSession() {
        sessionFactory = HibernateUtility.getSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
    }

    public void endSession() {
        session.getTransaction().commit();
    }

    public void setupData() {
        executeCommands("register participant branch_hkg with type normal");
        executeCommands("register participant branch_cpt with type normal");
        executeCommands("register participant branch_sfo with type normal");
        executeCommands("register participant branch_syd with type normal");
        executeCommands("register participant branch_wuh with type normal");
        executeCommands("register participant branch_sfo with type normal");
        executeCommands("register participant msa with type intruder");
        executeCommands("create channel hkg_wuh from branch_hkg to branch_wuh");
        executeCommands("create channel hkg_cpt from branch_hkg to branch_cpt");
        executeCommands("create channel cpt_syd from branch_cpt to branch_syd");
        executeCommands("create channel syd_sfo from branch_syd to branch_sfo");
    }

    public void setupAlgo(){
        startSession();
        Algorithm algorithm = new Algorithm("rsa");
        session.save(algorithm);
        algorithm = new Algorithm("shift");
        session.save(algorithm);
        endSession();
    }

    public Session getSession() {
        return session;
    }
}
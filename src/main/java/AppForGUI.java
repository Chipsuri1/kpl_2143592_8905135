import base.Configuration;
import base.LogEngine;
import entitys.*;
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
import java.util.List;

public class AppForGUI {

    private SessionFactory sessionFactory;
    private Session session;

    public static void main(String[] args) {

        AppForGUI app = new AppForGUI();
//        app.executeCommands("crack encrypted message \"dg== ALA= LA== LA== XA== AL0= ZQ== bw== dw== IA== Fw== bw== LA== IA== dg== XA== CQ== bw== XA== ag== bw== ag== AIE= IA== Fw== ZQ== CQ== bw== XA== AL0= bw== ew== Gg== XA== OA== CQ== XA== AL0= Zw== bw== ALA= ew== LA== \" using rsa and keyfile publicKeyfile.json");
//
//        String command1 = "crack encrypted message \"rtwumjzx\" using shift";
//        String command2 = "crack encrypted message \"Yw\" using rsa and keyfile publicKeyfile.json";
//
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
//
//        app.executeCommands("encrypt message \"y\" using rsa and keyfile publicKeyfile.json");
//        app.executeCommands("decrypt message \"ANQ=\" using rsa and keyfile privateKeyfile.json");
//        app.executeCommands("encrypt message \"yuhu\" using shift and keyfile keyFile.json");
//        app.executeCommands("decrypt message \"yuhu\" using shift and keyfile keyFile.json");

//        Transaction transaction = null;
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            // start a transaction
//            transaction = session.beginTransaction();
//            // save the student objects
//            // commit transaction
//            transaction.commit();
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        }
//
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        }
    }


    public String executeCommands(String input) {
        String command = input.split(" ")[0];
        String result = null;
        boolean rsa = false;
        boolean shift = false;
        File file = null;
        String message = null;
        String dataPath;
        String algorithm = null;

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
                Object encryptor = null;
                if (shift) {
                    algorithm = "shift";
                    encryptor = ShiftFactory.build();
                    try {
                        Method encryptMethod = encryptor.getClass().getDeclaredMethod("encrypt", String.class, File.class);
                        result = (String) encryptMethod.invoke(encryptor, message, file);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else if (rsa) {
                    algorithm = "rsa";
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
                    LogEngine.instance.init(command + "_" + algorithm + "_" + (System.currentTimeMillis() / 1000L));
                    LogEngine.instance.writeLn("Command: " + command + ", algorithm: " + algorithm + ",Message: " + message + ", Cipher: " + result);
                    LogEngine.instance.close();
                }
                break;
            case "decrypt":
                Object decrypter = null;
                if (shift) {
                    algorithm = "shift";
                    decrypter = ShiftFactory.build();
                    try {
                        Method decryptMethod = decrypter.getClass().getDeclaredMethod("decrypt", String.class, File.class);
                        result = (String) decryptMethod.invoke(decrypter, message, file);

                        System.out.println(result);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else if (rsa) {
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
                    LogEngine.instance.init(command + "_" + algorithm + "_" + (System.currentTimeMillis() / 1000L));
                    LogEngine.instance.writeLn("Command: " + command + ", Message: " + message + ", Cipher: " + result + ", algorithm: " + algorithm);
                    LogEngine.instance.close();
                }
                break;
            case "crack":
                return crackEncryptedMessage(shift, rsa, message, file);
            case "register":
                startSession();
                String[] inputStrings = input.split(" ");
                if (inputStrings.length == 6) {
                    String participantName = inputStrings[2];
                    String typeString = inputStrings[inputStrings.length - 1];
                    Query query = session.createQuery("from Participant P WHERE P.name = :participantName");
                    query.setParameter("participantName", participantName);
                    List resultList = query.list();
                    if (resultList.isEmpty()) {
                        query = session.createQuery("from Type T WHERE T.name = :typeString");
                        query.setParameter("typeString", typeString);
                        resultList = query.list();
                        Type type = null;
                        if (resultList.isEmpty()) {
                            type = new Type(typeString);
                            session.save(type);
                        } else {
                            type = (Type) resultList.get(0);
                        }
                        Participant participant = new Participant(participantName, type);
                        session.save(participant);
                        Postbox postbox = new Postbox(participant);
                        session.save(postbox);
                        result = "participant " + participantName + " with type " + typeString + " registered and postbox_" + participantName + " created";
                    } else {
                        result = "participant " + participantName + " already exists, using existing postbox_" + participantName;
                    }
                }
                endSession();
                break;
            case "create":
                startSession();
                Query query = null;
                ArrayList<Participant> participants = new ArrayList<>();
                inputStrings = input.split(" ");
                if (inputStrings.length == 7) {
                    String channelName = inputStrings[2];
                    String participantName1 = inputStrings[4];
                    String participantName2 = inputStrings[6];
                    tryToAddParticipantToList(participants, participantName1);
                    tryToAddParticipantToList(participants, participantName2);
                    if (participants.size() == 2) {
                        if (participants.get(0).equals(participants.get(1))) {
                            result = participantName1 + " and " + participantName2 + " are identical – cannot create channel on itself";
                        } else {
                            query = session.createQuery("from Channel C WHERE C.name = :channelName");
                            query.setParameter("channelName", channelName);
                            if (query.list().isEmpty()) {

                                query = session.createQuery("from Channel C WHERE C.participant1 = :participant1 AND C.participant2 = :participant2");
                                query.setParameter("participant1", participants.get(0));
                                query.setParameter("participant2", participants.get(1));
                                List query1List = query.list();

                                query = session.createQuery("from Channel C WHERE C.participant1 = :participant2 AND C.participant2 = :participant1");
                                query.setParameter("participant1", participants.get(0));
                                query.setParameter("participant2", participants.get(1));
                                List query2List = query.list();

                                if (query1List.isEmpty() && query2List.isEmpty()) {
                                    Channel channel = new Channel(channelName, participants.get(0), participants.get(1));
                                    session.save(channel);
                                    result = "channel " + channelName + " from " + participantName1 + " to " + participantName2 + " successfully created";
                                } else {
                                    result = "communication channel between " + participantName1 + " and " + participantName2 + " already exists";
                                }
                            } else {
                                result = "channel " + channelName + " already exists";
                            }
                        }
                    }
                }
                endSession();
                break;
            case "show":
                result = showChannel();
                endSession();
                break;
            case "drop":
                result = dropChannel(input);
                endSession();
                break;
            case "intrude":
                startSession();
                query = null;
                inputStrings = input.split(" ");
                String channelName = inputStrings[2];
                String participantName = inputStrings[4];
                query = session.createQuery("from Channel C WHERE C.name = :channelName");
                query.setParameter("channelName", channelName);
                if (!query.list().isEmpty()) {
                    Type type = null;
                    query = session.createQuery("from Type T WHERE T.name = :typeString");
                    query.setParameter("typeString", "intruder");
                    if (query.list().isEmpty()) {
                        type = new Type("intruder");
                        session.save(type);
                    } else {
                        type = (Type) query.list().get(0);
                    }
                    Participant participant = new Participant(participantName, type);
                }
                break;
            case "send":
                break;
            default:
                result = "invalid command, please check your input";
        }
        return result;
    }

    private void tryToAddParticipantToList(ArrayList<Participant> participants, String participantName) {
        Query query = session.createQuery("From Participant P WHERE P.name = :participantName");
        query.setParameter("participantName", participantName);
        if (!query.list().isEmpty()) {
            participants.add((Participant) query.list().get(0));
        }
    }

    private String crackEncryptedMessage(boolean shift, boolean rsa, String message, File file) {
        if (shift) {
            return crackEncryptedMessageShift(message);
        } else if (rsa) {
            return crackEncryptedMessageRSA(message, file);
        }
        return null;
    }

    private String crackEncryptedMessageRSA(String message, File file){
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
                return "cracking encrypted method \"" + message + "\" failed";
            } else {
                return encryptedMessage;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private String crackEncryptedMessageShift(String message){
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

    private String dropChannel(String input){
        startSession();
        String[] inputStrings = input.split("channel ");
        String channelNameDropQuery = inputStrings[1];
        Query queryDropChannel = session.createQuery("from Channel C where C.name = :channelName");
        queryDropChannel.setParameter("channelName", channelNameDropQuery);
        Channel channel = (Channel) queryDropChannel.list().get(0);

        session.delete(channel);
        if(queryDropChannel.list().isEmpty()){
            return "unknown channel " + channelNameDropQuery;
        }else{
            return "channel " + channelNameDropQuery + " deleted";
        }
    }

    private String showChannel(){
        startSession();
        Query showChannelQuery = session.createQuery("FROM Channel");

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < showChannelQuery.list().size(); i++) {
            Channel channel = (Channel) showChannelQuery.list().get(i);
            stringBuilder.append(channel.getName());
            stringBuilder.append(Configuration.instance.lineSeparator);
        }
        System.out.println(stringBuilder.toString());

        return stringBuilder.toString();
    }


    private void startSession() {
        sessionFactory = HibernateUtility.getSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
    }

    private void endSession() {
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
}
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

//        String command1 = "crack encrypted message \"rtwumjzx\" using shift";
//        String command2 = "crack encrypted message \"Yw\" using rsa and keyfile publicKeyfile.json";

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

//        app.executeCommands("encrypt message \"y\" using rsa and keyfile publicKeyfile.json");
//        app.executeCommands("decrypt message \"ANQ=\" using rsa and keyfile privateKeyfile.json");
//        app.executeCommands("encrypt message \"yuhu\" using shift and keyfile keyFile.json");
//        app.executeCommands("decrypt message \"yuhu\" using shift and keyfile keyFile.json");
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
                result = encrypt(shift, rsa, message, file, command);
                break;
            case "decrypt":
                result = decrypt(shift, rsa, message, file, command);
                break;
            case "crack":
                return crackEncryptedMessage(shift, rsa, message, file);
            case "register":
                result = register(input);
                break;
            case "create":
                result = create(input);
                break;
            case "show":
                result = showChannel();
                break;
            case "drop":
                result = dropChannel(input);
                break;
            case "intrude":
                result = intrude(input);
                break;
            case "send":
                result = send(shift, rsa, message, file, input);
                break;
            default:
                result = "invalid command, please check your input";
        }
        return result;
    }

    private String send(boolean shift, boolean rsa, String message, File file, String input) {
        startSession();
        String result = null;
        String cipher = null;
        String algorithm = null;
        if (shift) {
            algorithm = "shift";
        } else if(rsa){
            algorithm = "rsa";
        }

        Query query = null;
        ArrayList<Participant> participants = new ArrayList<>();
        String[] inputStrings = input.split(" ");
        String participantName1 = null;
        String participantName2 = null;
        if (inputStrings.length == 11) {
            participantName1 = inputStrings[4];
            participantName2 = inputStrings[6];
            tryToAddParticipantToList(participants, participantName1);
            tryToAddParticipantToList(participants, participantName2);
            if (participants.size() == 2) {
                if (participants.get(0).equals(participants.get(1))) {
                    result = "no valid channel from " + participantName1 + " to "+participantName2;
                } else {
                    cipher = executeCommands("encrypt message \"" + message + "\" using " + algorithm + " and keyfile " + file.getName().split("/")[1]);

                    query = session.createQuery("from Channel C WHERE C.participant1 = :participant1 AND C.participant2 = :participant2");
                    query.setParameter("participant1", participants.get(0));
                    query.setParameter("participant2", participants.get(1));
                    List queryList = query.list();
                    if (!queryList.isEmpty()) {
                        Channel channel = (Channel) queryList.get(0);

                        query = session.createQuery("from Algorithm A WHERE A.name = :algorithm");
                        query.setParameter("algorithm", algorithm);
                        Algorithm algorithmEntity = (Algorithm)query.list().get(0);
                        Message messageEntity = new Message(participants.get(0), participants.get(1), message, algorithmEntity, cipher, file.getName().split("/")[1]);
                        session.save(messageEntity);
                        message = executeCommands("decrypt message \"" + cipher + "\" using " + algorithm + " and keyfile " + file.getName().split("/")[1]);
                        //postbox von part2 getten und dann neuen eintrag machen
                        result = participantName2 + " received new message";
                    } else {
                        result = "no valid channel from " + participantName1 + " to "+participantName2;
                    }
                }
            }
        }

        endSession();
        return result;
    }

    private String decrypt(boolean shift, boolean rsa, String message, File file, String command) {
        Object decrypter = null;
        String algorithm = null;
        String result = null;
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
        endSession();
        return result;
    }

    private String encrypt(boolean shift, boolean rsa, String message, File file, String command) {
        Object encryptor = null;
        String algorithm = null;
        String result = null;
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
        endSession();
        return result;
    }


    private String register(String input) {
        String result = null;
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
        return result;
    }

    private String create(String input) {
        startSession();
        Query query = null;
        String result = null;
        ArrayList<Participant> participants = new ArrayList<>();
        String[] inputStrings = input.split(" ");
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
        return result;
    }

    private String intrude(String input) {
        startSession();
        String result;
        boolean successful = false;
        Participant participant = null;
        Query query;
        String[] inputStrings = input.split(" ");
        String channelName = inputStrings[2];
        String participantName = inputStrings[4];

        query = session.createQuery("from Channel C WHERE C.name = :channelName");
        query.setParameter("channelName", channelName);
        if (!query.list().isEmpty()) {

            Type type = null;
            query = session.createQuery("from Type T WHERE T.name = :typeString");
            query.setParameter("typeString", "intruder");
            type = (Type) query.list().get(0);
            query = session.createQuery("from Participant P WHERE P.type = :type");
            query.setParameter("type", type);
            participant = (Participant) query.list().get(0);

        }
        if (successful) {
            result = "intruder " + participant.getName() + " cracked message from participant [name] | [message]";
        } else {
            result = "intruder [name] | crack message from participant [name] failed";
        }
        endSession();
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
                return "cracking encrypted method \"" + message + "\" failed";
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

    private String dropChannel(String input) {
        String result = null;
        startSession();
        String[] inputStrings = input.split("channel ");
        String channelNameDropQuery = inputStrings[1];
        System.out.println(channelNameDropQuery);

        Query queryDropChannel = session.createQuery("from Channel C where C.name = :channelName");
        queryDropChannel.setParameter("channelName", channelNameDropQuery);

        if (queryDropChannel.list().size() == 0) {
            result = "unknown channel " + channelNameDropQuery;
        } else {
            Channel channel = (Channel) queryDropChannel.list().get(0);
            session.delete(channel);
            result = "channel " + channelNameDropQuery + " deleted";
        }
        endSession();
        return result;
    }

    private String showChannel() {
        startSession();
        Query showChannelQuery = session.createQuery("FROM Channel");

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < showChannelQuery.list().size(); i++) {
            Channel channel = (Channel) showChannelQuery.list().get(i);
            stringBuilder.append(channel.getName() + " | " + channel.getParticipant1().getName() + " and " + channel.getParticipant2().getName());
            stringBuilder.append(Configuration.instance.lineSeparator);
        }
        endSession();
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

        startSession();
        Algorithm algorithm = new Algorithm("rsa");
        session.save(algorithm);
        algorithm = new Algorithm("shift");
        session.save(algorithm);
        endSession();
    }
}
import base.Configuration;
import base.LogEngine;
import entitys.HibernateUtility;
import entitys.Participant;
import entitys.Type;
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
import java.util.concurrent.*;

public class AppForGUI {

    private SessionFactory sessionFactory;
    private Session session;

    public static void main(String[] args) {

        AppForGUI app = new AppForGUI();
        app.executeCommands("crack encrypted message \"Yw\" using rsa and keyfile publicKeyfile.json");


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
        String dataPath = null;
        String algorithm = null;

        if(input.contains("\"")){
            message = input.split("\"")[1];
            shift = input.substring(input.lastIndexOf("\"")).contains("shift");
            rsa = input.substring(input.lastIndexOf("\"")).contains("rsa");
        }
        if(input.contains("keyfile")){
            dataPath = "configuration/" + input.split("keyfile")[1].substring(1);
            file = new File(dataPath);
        }

        switch (command) {
            case "encrypt":
                Object encryptor = null;
                if (shift) {
                    algorithm = "shift";
                    encryptor = ShiftFactory.build();
                } else if (rsa) {
                    algorithm = "rsa";
                    encryptor = RSAFactory.build();
                }
                try {
                    Method encryptMethod = encryptor.getClass().getDeclaredMethod("encrypt", String.class, File.class);
                    result = (String) encryptMethod.invoke(encryptor, message, file);

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (Configuration.instance.debugMode) {
                    LogEngine.instance.init(command + "_" + algorithm + "_" + (System.currentTimeMillis() / 1000L));
                    LogEngine.instance.writeLn("Command: " + command + ",Message: " + message + ", Cipher: " + result + ", algorithm: " + algorithm);
                    LogEngine.instance.close();
                }
                break;
            case "decrypt":
                Object decrypter = null;
                if (shift) {
                    algorithm = "shift";
                    decrypter = ShiftFactory.build();
                } else if (rsa) {
                    algorithm = "rsa";
                    decrypter = RSAFactory.build();
                }
                try {
                    Method decryptMethod = decrypter.getClass().getDeclaredMethod("decrypt", String.class, File.class);
                    result = (String) decryptMethod.invoke(decrypter, message, file);

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (Configuration.instance.debugMode) {
                    LogEngine.instance.init(command + "_" + algorithm + "_" + (System.currentTimeMillis() / 1000L));
                    LogEngine.instance.writeLn("Command: " + command + ", Message: " + message + ", Cipher: " + result + ", algorithm: " + algorithm);
                    LogEngine.instance.close();
                }
                break;
            case "crack":
                Object cracker;

                if (shift) {
                    cracker = ShiftCrackerFactory.build();
                    try {
                        Method decryptMethod = cracker.getClass().getDeclaredMethod("decrypt", String.class);
                        String encryptedMessage = (String) decryptMethod.invoke(message);

                        if(encryptedMessage.equals("time is over 30 seconds")){
                            System.err.println("Calculation took to long");
                            return "cracking encrypted method \"" + message + "\" failed";
                        }else {
                            return encryptedMessage;
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }

                } else if (rsa) {
                    cracker = RSACrackerFactory.build();
                    try {
                        Method decryptMethod = cracker.getClass().getDeclaredMethod("decrypt", String.class, File.class);

                        String encryptedMessage = (String) decryptMethod.invoke(decryptMethod, message, file);
                        System.out.println(file.getName());



                        if(encryptedMessage.equals("time is over 30 seconds")){
                            System.err.println("Calculation took to long");
                            return "cracking encrypted method \"" + message + "\" failed";
                        }else {
                            return encryptedMessage;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "register":
                startSession();
                String[] inputStrings = input.split(" ");
                String participantName = inputStrings[2];
                String typeString = inputStrings[inputStrings.length - 1];

                Query query = session.createQuery("from Participant WHERE name = " + participantName);
//                if (query.list().get(0) == null) {
                    //TODO put to db
                    Type type = new Type(typeString);
                    session.save(type);
                    Participant participant = new Participant(participantName, type);
                    session.save(participant);
//                    Postbox postbox = new Postbox();
//                    session.save(postbox);
                    result = "participant " + participantName + " with type " + typeString + " registered and postbox_" + participantName + " created";
//                } else {
//                    result = "participant " + participantName + " already exists, using existing postbox_" + participantName;
//                }
                endSession();
                break;
            case "create":
                break;
            case "show":
                break;
            case "drop":
                break;
            case "intrude":
                break;
            case "send":
                break;
            default:
                throw new RuntimeException("invalid command, please check your input");
        }
        return result;
    }

    private void startSession() {
        sessionFactory = HibernateUtility.getSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
    }

    private void endSession() {
        session.getTransaction().commit();
    }

}
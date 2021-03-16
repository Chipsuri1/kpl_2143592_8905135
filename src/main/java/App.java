import entitys.HibernateUtil;
import factory.RSACrackerFactory;
import factory.RSAFactory;
import factory.ShiftCrackerFactory;
import factory.ShiftFactory;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.*;

public class App {
    public static void main(String[] args) {

            App app = new App();
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


    public String executeCommands(String input){
        String command = input.split(" ")[0];
        String result = null;
        String message = input.split("\"")[1];
        String dataPath = "configuration/" + input.split("keyfile")[1].substring(2);
        File file = new File(dataPath.substring(0, dataPath.length()-1));
        switch (command){
            case "encrypt":
                Object encryptor = null;
                if(input.substring(input.lastIndexOf("\"")).contains("shift") ){
                    encryptor = ShiftFactory.build();
                }else if(input.substring(input.lastIndexOf("\"")).contains("rsa")){
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
                break;
            case "decrypt":
                Object decrypter;
                if(input.contains("shitft")){
                    decrypter = ShiftFactory.build();
                    try {
                        Method decryptMethod = decrypter.getClass().getMethod("decrypt");
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }else if(input.contains("rsa")){
                    decrypter = RSAFactory.build();
                    try {
                        Method decryptMethod = decrypter.getClass().getDeclaredMethod("decrypt");
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "crack":
                Object cracker;

                if(input.substring(input.lastIndexOf("\"")).contains("shift")){
                    cracker = ShiftCrackerFactory.build();
                    try {
                        Method decryptMethod = cracker.getClass().getDeclaredMethod("decrypt", String.class);
                        final String[] encryptedMessage = new String[1];

                        final ExecutorService service = Executors.newSingleThreadExecutor();
                        try {
                            final Future<Object> f = service.submit(() -> {
                                // Do you long running calculation here
                                encryptedMessage[0] = (String) decryptMethod.invoke(message); // Simulate some delay
                                return encryptedMessage[0];
                            });

                            System.out.println(f.get(30, TimeUnit.SECONDS));
                        } catch (final TimeoutException e) {
                            System.err.println("Calculation took to long");
                            return "cracking encrypted method \"" + message + "\" failed";
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }

                }else if(input.substring(input.lastIndexOf("\"")).contains("rsa")){
                    cracker = RSACrackerFactory.build();
                    try {
                        Method decryptMethod = cracker.getClass().getDeclaredMethod("decrypt", String.class, File.class);
                        final String[] encryptedMessage = new String[1];

                        final ExecutorService service = Executors.newSingleThreadExecutor();
                        try {
                            final Future<Object> f = service.submit(() -> {
                                // Do you long running calculation here
                                encryptedMessage[0] = (String) decryptMethod.invoke(message, file); // Simulate some delay
                                return encryptedMessage[0];
                            });

                            System.out.println(f.get(30, TimeUnit.SECONDS));
                        } catch (final TimeoutException e) {
                            System.err.println("Calculation took to long");
                            return "cracking encrypted method \"" + message + "\" failed";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "register":
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

}
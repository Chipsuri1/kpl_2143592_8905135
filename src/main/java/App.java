import entitys.HibernateUtil;
import factory.RSACrackerFactory;
import factory.RSAFactory;
import factory.ShiftCrackerFactory;
import factory.ShiftFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.lang.reflect.Method;

public class App {
    public static void main(String[] args) {



        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the student objects
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }


    public void executeCommands(String input){
        String command = input.split(" ")[0];
        String result;
        switch (command){
            case "encrypt":
                Object encrypter;
                if(input.contains("shitft")){
                    encrypter = ShiftFactory.build();
                    try {
                        Method encryptMethod = encrypter.getClass().getMethod("encrypt");
//                        result = (String) encryptMethod.invoke(e)
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }else if(input.contains("rsa")){
                    encrypter = RSAFactory.build();
                    try {
                        Method encryptMethod = encrypter.getClass().getDeclaredMethod("encrypt");

                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
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
                if(input.contains("shift")){
                    cracker = ShiftCrackerFactory.build();
                    try {
                        Method decryptMethod = cracker.getClass().getDeclaredMethod("decrypt");
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                }else if(input.contains("rsa")){
                    cracker = RSACrackerFactory.build();
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
    }

}
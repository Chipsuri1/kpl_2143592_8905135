import entitys.HibernateUtil;
import factory.RSACrackerFactory;
import factory.ShiftCrackerFactory;
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

        switch (command){
            case "encrypt":
                break;
            case "decrypt":
                break;
            case "crack":
                Object cracker;
                if(input.contains("shift")){
                    cracker = ShiftCrackerFactory.build();
                    Method decryptMethod = cracker.getClass().getDeclaredMethod("decrypt");

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
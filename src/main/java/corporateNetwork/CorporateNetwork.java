package corporateNetwork;

import base.Configuration;
import base.LogEngine;
import com.google.common.eventbus.EventBus;
import entitys.*;
import entitys.Channel;
import entitys.Message;
import event.*;
import factory.RSACrackerFactory;
import factory.RSAFactory;
import factory.ShiftCrackerFactory;
import factory.ShiftFactory;
import org.hibernate.Query;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CorporateNetwork {

    private EventBus eventBus;
    private AppForGUI app;
    private HashMap<String, ParticipantSubscriber> participantSubscriberHashMap;
    private HashMap<String, corporateNetwork.Channel> channelHashMap;

    public CorporateNetwork(AppForGUI app) {
        this.app = app;
        eventBus = new EventBus();
        participantSubscriberHashMap = new HashMap<>();
        channelHashMap = new HashMap<>();
    }

    public void post(Object object) {
        eventBus.post(object);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public String receive(Encrypt event) {
        Object encryptor = null;
        String algorithm = null;
        String result = null;
        if (event.isShift()) {
            algorithm = "shift";
            encryptor = ShiftFactory.build();
            try {
                Method encryptMethod = encryptor.getClass().getDeclaredMethod("encrypt", String.class, File.class);
                result = (String) encryptMethod.invoke(encryptor, event.getMessage(), event.getFile());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (event.isRsa()) {
            algorithm = "rsa";
            encryptor = RSAFactory.build();
            try {
                String[] inputStrings = event.getMessage().split("");
                Method encryptMethod = encryptor.getClass().getDeclaredMethod("encrypt", String.class, File.class);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < inputStrings.length; i++) {
                    stringBuilder.append(encryptMethod.invoke(encryptor, inputStrings[i], event.getFile()) + " ");
                }
                result = stringBuilder.toString();

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (Configuration.instance.debugMode) {
            LogEngine.instance.init(event.getCommand() + "_" + algorithm + "_" + (System.currentTimeMillis() / 1000L));
            LogEngine.instance.writeLn("Command: " + event.getCommand() + ", algorithm: " + algorithm + ",Message: " + event.getMessage() + ", Cipher: " + result);
            LogEngine.instance.close();
        }
        return result;
    }

    public String receive(Decrypt event) {
        Object decrypter = null;
        String algorithm = null;
        String result = null;
        if (event.isShift()) {
            algorithm = "shift";
            decrypter = ShiftFactory.build();
            try {
                Method decryptMethod = decrypter.getClass().getDeclaredMethod("decrypt", String.class, File.class);
                result = (String) decryptMethod.invoke(decrypter, event.getMessage(), event.getFile());

                System.out.println(result);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (event.isRsa()) {
            algorithm = "rsa";
            decrypter = RSAFactory.build();
            String[] inputs = event.getMessage().split(" ");
            try {
                Method decryptMethod = decrypter.getClass().getDeclaredMethod("decrypt", String.class, File.class);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < inputs.length; i++) {
                    stringBuilder.append((String) decryptMethod.invoke(decrypter, inputs[i], event.getFile()));
                }
                result = stringBuilder.toString();

                System.out.println(result);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (Configuration.instance.debugMode) {
            LogEngine.instance.init(event.getCommand() + "_" + algorithm + "_" + (System.currentTimeMillis() / 1000L));
            LogEngine.instance.writeLn("Command: " + event.getCommand() + ", Message: " + event.getMessage() + ", Cipher: " + result + ", algorithm: " + algorithm);
            LogEngine.instance.close();
        }
        return result;
    }

    public String receive(CrackEncryptedMessage event) {
        if (event.isShift()) {
            return crackEncryptedMessageShift(event.getMessage());
        } else if (event.isRsa()) {
            return crackEncryptedMessageRSA(event.getMessage(), event.getFile());
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

    public String receive(Create event) {
        String input = event.getInput();

        app.startSession();
        Query query = null;
        String result = null;
        ArrayList<Participant> participants = new ArrayList<>();
        String[] inputStrings = input.split(" ");
        if (inputStrings.length == 7) {
            String channelName = inputStrings[2];
            String participantName1 = inputStrings[4];
            String participantName2 = inputStrings[6];
            app.tryToAddParticipantToList(participants, participantName1);
            app.tryToAddParticipantToList(participants, participantName2);
            if (participants.size() == 2) {
                if (participants.get(0).equals(participants.get(1))) {
                    result = participantName1 + " and " + participantName2 + " are identical – cannot create channel on itself";
                } else {
                    query = app.getSession().createQuery("from Channel C WHERE C.name = :channelName");
                    query.setParameter("channelName", channelName);
                    if (query.list().isEmpty()) {

                        query = app.getSession().createQuery("from Channel C WHERE C.participant1 = :participant1 AND C.participant2 = :participant2");
                        query.setParameter("participant1", participants.get(0));
                        query.setParameter("participant2", participants.get(1));
                        List query1List = query.list();

                        query = app.getSession().createQuery("from Channel C WHERE C.participant1 = :participant2 AND C.participant2 = :participant1");
                        query.setParameter("participant1", participants.get(0));
                        query.setParameter("participant2", participants.get(1));
                        List query2List = query.list();

                        if (query1List.isEmpty() && query2List.isEmpty()) {
                            Channel channel = new entitys.Channel(channelName, participants.get(0), participants.get(1));
                            ParticipantSubscriber participantSubscriber1 = participantSubscriberHashMap.get(participantName1);
                            ParticipantSubscriber participantSubscriber2 = participantSubscriberHashMap.get(participantName2);
                            corporateNetwork.Channel channelOfNetwork = new corporateNetwork.Channel(channelName, participantSubscriber1, participantSubscriber2);
                            channelHashMap.put(channelName, channelOfNetwork);

                            app.getSession().save(channel);
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
        app.endSession();
        return result;
}

    public String receive(Intrude event) {
        app.startSession();
        String result = null;
        boolean successful = false;
        Participant participant = null;
        Query query;
        String[] inputStrings = event.getInput().split(" ");
        String channelName = inputStrings[2];
        String participantName = inputStrings[4];

        query = app.getSession().createQuery("from Channel C WHERE C.name = :channelName");
        query.setParameter("channelName", channelName);
        if (!query.list().isEmpty()) {
            corporateNetwork.Channel channel = channelHashMap.get(channelName);
            IntruderSubscriber intruderSubscriber = (IntruderSubscriber) participantSubscriberHashMap.get(participantName);
            channel.getEventBus().register(intruderSubscriber);
        }
        result = app.executeCommands("set " + participantName+" subscribed channel " + channelName);
        app.endSession();
        return result;
    }

    public String receive(Register event) {
        String input = event.getInput();
        String result = null;
        app.startSession();
        String[] inputStrings = input.split(" ");
        if (inputStrings.length == 6) {
            String participantName = inputStrings[2];
            String typeString = inputStrings[inputStrings.length - 1];
            Query query = app.getSession().createQuery("from Participant P WHERE P.name = :participantName");
            query.setParameter("participantName", participantName);
            List resultList = query.list();
            if (resultList.isEmpty()) {
                query = app.getSession().createQuery("from Type T WHERE T.name = :typeString");
                query.setParameter("typeString", typeString);
                resultList = query.list();
                Type type = null;
                if (resultList.isEmpty()) {
                    type = new Type(typeString);
                    app.getSession().save(type);
                } else {
                    type = (Type) resultList.get(0);
                }

                Participant participant = new Participant(participantName, type);
                ParticipantSubscriber participantSubscriber = null;
                if(typeString.equals("normal")){
                    participantSubscriber = new ParticipantSubscriber(participantName, typeString);
                }else {
                    participantSubscriber = new IntruderSubscriber(participantName, typeString);
                }

                participantSubscriberHashMap.put(participantName, participantSubscriber);
                eventBus.register(participantSubscriber);

                app.getSession().save(participant);
                Postbox postbox = new Postbox(participant);
                app.getSession().save(postbox);
                result = "participant " + participantName + " with type " + typeString + " registered and postbox_" + participantName + " created";
            } else {
                result = "participant " + participantName + " already exists, using existing postbox_" + participantName;
            }
        }
        app.endSession();
        return result;
    }

    public String receive(Send event) {
        app.startSession();
        String result = null;
        String cipher = null;
        String algorithm = null;
        if (event.isShift()) {
            algorithm = "shift";
        } else if(event.isRsa()){
            algorithm = "rsa";
        }

        Query query = null;
        ArrayList<Participant> participants = new ArrayList<>();
        String[] inputStrings =  event.getInput().split("\" ")[1].split(" ");
        String participantName1 = null;
        String participantName2 = null;
        if (inputStrings.length == 9) {
            participantName1 = inputStrings[1];
            participantName2 = inputStrings[3];
            app.tryToAddParticipantToList(participants, participantName1);
            app.tryToAddParticipantToList(participants, participantName2);
            if (participants.size() == 2) {
                if (participants.get(0).equals(participants.get(1))) {
                    result = "no valid channel from " + participantName1 + " to " + participantName2;
                } else {
                    cipher = app.encrypt(algorithm, event.getMessage(), event.getFile());


                    query = app.getSession().createQuery("from Channel C WHERE C.participant1 = :participant1 AND C.participant2 = :participant2");
                    query.setParameter("participant1", participants.get(0));
                    query.setParameter("participant2", participants.get(1));
                    List queryList = query.list();
                    if (!queryList.isEmpty()) {

                        entitys.Channel channel = (entitys.Channel) queryList.get(0);
                        corporateNetwork.Channel netWorkChannel = channelHashMap.get(channel.getName());

                        netWorkChannel.post(new MessageEvent(cipher, participantSubscriberHashMap.get(participantName1), participantSubscriberHashMap.get(participantName2), app, algorithm, event.getFile()));

                        query = app.getSession().createQuery("from Algorithm A WHERE A.name = :algorithm");
                        query.setParameter("algorithm", algorithm);
                        Algorithm algorithmEntity = (Algorithm) query.list().get(0);
                        Message messageEntity = new Message(participants.get(0), participants.get(1), event.getMessage(), algorithmEntity, cipher, event.getFile().getName());
                        app.getSession().save(messageEntity);

                        result = participantName2 + " received new message";
                    } else {
                        result = "no valid channel from " + participantName1 + " to " + participantName2;
                    }
                }
            }
            app.endSession();
        }
        return result;
    }

    public String receive(Show event) {
            app.startSession();
            Query showChannelQuery = app.getSession().createQuery("FROM Channel");

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < showChannelQuery.list().size(); i++) {
                entitys.Channel channel = (Channel) showChannelQuery.list().get(i);
                stringBuilder.append(channel.getName() + " | " + channel.getParticipant1().getName() + " and " + channel.getParticipant2().getName());
                stringBuilder.append(Configuration.instance.lineSeparator);
            }
            app.endSession();
            return stringBuilder.toString();

    }

    public String receive(Drop event) {
        String result = null;
        app.startSession();
        String[] inputStrings = event.getInput().split("channel ");
        String channelNameDropQuery = inputStrings[1];
        System.out.println(channelNameDropQuery);

        Query queryDropChannel = app.getSession().createQuery("from Channel C where C.name = :channelName");
        queryDropChannel.setParameter("channelName", channelNameDropQuery);

        if (queryDropChannel.list().size() == 0) {
            result = "unknown channel " + channelNameDropQuery;
        } else {
            entitys.Channel channel = (entitys.Channel) queryDropChannel.list().get(0);
            app.getSession().delete(channel);
            result = "channel " + channelNameDropQuery + " deleted";
        }
        app.endSession();
        return result;
    }
}

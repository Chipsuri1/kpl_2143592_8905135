package corporateNetwork;

import com.google.common.eventbus.Subscribe;
import entitys.Participant;
import entitys.Postbox;
import event.MessageEvent;

import java.io.File;

public class IntruderSubscriber extends ParticipantSubscriber {
    public IntruderSubscriber(String name, String type) {
        super(name, type);
    }

    @Subscribe
    public void receive(MessageEvent event) {
        org.hibernate.query.Query queryGetParticipant = event.getApp().getSession().createQuery("from Participant P WHERE P.name = :name");
        queryGetParticipant.setParameter("name", name);
        Participant participantIntruder = (Participant) queryGetParticipant.list().get(0);

        queryGetParticipant = event.getApp().getSession().createQuery("from Participant P WHERE P.name = :name");
        queryGetParticipant.setParameter("name", event.getParticipantSubscriberFrom().name);
        Participant participantFrom = (Participant) queryGetParticipant.list().get(0);

        Postbox postbox = new Postbox(participantIntruder, "unknown");
//        postbox.setParticipantFrom(participantFrom);
        event.setFile(new File("configuration/publicKeyfile.json"));
        String message = event.getApp().crackEncryptedMessage(event.getAlgorithm(), event.getCipher(), event.getFile());

        if (message == null || message.equals("Invalid algorithm. Please try again") || message.contains("failed")) {
            event.getApp().executeCommands("set " + "intruder " + name + " | crack message from participant " + event.getParticipantSubscriberFrom().name + " failed");
        } else {
            System.out.println("intruder " + name + " cracked message from participant " + event.getParticipantSubscriberFrom().name + " | " + message);
            postbox.setMessage(message);
            event.getApp().executeCommands("set intruder " + name + " cracked message from participant " + event.getParticipantSubscriberFrom().name + " | " + message);
        }
        event.getApp().getSession().save(postbox);
    }
}

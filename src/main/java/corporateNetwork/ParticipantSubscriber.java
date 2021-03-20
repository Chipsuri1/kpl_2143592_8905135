package corporateNetwork;

import com.google.common.eventbus.Subscribe;
import entitys.Participant;
import entitys.Postbox;
import event.MessageEvent;
import event.Subscriber;
import org.hibernate.query.Query;

import java.io.File;


public class ParticipantSubscriber extends Subscriber {
    protected String name;
    protected String type;

    public ParticipantSubscriber(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Subscribe
    public void receive(MessageEvent event) {
        System.out.println(name + " received a message!");
        if (event.getParticipantSubscriberTo().equals(this)) {
            File file = new File("configuration/privateKeyfile.json");
            if(event.getAlgorithm().equals("shift")){
                file = event.getFile();
            }
            String message = event.getApp().decrypt(event.getAlgorithm(), event.getCipher(), file);

            event.getApp().startSession();
            Query queryGetParticipant = event.getApp().getSession().createQuery("from Participant P WHERE P.name = :name");
            queryGetParticipant.setParameter("name", name);
            Participant participant = (Participant) queryGetParticipant.list().get(0);

            Query queryGetPostbox = event.getApp().getSession().createQuery("from Postbox P WHERE P.participantTo = :participantTo");
            queryGetPostbox.setParameter("participantTo", participant);
            Postbox postbox = (Postbox) queryGetPostbox.list().get(0);

            if(postbox != null){
                postbox.setMessage(message);
            }
            event.getApp().endSession();
            event.getApp().executeCommands("set " + name + " received new message");
        } else {
            System.out.println("This message is not for me!");
        }
    }
}

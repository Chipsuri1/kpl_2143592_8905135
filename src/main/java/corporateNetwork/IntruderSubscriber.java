package corporateNetwork;

import com.google.common.eventbus.Subscribe;
import entitys.Postbox;
import event.MessageEvent;
import org.hibernate.Query;

public class IntruderSubscriber extends ParticipantSubscriber {
    public IntruderSubscriber(String name, String type) {
        super(name, type);
    }

    @Subscribe
    public void receive(MessageEvent event){
        event.getApp().startSession();
        Query query = event.getApp().getSession().createQuery("from Postbox P WHERE P.participantTo = :participantTo");
        query.setParameter("participantTo", this);
        Postbox postbox = (Postbox) query.list().get(0);
        postbox.setMessage("unknown");
        String message = event.getApp().crackEncryptedMessage(event.getAlgorithm(), event.getCipher(), event.getFile());

        if(message == null || message.equals("Invalid algorithm. Please try again") || message.contains("failed")){
            event.getApp().executeCommands("set " + "intruder " + name +" | crack message from participant " + event.getParticipantSubscriberFrom().name + " failed");
        }else {
            postbox.setMessage(message);
            event.getApp().executeCommands("set " + "intruder " + name + " cracked message from participant "+event.getParticipantSubscriberFrom().name + " | " + message);
        }
    }
}

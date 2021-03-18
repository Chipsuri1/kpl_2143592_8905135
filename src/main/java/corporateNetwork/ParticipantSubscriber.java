package corporateNetwork;

import com.google.common.eventbus.Subscribe;
import entitys.Postbox;
import event.MessageEvent;
import event.Subscriber;
import org.hibernate.Query;

public class ParticipantSubscriber extends Subscriber {
    protected String name;
    protected String type;

    public ParticipantSubscriber(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Subscribe
    public void receive(MessageEvent event) {
        if (event.getParticipantSubscriberTo().equals(this)) {

            //TODO decrypt without gui
            String message = event.getApp().executeCommands("decrypt message \"" + event.getCipher() + "\" using " + event.getAlgorithm() + " and keyfile " + event.getFile().getName().split("/")[1]);


            //TODO postbox eintrag erstellen aber kp ob neu oder ob alt und nur message setzen
            event.getApp().startSession();
//            Query query = event.getApp().getSession().createQuery("from Postbox P WHERE P.participantTo = :participantTo");
//            query.setParameter("participantTo", this);
//            Postbox postbox = (Postbox) query.list().get(0);
//            postbox.setMessage(message);
            event.getApp().executeCommands("set " + name + " received new message");
        } else {
            System.out.println("This message is not for me!");
        }

    }
}

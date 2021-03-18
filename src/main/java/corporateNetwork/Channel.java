package corporateNetwork;

import com.google.common.eventbus.EventBus;


public class Channel {
    private String name;
    private ParticipantSubscriber participantSubscriber1;
    private ParticipantSubscriber participantSubscriber2;
    private EventBus eventBus;

    public Channel(String name, ParticipantSubscriber participantSubscriber1, ParticipantSubscriber participantSubscriber2){
        this.name = name;
        this.participantSubscriber1 = participantSubscriber1;
        this.participantSubscriber2 = participantSubscriber2;
        eventBus = new EventBus();
        eventBus.register(participantSubscriber1);
        eventBus.register(participantSubscriber2);
    }

    public void post(Object object) {
        eventBus.post(object);
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}

package corporateNetwork;

import entitys.Type;
import event.Subscriber;

public class ParticipantSubscriber extends Subscriber {
    private String name;
    private String type;
    private String id;


    public ParticipantSubscriber(String name, String type, String id) {
        this.name = name;
        this.type = type;
        this.id = id;
    }
}

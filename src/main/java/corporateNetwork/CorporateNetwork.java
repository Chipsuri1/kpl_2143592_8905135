package corporateNetwork;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import event.EncryptMessage;

public class CorporateNetwork {
    private EventBus eventBus;

    public CorporateNetwork(){
        eventBus = new EventBus();
        eventBus.register(this);
    }

    public void post(Object object) {
        eventBus.post(object);
    }

    @Subscribe
    public void receive(EncryptMessage event) {

    }
}

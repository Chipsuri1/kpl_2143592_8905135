package event;

import com.google.common.eventbus.Subscribe;

public abstract class Subscriber {
    @Subscribe
    public abstract void receive(MessageEvent messageEvent);
}

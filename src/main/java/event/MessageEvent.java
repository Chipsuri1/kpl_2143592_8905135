package event;

import corporateNetwork.AppForGUI;
import corporateNetwork.ParticipantSubscriber;

import java.io.File;

public class MessageEvent {
    private AppForGUI app;
    private String cipher;
    private ParticipantSubscriber participantSubscriberFrom;
    private ParticipantSubscriber participantSubscriberTo;
    private String algorithm;
    private File file;


    public MessageEvent(String cipher, ParticipantSubscriber from, ParticipantSubscriber to, AppForGUI app, String algorithm, File file) {
        this.cipher = cipher;
        this.participantSubscriberFrom = from;
        this.participantSubscriberTo = to;
        this.app = app;
        this.algorithm = algorithm;
        this.file = file;
    }

    public String toString() {
        return "Event: Message";
    }

    public ParticipantSubscriber getParticipantSubscriberFrom() {
        return participantSubscriberFrom;
    }

    public ParticipantSubscriber getParticipantSubscriberTo() {
        return participantSubscriberTo;
    }

    public String getCipher() {
        return cipher;
    }

    public AppForGUI getApp() {
        return app;
    }

    public File getFile() {
        return file;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}

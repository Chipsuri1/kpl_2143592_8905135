package corporateNetwork;

public class Channel {
    private String name;
    private ParticipantSubscriber participantSubscriber1;
    private ParticipantSubscriber participantSubscriber2;

    public Channel(String name, ParticipantSubscriber participantSubscriber1, ParticipantSubscriber participantSubscriber2){
        this.name = name;
        this.participantSubscriber1 = participantSubscriber1;
        this.participantSubscriber2 = participantSubscriber2;
    }
}

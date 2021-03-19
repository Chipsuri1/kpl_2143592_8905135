package entitys;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "postbox")
public class Postbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NotNull
    private Integer id;

    public Postbox(Participant participantTo) {
        this.participantTo = participantTo;
        //TODO timestamp
        timestamp = Math.toIntExact((System.currentTimeMillis() / 1000L));
    }

    public Postbox(){
    }

    @ManyToOne
    @JoinColumn(name = "participant_to_id")
    private Participant participantTo;

    @ManyToOne
    @JoinColumn(name = "participant_from_id")
    private Participant participantFrom;

    @Column(name = "message")
    @NotNull
    private String message;

    @Column(name = "timestamp")
    private Integer timestamp;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setParticipantFrom(Participant participantFrom) {
        this.participantFrom = participantFrom;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }
}

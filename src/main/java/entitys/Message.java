package entitys;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NotNull
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "participant_from_id", nullable = false)
    private Participant participantFrom;

    @ManyToOne
    @JoinColumn(name = "participant_to_id", nullable = false)
    private Participant participantTo;

    @Column(name = "plain_message")
    @NotNull
    private String plainMessage;

    @ManyToOne
    @JoinColumn(name = "algorithm_id", nullable = false)
    private Algorithm algorithm;

    @Column(name = "encrypted_message")
    @NotNull
    private String encryptedMessage;

    @Column(name = "keyfile")
    @NotNull
    private String keyfile;

    @Column(name = "timestamp")
    private Integer timestamp;

    private Message() {}

    public Message(Participant participantFrom,
                   Participant participantTo,
                   String plainMessage,
                   Algorithm algorithm,
                   String encryptedMessage,
                   String keyfile) {
        this.participantFrom = participantFrom;
        this.participantTo = participantTo;
        this.plainMessage = plainMessage;
        this.algorithm = algorithm;
        this.encryptedMessage = encryptedMessage;
        this.keyfile = keyfile;
        this.timestamp = Math.toIntExact(Instant.now().getEpochSecond());
    }
}

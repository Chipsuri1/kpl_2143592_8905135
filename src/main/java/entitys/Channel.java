package entitys;

import com.sun.istack.NotNull;
import javax.persistence.*;

@Entity
@Table(name = "channel")
public class Channel {

    //TODO wo kommt der eventbus rein
    @Id
    @Column(name = "name", unique = true)
    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "participant_01", nullable = false)
    private Participant participant1;

    @ManyToOne
    @JoinColumn(name = "participant_02", nullable = false)
    private Participant participant2;

    public Channel() {
    }

    public Channel(String name, Participant participant1, Participant participant2) {
        this.name = name;
        this.participant1 = participant1;
        this.participant2 = participant2;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Participant getParticipant1() {
        return participant1;
    }

    private void setParticipant1(Participant participant1) {
        this.participant1 = participant1;
    }

    public Participant getParticipant2() {
        return participant2;
    }

    private void setParticipant2(Participant participant2) {
        this.participant2 = participant2;
    }
}

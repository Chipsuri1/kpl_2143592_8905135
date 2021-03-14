package entitys;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "algorithms")
public class Algorithm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NotNull
    private Integer id;

    @Column(name = "name", unique = true)
    @NotNull
    private String name;

    public Algorithm(String name) {
        this.name = name;
    }

    private Algorithm() {
    }

    private void setId(Integer id) {
        this.id = id;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

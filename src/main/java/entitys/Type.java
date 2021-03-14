package entitys;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "types")
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NotNull
    public Integer id;

    @Column(name = "name", unique = true)
    @NotNull
    private String name;

    public Type(String name) {
        this.name = name;
    }

    private Type() {
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setId(Integer id) {
        this.id = id;
    }
}

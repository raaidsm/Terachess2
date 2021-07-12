package raaidsm.spring.test.database;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String colourName;

    public User() {}

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getColourName() {
        return colourName;
    }
    public void setColourName(String colourName) {
        this.colourName = colourName;
    }
}
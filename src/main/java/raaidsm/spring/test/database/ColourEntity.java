package raaidsm.spring.test.database;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "colours")
public class ColourEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "colour_name")
    private String colourName;

    public ColourEntity() {}

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
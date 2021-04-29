package raaidsm.spring.test.models;

import java.io.Serializable;

public class NumForm implements Serializable {
    private int id = 0;

    public NumForm() {}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
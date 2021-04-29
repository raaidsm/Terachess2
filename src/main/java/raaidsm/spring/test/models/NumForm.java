package raaidsm.spring.test.models;

import java.io.Serializable;

public class NumForm implements Serializable {
    private int num = 0;

    public NumForm() {}

    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
}
package raaidsm.spring.test.models.managers;

import raaidsm.spring.test.models.Square;

import java.util.HashMap;

public class BoardManager {
    public final HashMap<String, Square> board;

    public BoardManager(HashMap<String, Square> board) {
        this.board = board;
    }
}
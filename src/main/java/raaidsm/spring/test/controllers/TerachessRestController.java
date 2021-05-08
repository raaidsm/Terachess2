package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raaidsm.spring.test.models.MoveForm;
import raaidsm.spring.test.models.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

@RestController
public class TerachessRestController {
    private final Logger logger = LoggerFactory.getLogger(TerachessController.class);
    private HashMap<String, Piece> board;

    public TerachessRestController() {}

    private void setInitialBoard() {
        board = new HashMap<String, Piece>(Map.ofEntries(
                //Add all the initial pieces starting from the top left

                //Black first rank
                entry("A8", new Piece("rook", "black", "A8")),
                entry("B8", new Piece("knight", "black", "B8")),
                entry("C8", new Piece("bishop", "black", "C8")),
                entry("D8", new Piece("queen", "black", "D8")),
                entry("E8", new Piece("king", "black", "E8")),
                entry("F8", new Piece("bishop", "black", "F8")),
                entry("G8", new Piece("knight", "black", "G8")),
                entry("H8", new Piece("rook", "black", "H8")),
                //Black second rank
                entry("A7", new Piece("pawn", "black", "A7")),
                entry("B7", new Piece("pawn", "black", "B7")),
                entry("C7", new Piece("pawn", "black", "C7")),
                entry("D7", new Piece("pawn", "black", "D7")),
                entry("E7", new Piece("pawn", "black", "E7")),
                entry("F7", new Piece("pawn", "black", "F7")),
                entry("G7", new Piece("pawn", "black", "G7")),
                entry("H7", new Piece("pawn", "black", "H7")),
                //White second rank
                entry("A2", new Piece("pawn", "black", "A2")),
                entry("B2", new Piece("pawn", "black", "B2")),
                entry("C2", new Piece("pawn", "black", "C2")),
                entry("D2", new Piece("pawn", "black", "D2")),
                entry("E2", new Piece("pawn", "black", "E2")),
                entry("F2", new Piece("pawn", "black", "F2")),
                entry("G2", new Piece("pawn", "black", "G2")),
                entry("H2", new Piece("pawn", "black", "H2")),
                //White first rank
                entry("A1", new Piece("rook", "black", "A1")),
                entry("B1", new Piece("knight", "black", "B1")),
                entry("C1", new Piece("bishop", "black", "C1")),
                entry("D1", new Piece("queen", "black", "D1")),
                entry("E1", new Piece("king", "black", "E1")),
                entry("F1", new Piece("bishop", "black", "F1")),
                entry("G1", new Piece("knight", "black", "G1")),
                entry("H1", new Piece("rook", "black", "H1"))
        ));
    }

    @PostMapping(value="/ReadFirstPieceSelection", produces="application/json")
    public List<String> readFirstPieceSelection(@ModelAttribute MoveForm form) {
        //Receive the square name of a piece and return all legal moves for that piece
        logger.trace("readFirstPieceSelection() runs");

        //TODO: For now, returning empty list
        return new ArrayList<>();
    }
    @PostMapping(value="/ReadMove")
    public void readMove(@ModelAttribute MoveForm form) {
        //Receive names of the two squares clicked during a move and calculate how the board has changed
        logger.trace("readMove() runs");
    }
}
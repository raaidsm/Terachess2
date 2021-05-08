package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raaidsm.spring.test.models.MoveForm;
import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.pieces.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

@RestController
public class TerachessRestController {
    private final Logger logger = LoggerFactory.getLogger(TerachessController.class);
    private HashMap<String, Piece> board;

    public TerachessRestController() {
        setInitialBoard();
    }

    public void setInitialBoard() {
        board = new HashMap<String, Piece>(Map.ofEntries(
                //Add all the initial pieces starting from the top left

                //region Black first rank
                entry("A8", new Rook("rook", "black", "A8", board)),
                entry("B8", new Knight("knight", "black", "B8", board)),
                entry("C8", new Bishop("bishop", "black", "C8", board)),
                entry("D8", new Queen("queen", "black", "D8", board)),
                entry("E8", new King("king", "black", "E8", board)),
                entry("F8", new Bishop("bishop", "black", "F8", board)),
                entry("G8", new Knight("knight", "black", "G8", board)),
                entry("H8", new Rook("rook", "black", "H8", board)),
                //endregion
                //region Black second rank
                entry("A7", new Pawn("pawn", "black", "A7", board)),
                entry("B7", new Pawn("pawn", "black", "B7", board)),
                entry("C7", new Pawn("pawn", "black", "C7", board)),
                entry("D7", new Pawn("pawn", "black", "D7", board)),
                entry("E7", new Pawn("pawn", "black", "E7", board)),
                entry("F7", new Pawn("pawn", "black", "F7", board)),
                entry("G7", new Pawn("pawn", "black", "G7", board)),
                entry("H7", new Pawn("pawn", "black", "H7", board)),
                //endregion
                //region White second rank
                entry("A2", new Pawn("pawn", "white", "A2", board)),
                entry("B2", new Pawn("pawn", "white", "B2", board)),
                entry("C2", new Pawn("pawn", "white", "C2", board)),
                entry("D2", new Pawn("pawn", "white", "D2", board)),
                entry("E2", new Pawn("pawn", "white", "E2", board)),
                entry("F2", new Pawn("pawn", "white", "F2", board)),
                entry("G2", new Pawn("pawn", "white", "G2", board)),
                entry("H2", new Pawn("pawn", "white", "H2", board)),
                //endregion
                //region White first rank
                entry("A1", new Rook("rook", "white", "A1", board)),
                entry("B1", new Knight("knight", "white", "B1", board)),
                entry("C1", new Bishop("bishop", "white", "C1", board)),
                entry("D1", new Queen("queen", "white", "D1", board)),
                entry("E1", new King("king", "white", "E1", board)),
                entry("F1", new Bishop("bishop", "white", "F1", board)),
                entry("G1", new Knight("knight", "white", "G1", board)),
                entry("H1", new Rook("rook", "white", "H1", board))
                //endregion
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
package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raaidsm.spring.test.models.BoardManager;
import raaidsm.spring.test.models.forms.MoveForm;
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
    private BoardManager boardManager;

    public TerachessRestController() {
        boardManager = new BoardManager();
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
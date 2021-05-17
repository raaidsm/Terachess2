package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raaidsm.spring.test.models.BoardManager;
import raaidsm.spring.test.models.exceptions.CheckmateException;
import raaidsm.spring.test.models.exceptions.StalemateException;
import raaidsm.spring.test.models.forms.MoveForm;
import raaidsm.spring.test.models.utils.GameStatus;

import java.util.*;

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

        //TODO: For now, returning default value
        return new ArrayList<>(
                Arrays.asList("E3", "E4")
        );
    }
    @PostMapping(value="/ReadMove")
    public void readMove(@ModelAttribute MoveForm form) {
        //Read move and calculate changes on the board
        logger.trace("readMove() runs");

        GameStatus gameStatus = boardManager.makeMove(form.getFirstSquare(), form.getSecondSquare());
        //TODO: For now, crash the program when checkmate is achieved
        if (gameStatus == GameStatus.CHECKMATE) throw new CheckmateException();
        else if (gameStatus == GameStatus.STALEMATE) throw new StalemateException();
    }
}
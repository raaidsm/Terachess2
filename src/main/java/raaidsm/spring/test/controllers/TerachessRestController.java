package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raaidsm.spring.test.models.MoveForm;
import raaidsm.spring.test.models.SquareForm;

@RestController
public class TerachessRestController {
    private final Logger logger = LoggerFactory.getLogger(TerachessController.class);

    @PostMapping(value="/ReadSquare", produces="application/json")
    public SquareForm readSquare(@ModelAttribute SquareForm form) {
        logger.trace("readSquare runs()");

        //TODO: For now just send back the same string
        return form;
    }

    @PostMapping(value="/ReadMove", produces="application/json")
    public MoveForm readMove(@ModelAttribute MoveForm form) {
        logger.trace("readMove() runs");

        //TODO: For now just send back the same object
        return form;
    }
}
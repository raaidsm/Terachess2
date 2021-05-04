package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raaidsm.spring.test.models.SquareForm;

@RestController
public class TerachessRestController {
    private final Logger logger = LoggerFactory.getLogger(TerachessController.class);

    @PostMapping(value="/ReadSquare", produces="application/json")
    public SquareForm readSquare(@ModelAttribute SquareForm form) {
        logger.trace("readSquare runs()");

        //For now, literally just send back the square lol
        return form;
    }
}
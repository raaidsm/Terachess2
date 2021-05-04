package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raaidsm.spring.test.models.NumForm;
import raaidsm.spring.test.models.SquareForm;

@RestController
public class TerachessRestController {
    private final Logger logger = LoggerFactory.getLogger(TerachessController.class);

    @PostMapping(value= "/AffectNum", produces="application/json")
    public NumForm affectNum(@ModelAttribute NumForm form) {
        logger.trace("affectNum() runs");

        //Take the form from Index as input, double the num, and return the value
        int id = form.getNum();
        id = id * 2;
        form.setNum(id);
        return form;
    }

    @PostMapping(value="/ReadSquare", produces="application/json")
    public SquareForm readSquare(@ModelAttribute SquareForm form) {
        logger.trace("readSquare runs()");

        //For now, literally just send back the square lol
        return form;
    }
}
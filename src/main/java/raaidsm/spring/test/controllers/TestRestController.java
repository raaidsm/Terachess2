package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raaidsm.spring.test.models.NumForm;

@RestController
public class TestRestController {
    private final Logger logger = LoggerFactory.getLogger(TestController.class);

    @PostMapping(value="/ApplyMove", produces="application/json")
    public NumForm applyMove(@ModelAttribute NumForm form) {
        logger.trace("applyMove() runs");

        //Take the form from Index as input, double the num, and return the value
        int id = form.getNum();
        id = id * 2;
        form.setNum(id);
        return form;
    }
}
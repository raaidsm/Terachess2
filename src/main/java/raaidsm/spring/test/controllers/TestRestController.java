package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raaidsm.spring.test.models.NumForm;

@RestController
public class TestRestController {
    private final Logger logger = LoggerFactory.getLogger(TestController.class);

    @PostMapping(value= "/AffectNum", produces="application/json")
    public NumForm affectNum(@ModelAttribute NumForm form) {
        logger.trace("affectNum() runs");
        logger.trace("form.getNum() = " + form.getNum());

        //Take the form from Index as input, double the num, and return the value
        int id = form.getNum();
        id = id * 2;
        form.setNum(id);
        return form;
    }
}
package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import raaidsm.spring.test.models.NumForm;

@Controller
public class TestController {
    private final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping(value={"/", "/Index"})
    public String index() {
        logger.trace("index() runs");
        return "Index";
    }
}
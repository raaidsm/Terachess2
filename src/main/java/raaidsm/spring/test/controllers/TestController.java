package raaidsm.spring.test.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;

public class TestController {
    private final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping(value={"/", "/Index"})
    public String index() {
        logger.trace("index() runs");
        return "Index";
    }
}
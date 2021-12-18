package org.dbs.pi4j.myrestgpio.configuration;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Resource redirection to swagger api documentation
 */
@Controller
@Slf4j
public class SwaggerResource {

    /**
     * Default constructor
     */
    public SwaggerResource() {
        super();
    }

    @RequestMapping(value = "/")
    public String index() {
        log.info("swagger-ui.html");
        return "redirect:swagger-ui/";
    }

}

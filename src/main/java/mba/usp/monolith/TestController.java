package mba.usp.monolith;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping(value = "/customers/{cid}/orders/{oid}", method = RequestMethod.GET)
    public String test() {
        return "test";
    }
}

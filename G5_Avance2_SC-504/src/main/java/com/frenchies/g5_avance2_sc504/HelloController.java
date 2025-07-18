
package com.frenchies.g5_avance2_sc504;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "¡Frenchies funcionando!";
    }
}

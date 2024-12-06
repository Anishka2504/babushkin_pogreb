package com.tgbot.controler;

import com.tgbot.service.UserActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserActivationController {

    private final UserActivationService userActivationService;

    @GetMapping("/activation")  //only GET is allowed through browser
    public ResponseEntity<?> activateUser(@RequestParam String id) {
        boolean activated = userActivationService.activate(id);
        if (activated) {
            return ResponseEntity.ok().body("Регистрация успешно завершена");
        }
        return ResponseEntity.internalServerError().build();
    }
}


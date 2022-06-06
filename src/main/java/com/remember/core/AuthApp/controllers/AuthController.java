package com.remember.core.AuthApp.controllers;

import com.remember.core.AuthApp.dtos.UserRequestDto;
import com.remember.core.AuthApp.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String signup(@ModelAttribute @Validated UserRequestDto userRequestDto,
                        BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "redirect:forms/signup";
        }

        authService.registerNewUserAccount(userRequestDto);
        return "redirect:/";
    }


    /*
     * forms
     */
    @GetMapping("/forms/login")
    public String login(){
        return "auth/forms/login";
    }

    @GetMapping("/forms/signup")
    public String signupForm() { return "auth/forms/signup"; }
}

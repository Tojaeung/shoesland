package me.tojaeung.shoesland.controller;

import me.tojaeung.shoesland.lib.Helper;
import me.tojaeung.shoesland.dto.Response;
import me.tojaeung.shoesland.dto.request.MemberRequestDto;
import me.tojaeung.shoesland.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final Response response;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Validated MemberRequestDto.SignUp signUp, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return memberService.signUp(signUp);
    }

    // @PostMapping("/login")
    // public ResponseEntity<?> login(@Validated MemberRequestDto.Login login,
    // Errors errors) {
    // // validation check
    // if (errors.hasErrors()) {
    // return response.invalidFields(Helper.refineErrors(errors));
    // }
    // return memberService.login(login);
    // }

    // @PostMapping("/reissue")
    // public ResponseEntity<?> reissue(@Validated MemberRequestDto.Reissue reissue,
    // Errors errors) {
    // // validation check
    // if (errors.hasErrors()) {
    // return response.invalidFields(Helper.refineErrors(errors));
    // }
    // return memberService.reissue(reissue);
    // }

    // @PostMapping("/logout")
    // public ResponseEntity<?> logout() {
    // // validation check
    // return memberService.logout();
    // }

    @GetMapping("/authority")
    public ResponseEntity<?> authority() {
        log.info("ADD ROLE_ADMIN");
        return memberService.authority();
    }

    @GetMapping("/userTest")
    public ResponseEntity<?> userTest() {
        log.info("ROLE_USER TEST");
        return response.success();
    }

    @GetMapping("/adminTest")
    public ResponseEntity<?> adminTest() {
        log.info("ROLE_ADMIN TEST");
        return response.success();
    }
}

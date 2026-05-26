package com.traderapp.test;

import com.traderapp.modules.auth.application.commands.RegisterUserCommand;
import com.traderapp.modules.auth.application.commands.VerifyEmailCommand;
import com.traderapp.modules.auth.application.usecases.RegisterUser;
import com.traderapp.modules.auth.application.usecases.VerifyEmail;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/test")
@Profile("test")
public class TestController {

    private final RegisterUser registerUser;
    private final VerifyEmail verifyEmail;
    private final JdbcTemplate jdbcTemplate;

    public TestController(RegisterUser registerUser, VerifyEmail verifyEmail, JdbcTemplate jdbcTemplate) {
        this.registerUser = registerUser;
        this.verifyEmail = verifyEmail;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/seed-verified-user")
    public ResponseEntity<SeedUserResponse> seedVerifiedUser(@RequestBody SeedUserRequest request) {
        registerUser.execute(new RegisterUserCommand(
            request.firstName(), request.lastName(), request.birthDate(),
            request.email(), request.password(), request.country()
        ));

        String code = jdbcTemplate.queryForObject(
            "SELECT code FROM email_verification_codes" +
            " WHERE user_id = (SELECT id FROM users WHERE email = ?) AND used = false",
            String.class,
            request.email()
        );

        verifyEmail.execute(new VerifyEmailCommand(request.email(), code));

        return ResponseEntity.ok(new SeedUserResponse(request.email(), request.password()));
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        jdbcTemplate.update("DELETE FROM users WHERE email = ?", email);
        return ResponseEntity.noContent().build();
    }

    record SeedUserRequest(
        String email, String password,
        String firstName, String lastName,
        LocalDate birthDate, String country
    ) {}

    record SeedUserResponse(String email, String password) {}
}
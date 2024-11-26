package za.co.datacentrix.taskservice.controller;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import za.co.datacentrix.taskservice.service.JwtService;


@RestController
@RequestMapping("/api/v1/validate")
@SecurityRequirements() /*
This API won't have any security requirements. Therefore, we need to override the default security requirement configuration
with @SecurityRequirements()
*/
@RequiredArgsConstructor
@Slf4j
public class ValidateToken {

    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<String> validate(
            @RequestParam String token) {

        Claims claims = jwtService.validateToken(token);

        return ResponseEntity.ok("Token is valid");
    }
}

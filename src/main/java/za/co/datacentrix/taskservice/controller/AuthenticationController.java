package za.co.datacentrix.taskservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import za.co.datacentrix.taskservice.entities.User;
import za.co.datacentrix.taskservice.payload.request.AuthenticationRequest;
import za.co.datacentrix.taskservice.payload.request.RefreshTokenRequest;
import za.co.datacentrix.taskservice.payload.request.RegisterRequest;
import za.co.datacentrix.taskservice.payload.request.UserUpdateRequest;
import za.co.datacentrix.taskservice.payload.response.AuthenticationResponse;
import za.co.datacentrix.taskservice.payload.response.RefreshTokenResponse;
import za.co.datacentrix.taskservice.service.AuthenticationService;
import za.co.datacentrix.taskservice.service.JwtService;
import za.co.datacentrix.taskservice.service.RefreshTokenService;

import java.util.Map;


@Tag(name = "Authentication", description = "The Authentication API. Contains operations like login, logout, refresh-token etc.")
@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirements()
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;



    @PostMapping("/register")
    @Operation(summary = "Register New User",
            description = "Create new user by posting firstname, lastname, email, password, role, and BS number")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request,
                                                           @RequestParam boolean createdByAdmin,
                                                           @RequestHeader Map<String, String> headers) {
        String authorizationValue = null;

        if (headers.get("authorization") != null && headers.get("authorization").length() > 7) {
            authorizationValue = headers.get("authorization").substring(7);
        }

        // Ensure the request includes BS number
//        if (request.getBsNumber() == null || request.getBsNumber().isEmpty()) {
//            throw new RuntimeException("BS number must not be empty.");
//        }

        AuthenticationResponse authenticationResponse = authenticationService.register(request, createdByAdmin, authorizationValue);

        ResponseCookie jwtCookie = jwtService.generateJwtCookie(authenticationResponse.getAccessToken());
        ResponseCookie refreshTokenCookie = refreshTokenService.generateRefreshTokenCookie(authenticationResponse.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(authenticationResponse);
    }


    @PostMapping("/authenticate")
    @Operation(
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "401",
                            content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")}
                    )
            }
    )
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {

        log.info("Did we reach here??");

        log.info("Received authentication request for email: {}", request.getEmail());

        log.info("Received authentication request for password: {}", request.getPassword());



        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        log.info(authenticationResponse.getAccessToken() + " " + authenticationResponse.getFirstname() + " " + authenticationResponse.getEmail());

        log.info("What about here?");

        ResponseCookie jwtCookie = jwtService.generateJwtCookie(authenticationResponse.getAccessToken());
        ResponseCookie refreshTokenCookie = refreshTokenService.generateRefreshTokenCookie(authenticationResponse.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString())
                .body(authenticationResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.generateNewToken(request));
    }

    @PostMapping("/refresh-token-cookie")
    public ResponseEntity<Void> refreshTokenCookie(HttpServletRequest request) {
        String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);
        RefreshTokenResponse refreshTokenResponse = refreshTokenService
                .generateNewToken(new RefreshTokenRequest(refreshToken));
        ResponseCookie NewJwtCookie = jwtService.generateJwtCookie(refreshTokenResponse.getAccessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, NewJwtCookie.toString())
                .build();
    }
    @GetMapping("/info")
    public Authentication getAuthentication(@RequestBody AuthenticationRequest request){
        return     authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
    }

    @PostMapping("/test")
    public String getA(@RequestBody String token){
        return  "hello you have reached us: " + token;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request){
        String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);
        if(refreshToken != null) {
           refreshTokenService.deleteByToken(refreshToken);
        }
        ResponseCookie jwtCookie = jwtService.getCleanJwtCookie();
        ResponseCookie refreshTokenCookie = refreshTokenService.getCleanRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString())
                .build();

    }


    @GetMapping("user/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    //  @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN' , 'SUPERADMIN', 'TEACHER')")
    public ResponseEntity<User> getSchoolById(@PathVariable Long id) {
        User user= authenticationService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("update/id/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequest userUpdateRequest) {
        System.out.println(userUpdateRequest);
        User updatedUser = authenticationService.updateUser(userId, userUpdateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/change-password/{email}/{currentPassword}/{newPassword}")
    public ResponseEntity<String> changePassword(
            @PathVariable String email,
            @PathVariable String currentPassword,
            @PathVariable String newPassword) {
        try {
            authenticationService.changePassword(email, currentPassword, newPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

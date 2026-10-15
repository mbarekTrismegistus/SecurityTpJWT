
package ma.enset.jwt_security_tp.controller;

import ma.enset.jwt_security_tp.dto.AuthResponse;
import ma.enset.jwt_security_tp.dto.LoginRequest;
import ma.enset.jwt_security_tp.dto.RegisterRequest;
import ma.enset.jwt_security_tp.entity.Role;
import ma.enset.jwt_security_tp.entity.User;
import ma.enset.jwt_security_tp.repository.UserRepository;
import ma.enset.jwt_security_tp.service.CustomUserDetailsService;
import ma.enset.jwt_security_tp.security.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest()
                    .body("Email deja utilise");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(
                passwordEncoder.encode(request.password())
        );
        user.setRole(Role.USER);

        userRepository.save(user);

        return ResponseEntity.ok("Utilisateur cree");
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        request.email()
                );

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token);
    }
}

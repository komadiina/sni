package org.unibl.etf.sni.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.unibl.etf.sni.auth.AuthRequest;
import org.unibl.etf.sni.auth.JwtAuthResponse;
import org.unibl.etf.sni.exceptions.InvalidUsernameException;
import org.unibl.etf.sni.db.UserRepository;

@Service
public class AuthService {
    private UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public JwtAuthResponse login(AuthRequest request, String remoteAddr, String userAgent) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidUsernameException("Invalid username or password."));

        var jwt = jwtService.generateToken(user, remoteAddr, userAgent);
        return new JwtAuthResponse(jwt);
    }
}

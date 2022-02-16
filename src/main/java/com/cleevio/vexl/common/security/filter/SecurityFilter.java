package com.cleevio.vexl.common.security.filter;

import com.cleevio.vexl.common.security.AuthenticationHolder;
import com.cleevio.vexl.module.user.exception.DigitalSignatureException;
import com.cleevio.vexl.module.user.service.SignatureService;
import com.cleevio.vexl.module.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityFilter extends OncePerRequestFilter {

    private final SignatureService signatureService;
    private final UserService userService;

    public SecurityFilter(SignatureService signatureService,
                          UserService userService) {
        this.signatureService = signatureService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String publicKey = request.getHeader("public-key");
        String phoneHash = request.getHeader("phone-hash");
        String signature = request.getHeader("signature");

        if (signature == null || publicKey == null || phoneHash == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (signatureService.isSignatureValid(publicKey, phoneHash, signature)) {
                AuthenticationHolder authentication = userService
                        .findByPublicKey(publicKey)
                        .map(user -> {
                            AuthenticationHolder authenticationHolder = new AuthenticationHolder(user);
                            authenticationHolder.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            return authenticationHolder;
                        })
                        .orElse(null);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (DigitalSignatureException e) {
            SecurityContextHolder.clearContext();
            //todo handleError
        }

        filterChain.doFilter(request, response);
    }
}
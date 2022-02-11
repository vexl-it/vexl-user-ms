package com.cleevio.vexl.common.security.filter;

import com.cleevio.vexl.common.security.AuthenticationHolder;
import com.cleevio.vexl.module.user.service.SignatureService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class SecurityFilter extends OncePerRequestFilter {

    private final SignatureService signatureService;

    public SecurityFilter(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String publicKeyPhoneHash = request.getHeader("pkphonehash");
        String signature = request.getHeader("signature");

        if (signature == null && publicKeyPhoneHash == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (signatureService.isValid(publicKeyPhoneHash, signature)) {
                AuthenticationHolder authentication = new AuthenticationHolder("username");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            SecurityContextHolder.clearContext();
            //todo handleError
        }

        filterChain.doFilter(request, response);
    }
}

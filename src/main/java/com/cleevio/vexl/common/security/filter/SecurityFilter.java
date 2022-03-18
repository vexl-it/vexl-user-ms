package com.cleevio.vexl.common.security.filter;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.common.security.AuthenticationHolder;
import com.cleevio.vexl.module.user.enums.AlgorithmEnum;
import com.cleevio.vexl.module.user.exception.DigitalSignatureException;
import com.cleevio.vexl.module.user.service.SignatureService;
import com.cleevio.vexl.module.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

public class SecurityFilter extends OncePerRequestFilter {

    public static final String HEADER_PUBLIC_KEY = "public-key";
    public static final String HEADER_HASH = "hash";
    public static final String HEADER_SIGNATURE = "signature";

    private final SignatureService signatureService;
    private final UserService userService;

    public SecurityFilter(SignatureService signatureService,
                          UserService userService) {
        this.signatureService = signatureService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();

        String publicKey = request.getHeader(HEADER_PUBLIC_KEY);
        String phoneHash = request.getHeader(HEADER_HASH);
        String signature = request.getHeader(HEADER_SIGNATURE);

        if (signature == null || publicKey == null || phoneHash == null || !requestURI.contains("/api/v1")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (signatureService.isSignatureValid(publicKey, phoneHash, signature, AlgorithmEnum.EdDSA.getValue(), AlgorithmEnum.EdDSA.getValue())) {
                AuthenticationHolder authentication = userService
                        .findByBase64PublicKey(publicKey)
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
            handleError(response, "Signature verification failed: " + e.getMessage(), Integer.parseInt(e.getErrorCode()));
        }

        filterChain.doFilter(request, response);
    }

    protected void handleError(ServletResponse response, String s, int code) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(code);

        ErrorResponse error = new ErrorResponse(Collections.singleton(s), "0");
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, error);
        out.flush();

        throw new RuntimeException();
    }
}
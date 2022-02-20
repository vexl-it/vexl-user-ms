package com.cleevio.vexl.module.user.controller;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.module.user.dto.request.ChallengeRequest;
import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.UserCreateRequest;
import com.cleevio.vexl.module.user.dto.request.UsernameAvailableRequest;
import com.cleevio.vexl.module.user.dto.response.PhoneConfirmResponse;
import com.cleevio.vexl.module.user.dto.response.ConfirmCodeResponse;
import com.cleevio.vexl.module.user.dto.response.SignatureResponse;
import com.cleevio.vexl.module.user.dto.response.UserResponse;
import com.cleevio.vexl.module.user.dto.response.UsernameAvailableResponse;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.enums.AlgorithmEnum;
import com.cleevio.vexl.module.user.exception.VerificationNotFoundException;
import com.cleevio.vexl.module.user.exception.DigitalSignatureException;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.module.user.exception.UserNotFoundException;
import com.cleevio.vexl.module.user.service.ChallengeService;
import com.cleevio.vexl.module.user.service.SignatureService;
import com.cleevio.vexl.module.user.service.UserService;
import com.cleevio.vexl.module.user.service.UserVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@Tag(name = "User")
@RestController
@RequestMapping(value = "/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserVerificationService userVerificationService;
    private final ChallengeService challengeService;
    private final SignatureService signatureService;

    @PostMapping("/confirm/phone")
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Phone number confirmation")
    PhoneConfirmResponse requestConfirmPhone(@Valid @RequestBody PhoneConfirmRequest phoneConfirmRequest)
            throws NoSuchAlgorithmException {
        return new PhoneConfirmResponse(this.userVerificationService.requestConfirmPhone(phoneConfirmRequest));
    }

    @PostMapping("/confirm/code")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "409 (100101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Code number confirmation, if valid, generate challenge")
    ConfirmCodeResponse confirmCodeAndGenerateCodeChallenge(@Valid @RequestBody CodeConfirmRequest codeConfirmRequest)
            throws NoSuchAlgorithmException, UserAlreadyExistsException {
        return this.userVerificationService.requestConfirmCodeAndGenerateCodeChallenge(codeConfirmRequest);
    }

    @PostMapping("/confirm/challenge")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404 (100103)", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404 (100104)", description = "Verification not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (100105)", description = "Signature could not be generated", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Verify code challenge, if valid, generate certificate")
    SignatureResponse verifyChallengeAndGenerateSignature(@Valid @RequestBody ChallengeRequest challengeRequest)
            throws UserNotFoundException, DigitalSignatureException, VerificationNotFoundException {

        User user = this.userService.findByPublicKey(challengeRequest.getUserPublicKey())
                .orElseThrow(UserNotFoundException::new);

        if (this.challengeService.isSignedChallengeValid(user, challengeRequest)) {
            return this.signatureService.createSignature(user, AlgorithmEnum.EdDSA.getValue());
        }
        return new SignatureResponse(false);
    }

    @PostMapping("/username/available")
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Is username available")
    UsernameAvailableResponse usernameAvailable(@Valid @RequestBody UsernameAvailableRequest usernameAvailableRequest) {
        return new UsernameAvailableResponse(this.userService.existsUserByUsername(usernameAvailableRequest.getUsername()));
    }

    @PostMapping
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "201", description = "User has been created"),
            @ApiResponse(responseCode = "409 (100101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Register as a new user")
    ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreateRequest userCreateRequest,
                                          @Parameter(hidden = true) @AuthenticationPrincipal User user)
            throws UserAlreadyExistsException {
        return new ResponseEntity<>(new UserResponse(this.userService.create(user, userCreateRequest)), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Get an user")
    UserResponse getMe(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return new UserResponse(user);
    }

    @PutMapping("/me")
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "409 (100101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Update an user")
    UserResponse updateMe(@Valid @RequestBody UserCreateRequest userCreateRequest,
                          @Parameter(hidden = true) @AuthenticationPrincipal User user)
            throws UserAlreadyExistsException {
        return new UserResponse(this.userService.update(user, userCreateRequest));
    }

    @DeleteMapping("/me")
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Remove an user")
    void removeMe(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        this.userService.remove(user);
    }

}

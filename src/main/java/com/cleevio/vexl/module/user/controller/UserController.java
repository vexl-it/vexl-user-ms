package com.cleevio.vexl.module.user.controller;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.user.dto.UserData;
import com.cleevio.vexl.module.user.dto.request.ChallengeRequest;
import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.UserCreateRequest;
import com.cleevio.vexl.module.user.dto.request.UserUpdateRequest;
import com.cleevio.vexl.module.user.dto.response.PhoneConfirmResponse;
import com.cleevio.vexl.module.user.dto.response.ConfirmCodeResponse;
import com.cleevio.vexl.module.user.dto.response.SignatureResponse;
import com.cleevio.vexl.module.user.dto.response.UserResponse;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.ChallengeGenerationException;
import com.cleevio.vexl.module.user.exception.UserPhoneInvalidException;
import com.cleevio.vexl.module.user.exception.VerificationExpiredException;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.module.user.exception.UserNotFoundException;
import com.cleevio.vexl.module.user.service.SignatureService;
import com.cleevio.vexl.module.user.service.UserService;
import com.cleevio.vexl.module.user.service.UserVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User")
@RestController
@RequestMapping(value = "/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserVerificationService userVerificationService;
    private final SignatureService signatureService;

    @PostMapping("/confirmation/phone")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400 (100110)", description = "User phone number is invalid", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Operation(summary = "Phone number confirmation")
    PhoneConfirmResponse requestConfirmPhone(@RequestBody PhoneConfirmRequest phoneConfirmRequest)
            throws UserPhoneInvalidException {
        return new PhoneConfirmResponse(this.userVerificationService.requestConfirmPhone(phoneConfirmRequest));
    }

    @PostMapping("/confirmation/code")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "409 (100101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500 (100106)", description = "Challenge couldn't be generated", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404 (100104)", description = "Verification not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Operation(
            summary = "Code number confirmation.",
            description = "If code number is valid, we will generate challenge for user. Challenge is used to verify that the public key is really his. "
    )
    ConfirmCodeResponse confirmCodeAndGenerateCodeChallenge(@RequestBody CodeConfirmRequest codeConfirmRequest)
            throws UserAlreadyExistsException, ChallengeGenerationException, VerificationExpiredException {
        return new ConfirmCodeResponse(this.userVerificationService.requestConfirmCodeAndGenerateCodeChallenge(codeConfirmRequest));
    }

    @PostMapping("/confirmation/challenge")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404 (100103)", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404 (100104)", description = "Verification not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (100105)", description = "Signature could not be generated", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406 (100108)", description = "Server could not create message for signature. Public key or hash is invalid.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Operation(summary = "Verify challenge.", description = "If challenge is verified successfully, we will create certificate for user.")
    SignatureResponse verifyChallengeAndGenerateSignature(@RequestBody ChallengeRequest challengeRequest) {
        final UserData userData = this.userService.findValidUserWithChallenge(challengeRequest);

        return new SignatureResponse(this.signatureService.createSignature(userData));
    }

    @GetMapping("/signature/{facebookId}")
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400 (100105)", description = "Signature could not be generated", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406 (100108)", description = "Server could not create message for signature. Public key or hash is invalid.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Operation(summary = "Generate signature for Facebook.")
    SignatureResponse generateSignature(@AuthenticationPrincipal User user,
                                        @PathVariable String facebookId) {

        return new SignatureResponse(this.signatureService.createSignature(
                user.getPublicKey(),
                facebookId,
                false
        ));
    }

    @PostMapping
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User has been created"),
            @ApiResponse(responseCode = "409 (100109)", description = "Username is not available. Choose different username.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register as a new user")
    UserResponse register(@RequestBody UserCreateRequest userCreateRequest,
                          @AuthenticationPrincipal User user) {
        return new UserResponse(this.userService.create(user, userCreateRequest));
    }

    @GetMapping("/me")
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Get an user")
    UserResponse getMe(@AuthenticationPrincipal User user) {
        return new UserResponse(user);
    }

    @PutMapping("/me")
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "409 (100109)", description = "Username is not available. Choose different username.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Update an user")
    UserResponse updateMe(@RequestBody UserUpdateRequest userCreateRequest,
                          @AuthenticationPrincipal User user) {
        return new UserResponse(this.userService.update(user, userCreateRequest));
    }

    @DeleteMapping("/me")
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Remove an user")
    void removeMe(@AuthenticationPrincipal User user) {
        this.userService.remove(user);
    }

    @DeleteMapping("/me/avatar")
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Remove my avatar")
    void removeMyAvatar(@AuthenticationPrincipal User user) {
        this.userService.removeAvatar(user);
    }

}

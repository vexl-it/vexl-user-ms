package com.cleevio.vexl.module.user.controller;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.module.user.dto.request.CodeConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.PhoneConfirmRequest;
import com.cleevio.vexl.module.user.dto.request.UserCreateRequest;
import com.cleevio.vexl.module.user.dto.request.UsernameAvailableRequest;
import com.cleevio.vexl.module.user.dto.response.PhoneConfirmResponse;
import com.cleevio.vexl.module.user.dto.response.PublicKeyResponse;
import com.cleevio.vexl.module.user.dto.response.ConfirmCodeResponse;
import com.cleevio.vexl.module.user.dto.response.UserResponse;
import com.cleevio.vexl.module.user.dto.response.UsernameAvailableResponse;
import com.cleevio.vexl.module.user.service.SignatureService;
import com.cleevio.vexl.module.user.service.UserService;
import com.cleevio.vexl.module.user.service.UserVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

@Tag(name = "User")
@RestController
@RequestMapping(value = "/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserVerificationService userVerificationService;
    private final SignatureService signatureService;

    @PostMapping("/confirm/phone")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Phone number confirmation")
    PhoneConfirmResponse requestConfirmPhone(@Valid @RequestBody PhoneConfirmRequest phoneConfirmRequest) {
        return new PhoneConfirmResponse(this.userVerificationService.requestConfirmPhone(phoneConfirmRequest));
    }

    @PostMapping("/confirm/code")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Code number confirmation, if valid, generate certificate")
    ConfirmCodeResponse requestConfirmCodeAndGenerateCertificate(@Valid @RequestBody CodeConfirmRequest codeConfirmRequest)
            throws NoSuchAlgorithmException, IOException, SignatureException, InvalidKeySpecException, InvalidKeyException {
        return this.userVerificationService.requestConfirmCodeAndGenerateCert(codeConfirmRequest);
    }

    @PostMapping("/username/available")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Is username available")
    UsernameAvailableResponse usernameAvailable(@Valid @RequestBody UsernameAvailableRequest usernameAvailableRequest) {
        return new UsernameAvailableResponse(this.userService.isUsernameAvailable(usernameAvailableRequest.getUsername()));
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Register as a new user")
    UserResponse register(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        return new UserResponse(this.userService.create(userCreateRequest));
    }

    @PutMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Update an user")
    UserResponse update(@PathVariable long id,
                        @Valid @RequestBody UserCreateRequest userCreateRequest) {
        return new UserResponse(this.userService.update(id, userCreateRequest));
    }

    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Remove an user")
    void remove(@PathVariable long id) {
        this.userService.remove(id);
    }

    @GetMapping("/{id}/publickey")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get a user's public key")
    PublicKeyResponse retrievePublicKey(@PathVariable long id) {
        return new PublicKeyResponse(this.userService.retrievePublicKeyByUserId(id));
    }

    @GetMapping("/{id}/data")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Export all known user's data")
    UserResponse exportKnownData(@PathVariable long id) {
        return new UserResponse(this.userService.find(id));
    }
}

package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.constant.ModuleLockNamespace;
import com.cleevio.vexl.common.service.AdvisoryLockService;
import com.cleevio.vexl.module.file.service.ImageService;
import com.cleevio.vexl.module.user.dto.UserData;
import com.cleevio.vexl.module.user.dto.request.ChallengeRequest;
import com.cleevio.vexl.module.user.dto.request.UserCreateRequest;
import com.cleevio.vexl.module.user.dto.request.UserUpdateRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.constant.UserAdvisoryLock;
import com.cleevio.vexl.module.user.event.UserRemovedEvent;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.module.user.exception.UserNotFoundException;
import com.cleevio.vexl.module.user.exception.VerificationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Service for processing of user - creating, updating, searching, deleting.
 * <p>
 * Life cycle of user:
 * 1. user's phone number is verified, so we prepare user - that means we create user entity with public key
 * 2. user's challenge is verified, so user can create his account - that means we add values as username and avatar to his account
 * 3. user log out of application, we delete user
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final AdvisoryLockService advisoryLockService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public User create(User user, @Valid UserCreateRequest dto) {
        advisoryLockService.lock(
                ModuleLockNamespace.USER,
                UserAdvisoryLock.CREATE_USER.name(),
                dto.username()
        );

        log.info("Creating user [{}]", user);

        if (dto.avatar() != null) {
            final String destination = this.imageService.save(dto.avatar());
            user.setAvatar(destination);
        }

        user.setUsername(dto.username());

        final User savedUser = this.userRepository.save(user);
        log.info("User [{}] has been successfully created.",
                savedUser);

        return savedUser;
    }

    @Transactional
    public User prepareUser(String publicKey)
            throws UserAlreadyExistsException {
        advisoryLockService.lock(
                ModuleLockNamespace.USER,
                UserAdvisoryLock.PREPARE_USER.name(),
                publicKey
        );

        if (this.userRepository.existsUserByPublicKey(publicKey)) {
            throw new UserAlreadyExistsException();
        }

        final User user = User.builder()
                .publicKey(publicKey)
                .build();

        final User savedUser = this.userRepository.save(user);
        log.info("User with ID {} is prepared.", user.getId());

        return savedUser;
    }

    @Transactional
    public User update(User user, @Valid UserUpdateRequest userUpdateRequest) {
        advisoryLockService.lock(
                ModuleLockNamespace.USER,
                UserAdvisoryLock.UPDATE_USER.name(),
                userUpdateRequest.username() != null ? userUpdateRequest.username() : user.getPublicKey()
        );

        log.info("Updating an user {}", user);

        if (userUpdateRequest.username() != null && !userUpdateRequest.username().equals(user.getUsername())) {
            user.setUsername(userUpdateRequest.username());
        }

        if (userUpdateRequest.avatar() != null) {
            if (user.getAvatar() != null) {
                this.imageService.removeAvatar(user.getAvatar());
            }
            final String destination = this.imageService.save(userUpdateRequest.avatar());
            user.setAvatar(destination);
        }

        final User updatedUser = this.userRepository.save(user);
        log.info("User {} was successfully updated.",
                updatedUser);

        return updatedUser;
    }

    public void remove(User user) {
        this.userRepository.delete(user);
        applicationEventPublisher.publishEvent(new UserRemovedEvent(user));
    }

    @Transactional(readOnly = true)
    public User getById(long id)
            throws UserNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByPublicKey(String publicKey) {
        return this.userRepository.findByPublicKey(publicKey);
    }

    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        return this.userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserData findValidUserWithChallenge(@Valid  ChallengeRequest request) {
        final User user = findByPublicKey(request.userPublicKey())
                .orElseThrow(UserNotFoundException::new);

        if (user.getUserVerification() == null || user.getUserVerification().getChallenge() == null) {
            throw new VerificationNotFoundException();
        }

        return new UserData(
                user.getPublicKey(),
                user.getUserVerification().getPhoneNumber(),
                user.getUserVerification().getChallenge(),
                request.signature()
        );
    }
}

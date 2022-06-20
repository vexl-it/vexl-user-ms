package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.file.exception.FileWriteException;
import com.cleevio.vexl.module.file.service.ImageService;
import com.cleevio.vexl.module.user.dto.request.UserCreateRequest;
import com.cleevio.vexl.module.user.dto.request.UserUpdateRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.module.user.exception.UserNotFoundException;
import com.cleevio.vexl.module.user.exception.UsernameNotAvailable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;

    @Transactional(rollbackFor = Exception.class)
    public User create(User user, UserCreateRequest dto)
            throws UsernameNotAvailable, FileWriteException {
        log.info("Creating user {}", user.getId());

        if (existsUserByUsername(dto.getUsername())) {
            log.warn("Username {} is not available. Username must be unique.",
                    dto.getUsername());
            throw new UsernameNotAvailable();
        }

        if (dto.getAvatar() != null) {
            String destination = this.imageService.save(dto.getAvatar());
            user.setAvatar(destination);
        }

        user.setUsername(dto.getUsername());

        User savedUser = this.userRepository.save(user);
        log.info("User {} has been successfully created.",
                savedUser.getId());

        return savedUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public User prepareUser(String publicKey)
            throws UserAlreadyExistsException {

        if (this.userRepository.existsUserByPublicKey(publicKey)) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
                .publicKey(publicKey)
                .build();

        User savedUser = this.userRepository.save(user);
        log.info("User with ID {} is prepared.", user.getId());

        return savedUser;
    }

    @Transactional(readOnly = true)
    public boolean existsUserByUsername(String username) {
        return this.userRepository.existsUserByUsername(username);
    }

    @Transactional(rollbackFor = Exception.class)
    public User update(User user, UserUpdateRequest userUpdateRequest)
            throws UsernameNotAvailable, FileWriteException {
        log.info("Updating user {}", user.getId());

        if (userUpdateRequest.username() != null && !userUpdateRequest.username().equals(user.getUsername())) {
            if (existsUserByUsername(userUpdateRequest.username())) {
                log.warn("Username {} is not available. Username must be unique.",
                        userUpdateRequest.username());
                throw new UsernameNotAvailable();
            }
            user.setUsername(userUpdateRequest.username());
        }

        if (userUpdateRequest.avatar() != null) {
            this.imageService.removeAvatar(user.getAvatar());
            String destination = this.imageService.save(userUpdateRequest.avatar());
            user.setAvatar(destination);
        }

        User updatedUser = this.userRepository.save(user);
        log.info("User {} was successfully updated.",
                updatedUser.getId());

        return updatedUser;
    }

    public void remove(User user) {
        if (user.getAvatar() != null) {
            this.imageService.removeAvatar(user.getAvatar());
        }
        this.userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public User find(long id)
            throws UserNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public Optional<User> findByBase64PublicKey(String publicKeyBase64) {
        return this.findByPublicKey(publicKeyBase64);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByPublicKey(String publicKey) {
        return this.userRepository.findByPublicKey(publicKey);
    }

    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        return this.userRepository.save(user);
    }

}

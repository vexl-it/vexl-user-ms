package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.dto.request.UserCreateRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.UserCreationException;
import com.cleevio.vexl.module.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public User create(User user, UserCreateRequest userCreateRequest)
            throws UserCreationException {
        if (existsUserByUsername(userCreateRequest.getUsername())) {
            log.error(String.format(
                    "Username %s is not available. Username must be unique.",
                    userCreateRequest.getUsername())
            );
            throw new UserCreationException();
        }

        user.setAvatar(userCreateRequest.getAvatar());
        user.setUsername(userCreateRequest.getUsername());

        return this.userRepository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void prepareUserWithPublicKey(String publicKey)
            throws UserCreationException {

        if (this.userRepository.existsUserByPublicKey(publicKey)) {
            throw new UserCreationException();
        }

        User user = User.builder()
                .publicKey(publicKey)
                .build();

        this.userRepository.save(user);
    }

    public boolean existsUserByUsername(String username) {
        return this.userRepository.existsUserByUsername(username);
    }

    @Transactional(rollbackFor = Exception.class)
    public User update(User user, UserCreateRequest userCreateRequest)
            throws UserCreationException {

        if (userCreateRequest.getUsername() != null) {
            if (existsUserByUsername(userCreateRequest.getUsername())) {
                log.error(String.format(
                        "Username %s is not available. Username must be unique.",
                        userCreateRequest.getUsername())
                );
                throw new UserCreationException();
            }
            user.setUsername(userCreateRequest.getUsername());
        }

        if (userCreateRequest.getAvatar() != null) {
            user.setAvatar(userCreateRequest.getAvatar());
        }

        return this.userRepository.save(user);
    }

    public void remove(User user) {
        this.userRepository.delete(user);
    }

    public User find(long id)
            throws UserNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public Optional<User> findByPublicKey(String publicKey) {
        log.info("Retrieving user with public key {}",
                publicKey);

        return userRepository.findByPublicKey(publicKey);
    }

}

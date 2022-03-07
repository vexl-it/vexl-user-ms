package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.user.dto.request.UserCreateRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.module.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@IntegrationTest
public class UserServiceTest {

    private final static String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEzIdBL0Q/P+OEk84pJTaEIwro2mY9Y3JihBzNlMn5jTxVtzyi0MEepbgu57Z5nBZG6kNo0D8FTrY0Oe/2niL13w==";

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    private UserService userService;

    @BeforeEach
    public void setup() {
        this.userService = new UserService(userRepository);
    }

    @Test
    void createTest() throws UserAlreadyExistsException {
        Mockito.when(userService.existsUserByUsername(user.getUsername())).thenReturn(false);
        userService.create(user, UserCreateRequest.of(user.getUsername(), user.getAvatar()));
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void findTest() throws UserNotFoundException {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());

        User response = userService.find(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(this.user.getId());
        assertThat(response.getUsername()).isEqualTo(this.user.getUsername());
        assertThat(response.getPublicKey()).isEqualTo(this.user.getPublicKey());

        assertThrows(UserNotFoundException.class, () -> userService.find(2L));
    }

    @Test
    void prepareUserTest() throws UserAlreadyExistsException {
        Mockito.when(userService.existsUserByUsername(user.getUsername())).thenReturn(false);
        userService.prepareUser(PUBLIC_KEY);
        Mockito.verify(userRepository).save(any());
    }
}

package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.util.CreateRequestUtilTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceIT {

    private static final String USER_PUBLIC_KEY_1 = "dummy_user_public_key_1";
    private static final String USER_PUBLIC_KEY_2 = "dummy_user_public_key_2";
    private static final String USER_NAME_1 = "dummy_user_name_1";
    private static final String USER_NAME_2 = "dummy_user_name_2";
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceIT(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Test
    void testPrepareAndCreateUser_shouldCreateUser() {
        final User prepareUser = userService.prepareUser(USER_PUBLIC_KEY_1);
        final Long userId = prepareUser.getId();
        final User savedPreparedUser = userRepository.findById(userId).get();

        assertThat(savedPreparedUser.getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(savedPreparedUser.getUsername()).isNull();
        assertThat(savedPreparedUser.getAvatar()).isNull();

        userService.create(savedPreparedUser, CreateRequestUtilTest.createUserCreateRequest(USER_NAME_1));
        final User finalUser = userRepository.findById(userId).get();

        assertThat(finalUser.getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(finalUser.getUsername()).isEqualTo(USER_NAME_1);
        assertThat(finalUser.getAvatar()).isNull();
    }

    @Test
    void testPrepareDuplicatedUser_shouldReturnException() {
        userService.prepareUser(USER_PUBLIC_KEY_1);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.prepareUser(USER_PUBLIC_KEY_1)
        );
    }

    @Test
    void testCreateUserWithAlreadyUsedNickname_shouldBeCreateD() {
        final User prepareUser = userService.prepareUser(USER_PUBLIC_KEY_1);
        final User prepareUser2 = userService.prepareUser(USER_PUBLIC_KEY_2);
        final Long userId = prepareUser.getId();
        final Long userId2 = prepareUser2.getId();
        final User savedPreparedUser = userRepository.findById(userId).get();
        final User savedPreparedUser2 = userRepository.findById(userId2).get();

        assertThat(savedPreparedUser.getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(savedPreparedUser.getUsername()).isNull();
        assertThat(savedPreparedUser.getAvatar()).isNull();

        userService.create(savedPreparedUser, CreateRequestUtilTest.createUserCreateRequest(USER_NAME_1));

        userService.create(savedPreparedUser2, CreateRequestUtilTest.createUserCreateRequest(USER_NAME_1));

        final List<User> users = userRepository.findAll();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getUsername()).isEqualTo(USER_NAME_1);
        assertThat(users.get(0).getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(users.get(1).getPublicKey()).isEqualTo(USER_PUBLIC_KEY_2);
        assertThat(users.get(1).getUsername()).isEqualTo(USER_NAME_1);
    }


    @Test
    void testUpdateUser_shouldUpdateUser() {
        final User prepareUser = userService.prepareUser(USER_PUBLIC_KEY_1);
        final Long userId = prepareUser.getId();
        final User savedPreparedUser = userRepository.findById(userId).get();

        assertThat(savedPreparedUser.getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(savedPreparedUser.getUsername()).isNull();
        assertThat(savedPreparedUser.getAvatar()).isNull();

        userService.create(savedPreparedUser, CreateRequestUtilTest.createUserCreateRequest(USER_NAME_1));
        final User finalUser = userRepository.findById(userId).get();

        assertThat(finalUser.getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(finalUser.getUsername()).isEqualTo(USER_NAME_1);
        assertThat(finalUser.getAvatar()).isNull();

        userService.update(finalUser, CreateRequestUtilTest.createUserUpdateRequest(USER_NAME_2));
        final User updatedUser = userRepository.findById(userId).get();

        assertThat(updatedUser.getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(updatedUser.getUsername()).isEqualTo(USER_NAME_2);
        assertThat(updatedUser.getAvatar()).isNull();
    }

    @Test
    void testUpdateUserToAlreadyExistingUsername_shouldBeUpdated() {
        //create first user
        final User prepareUser = userService.prepareUser(USER_PUBLIC_KEY_1);
        final Long userId = prepareUser.getId();
        final User savedPreparedUser = userRepository.findById(userId).get();
        userService.create(savedPreparedUser, CreateRequestUtilTest.createUserCreateRequest(USER_NAME_1));
        final User finalUser = userRepository.findById(userId).get();

        //create second user
        final User prepareUser2 = userService.prepareUser(USER_PUBLIC_KEY_2);
        final Long userId2 = prepareUser2.getId();
        final User savedPreparedUser2 = userRepository.findById(userId2).get();
        userService.create(savedPreparedUser2, CreateRequestUtilTest.createUserCreateRequest(USER_NAME_2));
        userRepository.findById(userId2).get();

        final User updatedUser = userService.update(finalUser, CreateRequestUtilTest.createUserUpdateRequest(USER_NAME_2));

        assertThat(updatedUser.getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(updatedUser.getUsername()).isEqualTo(USER_NAME_2);
        assertThat(updatedUser.getAvatar()).isNull();
    }

    @Test
    void testGetAndFoundMethods_shouldFoundUser() {
        final User prepareUser = userService.prepareUser(USER_PUBLIC_KEY_1);
        final Long userId = prepareUser.getId();
        final User savedPreparedUser = userRepository.findById(userId).get();
        userService.create(savedPreparedUser, CreateRequestUtilTest.createUserCreateRequest(USER_NAME_1));

        final User foundUser = this.userService.getById(userId);
        assertThat(foundUser.getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(foundUser.getUsername()).isEqualTo(USER_NAME_1);
        assertThat(foundUser.getAvatar()).isNull();

        final User userByPublicKey = this.userService.findByPublicKey(USER_PUBLIC_KEY_1).get();
        assertThat(userByPublicKey.getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(userByPublicKey.getUsername()).isEqualTo(USER_NAME_1);
        assertThat(userByPublicKey.getAvatar()).isNull();
    }

    @Test
    void testRemoveUser_shouldBeRemoved() {
        final User prepareUser = userService.prepareUser(USER_PUBLIC_KEY_1);
        final Long userId = prepareUser.getId();
        final User savedPreparedUser = userRepository.findById(userId).get();
        userService.create(savedPreparedUser, CreateRequestUtilTest.createUserCreateRequest(USER_NAME_1));
        final User finalUser = userRepository.findById(userId).get();
        assertThat(finalUser.getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(finalUser.getUsername()).isEqualTo(USER_NAME_1);
        assertThat(finalUser.getAvatar()).isNull();

        this.userService.remove(finalUser);

        final List<User> allUsers = userRepository.findAll();
        assertThat(allUsers).isEmpty();
    }

    @Test
    void testRemoveUserAvatar_shouldBeRemoved() {
        final User prepareUser = userService.prepareUser(USER_PUBLIC_KEY_1);
        final Long userId = prepareUser.getId();
        final User savedPreparedUser = userRepository.findById(userId).get();
        userService.create(savedPreparedUser, CreateRequestUtilTest.createUserCreateRequestWithAvatar(USER_NAME_1));
        final User finalUser = userRepository.findById(userId).get();
        assertThat(finalUser.getPublicKey()).isEqualTo(USER_PUBLIC_KEY_1);
        assertThat(finalUser.getUsername()).isEqualTo(USER_NAME_1);
        assertThat(finalUser.getAvatar()).isNotNull();

        this.userService.removeAvatar(finalUser);

        final List<User> allUsers = userRepository.findAll();
        assertThat(allUsers.get(0).getAvatar()).isNull();
    }
}

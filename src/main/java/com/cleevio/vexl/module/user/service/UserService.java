package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.dto.request.UserCreateRequest;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.PublicKeyNotFoundException;
import com.cleevio.vexl.module.user.exception.UserCreationException;
import com.cleevio.vexl.module.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(UserCreateRequest userCreateRequest) {
        if (!isUsernameAvailable(userCreateRequest.getUsername())) {
            throw new UserCreationException(
                    String.format(
                            "Username %s is not available. Username must be unique.",
                            userCreateRequest.getUsername()
                    )
            );
        }

        User user = User.builder()
                .avatar(userCreateRequest.getAvatar())
                .username(userCreateRequest.getUsername())
                .publicKey(userCreateRequest.getPublicKey())
                .build();

        return this.userRepository.save(user);
    }

    public boolean isUsernameAvailable(String username) {
        return this.userRepository.isUsernameAvailable(username);
    }

    public User update(long id, UserCreateRequest userCreateRequest) {
        User user = this.find(id);

        if (userCreateRequest.getUsername() != null) {
            user.setUsername(userCreateRequest.getUsername());
        }

        if (userCreateRequest.getAvatar() != null) {
            user.setAvatar(userCreateRequest.getAvatar());
        }

        return this.userRepository.save(user);
    }

    public void remove(long id) {
        this.userRepository.deleteById(id);
    }

    public String retrievePublicKeyByUserId(long id) {
        return this.userRepository.retrievePublicKeyByUserId(id)
                .orElseThrow(PublicKeyNotFoundException::new);
    }

    public User find(long id) {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

/**
 * THIS IS FROM BOOTSTRAP, MAYBE IT CAN BE USEFUL FOR NEXT TICKETS, SO I DIDNT REMOVE IT FOR NEW
 */
//
//    /**
//     * Update a user
//     *
//     * @param dto User update DTO
//     * @return User
//     */
//    public User update(long id, UserCreateRequest dto) throws UserNotFoundException, UserDuplicateException, FileWriteException, UserAvatarInvalidException {
//        User user = this.find(id);
//
//        if (this.userRepository.findByEmail(dto.getEmail())
//                .filter(u -> !u.equals(user))
//                .isPresent()) {
//            throw new UserDuplicateException();
//        }
//
//        user.setEmail(dto.getEmail());
//
//        if (dto.getAvatar() != null) {
//            user.setAvatar(this.imageService.create(dto.getAvatar(), Image.Category.AVATAR));
//        }
//
//        if (dto.getPassword() != null) {
//            user.addIdentity(new IdentityPassword(user, this.passwordEncoder.encode(dto.getPassword())));
//        }
//
//        return this.userRepository.save(user);
//    }
//
//    /**
//     * Find user by id
//     *
//     * @param id User ID
//     * @return User user
//     * @throws UserNotFoundException User does not exist
//     */
//    public User find(long id) throws UserNotFoundException {
//        return this.userRepository.findById(id)
//                .orElseThrow(UserNotFoundException::new);
//    }
//
//    /**
//     * Find user by email
//     *
//     * @param email User email
//     */
//    public Optional<User> findByEmail(String email) {
//        return this.userRepository.findByEmail(email);
//    }
//
//    /**
//     * Find user by access token
//     *
//     * @param token Access token
//     * @return user
//     */
//    public Optional<User> findByToken(String token, Token.Type type) {
//        return this.accessService.find(token, type).map(Access::getUser);
//    }
//
//    /**
//     * Find paged list of users
//     *
//     * @param request       Request
//     * @param page          Page number
//     * @param specification Search
//     * @return Users page
//     */
//    public UsersResponse findAll(HttpServletRequest request, int page, UserSpecification specification) {
//        return new UsersResponse(request, this.userRepository
//                .findAll(specification.build(), PageRequest.of(page, 10))
//                .map(UserResponse::new));
//    }
//
//    /**
//     * Check if subject has a permission
//     *
//     * @param user       User to check
//     * @param entity     Entity to check
//     * @param permission Permission to check
//     * @return has permission
//     */
//    public boolean hasPermission(AclSubject user, Class<? extends AclSecured> entity, String permission) {
//        return user.getRoles().stream()
//                .map(Role::getPrivileges)
//                .flatMap(Collection::stream)
//                .anyMatch(p -> allowEntity(p, entity.getName(), permission));
//    }
//
//    /**
//     * Check if subject has a permission
//     *
//     * @param user       User to check
//     * @param id         Entity identificator
//     * @param entity     Entity to check
//     * @param permission Permission to check
//     * @return has permission
//     */
//    public boolean hasPermission(AclSubject user, Serializable id, Class<? extends AclSecured> entity, String permission) {
//
//        if (user == null) {
//            return this.allowEntity(entity, id);
//        }
//
//        Collection<Role> roles = user.getRoles();
//        return roles.stream()
//                .map(Role::getPrivileges)
//                .flatMap(Collection::stream)
//                .filter(p -> allowEntity(p, entity.getName(), permission))
//                .anyMatch(p -> this.allowEntity(p, entity, id, user));
//    }
//
//    /**
//     * Check if subject has access to custom entity
//     *
//     * @param privilege  Privilege
//     * @param entity     Entity
//     * @param permission Permission
//     * @return Is allowed
//     */
//    private boolean allowEntity(Privilege privilege, String entity, String permission) {
//        return (privilege.getEntity() == null || privilege.getEntity().equals(entity)) &&
//                (privilege.getPrivilege() == null || privilege.getPrivilege().equals(permission));
//    }
//
//    /**
//     * Check if subject has access to custom entity
//     *
//     * @param privilege Privilege
//     * @param entity    Entity class
//     * @param id        Entity ID
//     * @param subject   Subject to check
//     * @return Is allowed
//     */
//    private boolean allowEntity(Privilege privilege, Class<?> entity, Serializable id, AclSubject subject) {
//        if (!privilege.isHasCheck()) {
//            return true;
//        }
//
//        AclSecured obj = ((AclSecured) this.aclRepository.findByID(id, entity));
//
//        if (obj == null) {
//            return false;
//        }
//
//        return obj.isAllowed(subject);
//    }
//
//    /**
//     * Check if anonymous subject has access to custom entity
//     *
//     * @param entity Entity class
//     * @param id     Entity ID
//     * @return Is allowed
//     */
//    private boolean allowEntity(Class<?> entity, Serializable id) {
//        return ((AclSecured) this.aclRepository.findByID(id, entity)).isAllowed(null);
//    }
//
//    /**
//     * Recover the lost password via email
//     *
//     * @param request Request containing e-mail
//     */
//    public void recoverPassword(PasswordRecoveryRequest request) {
//        Optional<User> user = this.userRepository.findByEmail(request.getEmail());
//        if (user.isEmpty()) return;
//
//        SecureRandom random = new SecureRandom();
//        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
//
//        byte[] randomBytes = new byte[this.passwordRecoverySize];
//        random.nextBytes(randomBytes);
//        String code = base64Encoder.encodeToString(randomBytes).substring(0, this.passwordRecoverySize);
//        Date expires = new Date(System.currentTimeMillis() + (1000 * passwordRecoveryExpiration));
//
//        user.get().addIdentity(new IdentityRecovery(user.get(), code, expires));
//        this.userRepository.save(user.get());
//
//        this.eventPublisher.publish(new PasswordRecoveryEvent(this, user.get(), code));
//    }
//
//    /**
//     * Change the user password via password reset token
//     *
//     * @param request Request containing the token and new pass
//     * @throws EntityNotFoundException if the token is not found
//     */
//    public void resetPassword(PasswordResetRequest request) throws EntityNotFoundException {
//        User user = this.identityRepository.findByCode(request.getCode())
//                .filter(i -> i.getExpires().after(new Date()))
//                .map(Identity::getUser)
//                .orElseThrow(EntityNotFoundException::new);
//
//        user.addIdentity(new IdentityPassword(user, this.passwordEncoder.encode(request.getPassword())));
//        user.removeIdentity(IdentityRecovery.class);
//
//        this.accessService.clearAccess(user);
//        this.userRepository.save(user);
//    }
//
//    /**
//     * Create user from facebook data. Throws UserDuplicateException if the user registered via another method
//     *
//     * @param token authorization token returned from facebook service
//     * @return AuthLoginData
//     * @throws FacebookLoginException        Authentication failed
//     * @throws FacebookMissingEmailException Returned data missing email
//     * @throws FileWriteException            Failed to write avatar
//     * @throws UserDuplicateException        User already registered via another method
//     */
//    public AuthLoginData loginFacebook(String token) throws FacebookLoginException, FacebookMissingEmailException, FileWriteException, UserDuplicateException {
//        FacebookUserData userData = this.facebookService.getUser(token);
//        if (userData.getEmail() == null) throw new FacebookMissingEmailException();
//
//        var optionalUser = this.identityRepository.findByFacebookId(userData.getId()).map(Identity::getUser);
//        var user = findUserSocial(userData, optionalUser);
//        if (user.getId() != null) return new AuthLoginData(user, false);
//
//        user.addIdentity(new IdentityFacebook(user, userData.getId()));
//        return new AuthLoginData(this.userRepository.save(user), true);
//    }
//
//    /**
//     * Link the facebook account to the user
//     *
//     * @param id    user id to link to
//     * @param token authorization token returned from facebook service
//     * @return user
//     * @throws FacebookLoginException Authentication failed
//     * @throws SocialAlreadyLinked    Account has already linked facebook
//     * @throws UserNotFoundException  user doesn't exist
//     */
//    public User linkFacebook(long id, String token) throws UserNotFoundException, FacebookLoginException, SocialAlreadyLinked {
//        FacebookUserData userData = this.facebookService.getUser(token);
//
//        User user = this.find(id);
//        linkSocial(user, new IdentityFacebook(user, userData.getId()));
//        return this.userRepository.save(user);
//    }
//
//    /**
//     * Unlink the facebook account from the user
//     *
//     * @param id    user id to link to
//     * @param token authorization token returned from facebook service
//     * @return user
//     * @throws FacebookLoginException   Authentication failed
//     * @throws SocialNotLinkedException Account has not linked facebook
//     * @throws UserNotFoundException    user doesn't exist
//     */
//    public User unlinkFacebook(long id, String token) throws FacebookLoginException, UserNotFoundException, UserLastAuthenticationException, SocialNotLinkedException {
//        if (!this.facebookService.testConnection(token)) {
//            throw new FacebookLoginException();
//        }
//
//        User user = this.find(id);
//        unlinkSocial(user, IdentityFacebook.class);
//        return this.userRepository.save(user);
//    }
//
//
//    /**
//     * Create user from google data. Throws UserDuplicateException if the user registered via another method
//     *
//     * @param token authorization token returned from facebook service
//     * @return AuthLoginData
//     * @throws GoogleLoginException   Authentication failed
//     * @throws UserDuplicateException User already registered via another method
//     */
//    public AuthLoginData loginGoogle(String token) throws GoogleLoginException, UserDuplicateException, FileWriteException {
//        GoogleUserData userData = this.googleService.getUser(token);
//
//        var optionalUser = this.identityRepository.findByGoogleId(userData.getId()).map(Identity::getUser);
//        var user = findUserSocial(userData, optionalUser);
//        if (user.getId() != null) return new AuthLoginData(user, false);
//
//        user.addIdentity(new IdentityGoogle(user, userData.getId()));
//        return new AuthLoginData(this.userRepository.save(user), true);
//    }
//
//    /**
//     * Link the google account to the user
//     *
//     * @param id    user id to link to
//     * @param token authorization token returned from facebook service
//     * @return user
//     * @throws GoogleLoginException  Authentication failed
//     * @throws SocialAlreadyLinked   Account has already linked facebook
//     * @throws UserNotFoundException user doesn't exist
//     */
//    public User linkGoogle(long id, String token) throws GoogleLoginException, UserNotFoundException, SocialAlreadyLinked {
//        GoogleUserData userData = this.googleService.getUser(token);
//
//        User user = this.find(id);
//        linkSocial(user, new IdentityGoogle(user, userData.getId()));
//        this.userRepository.save(user);
//        return user;
//    }
//
//    /**
//     * Unlink the google account from the user
//     *
//     * @param id    user id to link to
//     * @param token authorization token returned from facebook service
//     * @return user
//     * @throws GoogleLoginException     Authentication failed
//     * @throws SocialNotLinkedException Account has already linked google
//     * @throws UserNotFoundException    user doesn't exist
//     */
//    public User unlinkGoogle(long id, String token) throws GoogleLoginException, UserNotFoundException, SocialNotLinkedException, UserLastAuthenticationException {
//        if (!this.googleService.testConnection(token)) {
//            throw new GoogleLoginException();
//        }
//
//        User user = this.find(id);
//        unlinkSocial(user, IdentityGoogle.class);
//        this.userRepository.save(user);
//        return user;
//    }
//
//    /**
//     * Create user from apple data. Throws UserDuplicateException if the user registered via another method
//     *
//     * @param request apple request containing the token, first and last name
//     * @return AuthLoginData
//     * @throws AppleLoginException    Authentication failed
//     * @throws UserDuplicateException User already registered via another method
//     */
//    public AuthLoginData loginApple(AppleDataRequest request) throws AppleLoginException, UserDuplicateException, FileWriteException {
//        AppleUserData userData = this.appleService.getUser(request.getToken());
//
//        var optionalUser = this.identityRepository.findByAppleEmail(userData.getEmail()).map(Identity::getUser);
//        var user = findUserSocial(userData, optionalUser);
//        if (user.getId() != null) return new AuthLoginData(user, false);
//
//        user.addIdentity(new IdentityApple(user, userData.getEmail()));
//        return new AuthLoginData(this.userRepository.save(user), true);
//    }
//
//    /**
//     * Link the apple account to the user
//     *
//     * @param id    user id to link to
//     * @param token authorization token returned from facebook service
//     * @return user
//     * @throws AppleLoginException   Authentication failed
//     * @throws SocialAlreadyLinked   Account has already linked facebook
//     * @throws UserNotFoundException user doesn't exist
//     */
//    public User linkApple(long id, String token) throws AppleLoginException, UserNotFoundException, SocialAlreadyLinked {
//        AppleUserData userData = this.appleService.getUser(token);
//
//        User user = this.find(id);
//        linkSocial(user, new IdentityApple(user, userData.getEmail()));
//        this.userRepository.save(user);
//        return user;
//    }
//
//    /**
//     * Unlink the apple account from the user
//     *
//     * @param id    user id to link to
//     * @param token authorization token returned from facebook service
//     * @return user
//     * @throws AppleLoginException      Authentication failed
//     * @throws SocialNotLinkedException Account has already linked google
//     * @throws UserNotFoundException    user doesn't exist
//     */
//    public User unlinkApple(long id, String token) throws UserNotFoundException, SocialNotLinkedException, UserLastAuthenticationException, AppleLoginException {
//        this.appleService.getUser(token);
//
//        User user = this.find(id);
//        unlinkSocial(user, IdentityApple.class);
//        this.userRepository.save(user);
//        return user;
//    }
//
//    /**
//     * Unlink social
//     *
//     * @throws SocialNotLinkedException        social is not linked yet
//     * @throws UserLastAuthenticationException last authentication method
//     */
//    private void unlinkSocial(User user, Class<? extends Identity> cls) throws SocialNotLinkedException, UserLastAuthenticationException {
//        if (user.getIdentity(cls) == null) {
//            throw new SocialNotLinkedException();
//        }
//        if (user.authenticationIdentities() <= 1) {
//            throw new UserLastAuthenticationException();
//        }
//        user.removeIdentity(cls);
//    }
//
//    /**
//     * Link social
//     *
//     * @throws SocialAlreadyLinked the social is already linked
//     */
//    private void linkSocial(User user, Identity identity) throws SocialAlreadyLinked {
//        if (user.getIdentity(identity.getClass()) != null) {
//            throw new SocialAlreadyLinked();
//        }
//        user.addIdentity(identity);
//    }
//
//    private User findUserSocial(SocialUserData userData, Optional<User> optionalUser) throws UserDuplicateException, FileWriteException {
//        if (optionalUser.isPresent()) {
//            return optionalUser.get();
//        }
//
//        optionalUser = this.userRepository.findByEmail(userData.getEmail());
//        if (optionalUser.isPresent()) {
//            throw new UserDuplicateException();
//        }
//
//        // create new user
//        User user = new User();
//        user.setEmail(userData.getEmail());
//
//        if (userData.getProfileImage() != null) {
//            Image image = new Image(Image.Category.AVATAR, UUID.randomUUID().toString());
//            this.imageService.save(image, userData.getProfileImage());
//            user.setAvatar(image);
//        }
//
//        roleRepository.findByName(userRole)
//                .ifPresent(role -> user.setRoles(Collections.singleton(role)));
//
//        return user;
//    }
}

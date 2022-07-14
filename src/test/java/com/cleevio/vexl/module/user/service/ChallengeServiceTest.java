package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.entity.UserVerification;
import com.cleevio.vexl.module.user.exception.VerificationNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChallengeServiceTest {

    private static final User USER;
    private static final UserVerification USER_VERIFICATION;
    private static final String PUBLIC_KEY = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUU0d0VBWUhLb1pJemowQ0FRWUZLNEVFQUNFRE9nQUUzSzhsVjJ2QVQ2S2w5VFBjbTNQdHcxV0hFL2RTbTJScApEMElQaUU0ZVpwQTVPQjJRMU9YK2FSaEoyZ0hFQjFydkdHc3A2bjZNUHdvPQotLS0tLUVORCBQVUJMSUMgS0VZLS0tLS0K";
    private static final String PRIVATE_KEY = "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1IZ0NBUUF3RUFZSEtvWkl6ajBDQVFZRks0RUVBQ0VFWVRCZkFnRUJCQnlQZG1IQnF4OUxZOFVhc0FQdE1xOGoKbEdmRGRDQkM1UHNhcldwaW9Ud0RPZ0FFM0s4bFYydkFUNktsOVRQY20zUHR3MVdIRS9kU20yUnBEMElQaUU0ZQpacEE1T0IyUTFPWCthUmhKMmdIRUIxcnZHR3NwNm42TVB3bz0KLS0tLS1FTkQgUFJJVkFURSBLRVktLS0tLQo=";
    private static final String PUBLIC_KEY_2 = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUU0d0VBWUhLb1pJemowQ0FRWUZLNEVFQUNFRE9nQUVyM3BienM3UVo1VVhPRXFlMlQ3eU4xQmxDVXZucWhVMQpSSVhzelY2SWlFYW1Mc2x5WWt2d3ZNVnFZRlhZSWRmY2J1VHlwWmtNMzJrPQotLS0tLUVORCBQVUJMSUMgS0VZLS0tLS0K";
    private final ChallengeService challengeService = new ChallengeService();

    static {
        USER_VERIFICATION = new UserVerification();

        USER = new User();
    }

    @Test
    void testChallengeCreationAndValidation_challengeShouldBeValid() throws Exception {
        final String challenge = this.challengeService.generateChallenge();
        final String signedChallenge = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY, PRIVATE_KEY, challenge, challenge.length());

        USER_VERIFICATION.setChallenge(challenge);
        USER.setPublicKey(PUBLIC_KEY);
        USER.setUserVerification(USER_VERIFICATION);

        final boolean signedChallengeValid = challengeService.isSignedChallengeValid(USER, signedChallenge);
        assertThat(signedChallengeValid).isTrue();
    }

    @Test
    void testChallengeCreationAndValidation_challengeShouldBeInvalid() throws Exception {
        final String challenge = this.challengeService.generateChallenge();
        final String signedChallenge = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY, PRIVATE_KEY, challenge, challenge.length());

        USER_VERIFICATION.setChallenge(challenge);
        USER.setPublicKey(PUBLIC_KEY_2);
        USER.setUserVerification(USER_VERIFICATION);

        final boolean signedChallengeValid = challengeService.isSignedChallengeValid(USER, signedChallenge);
        assertThat(signedChallengeValid).isFalse();
    }

    @Test
    void testChallengeCreationAndValidation_missingChallenge_shouldReturnException() throws Exception {
        final String challenge = this.challengeService.generateChallenge();
        final String signedChallenge = CLibrary.CRYPTO_LIB.ecdsa_sign(PUBLIC_KEY, PRIVATE_KEY, challenge, challenge.length());

        USER_VERIFICATION.setChallenge(null);
        USER.setPublicKey(PUBLIC_KEY);
        USER.setUserVerification(USER_VERIFICATION);

        assertThrows(
                VerificationNotFoundException.class,
                () -> challengeService.isSignedChallengeValid(USER, signedChallenge)
        );
    }

}

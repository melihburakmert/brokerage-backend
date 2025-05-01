package mbm.brokerage_backend.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import mbm.brokerage_backend.auth.JwtService;
import mbm.brokerage_backend.auth.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceUT {

    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION = create(Long.class);
    private static final String USERNAME = create(String.class);
    private static final String PASSWORD = create(String.class);

    @Mock private JwtProperties properties;

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        when(properties.secretKey()).thenReturn(SECRET_KEY);
        when(properties.expiration()).thenReturn(EXPIRATION);

        jwtService = new JwtServiceImp(properties);

        userDetails = User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void test_generateToken() {
        // WHEN
        final String token = jwtService.generateToken(userDetails);

        // THEN
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void test_extractUsername() {
        // GIVEN
        final String token = generateValidToken(USERNAME);

        // WHEN
        final String extractedUsername = jwtService.extractUsername(token);

        // THEN
        assertThat(extractedUsername).isEqualTo(USERNAME);
    }

    @Test
    void test_isTokenValid_whenValid() {
        // GIVEN
        final String token = generateValidToken(USERNAME);

        // WHEN
        final boolean isValid = jwtService.isTokenValid(token, userDetails);

        // THEN
        assertThat(isValid).isTrue();
    }

    @Test
    void test_isTokenValid_whenUsernameDoesNotMatch() {
        // GIVEN
        final String differentUsername = create(String.class);
        final String token = generateValidToken(differentUsername);

        // WHEN
        final boolean isValid = jwtService.isTokenValid(token, userDetails);

        // THEN
        assertThat(isValid).isFalse();
    }

    private String generateValidToken(final String username) {
        final byte[] keyBytes = Decoders.BASE64.decode(properties.secretKey());
        final Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + properties.expiration()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
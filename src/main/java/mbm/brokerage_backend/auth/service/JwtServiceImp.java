package mbm.brokerage_backend.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import mbm.brokerage_backend.auth.JwtService;
import mbm.brokerage_backend.auth.config.JwtProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImp implements JwtService {

    private final JwtProperties jwtProperties;

    public JwtServiceImp(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateToken(final UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public boolean isTokenValid(final String token, final UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private  <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String generateToken(final Map<String, Object> extraClaims, final UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.expiration()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    private Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(final String token) {
        final Key signInKey = getSignInKey();
        return Jwts
                .parserBuilder()
                .setSigningKey(signInKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secretKey().trim());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
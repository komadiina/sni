package org.unibl.etf.sni.dev;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class SigningKeyGenerator {
    public static void main(String[] args) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // or HS512, RS256, etc.
        String encodedKey = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("Generated JWT Signing Key: " + encodedKey);
    }
}

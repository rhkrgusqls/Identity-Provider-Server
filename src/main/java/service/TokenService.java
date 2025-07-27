package service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.util.Date;

@Service
public class TokenService {

    // 설정값 하드코딩 (프로퍼티 대신)
    private final long refreshTokenExpiration = 604800000; // 7일 (밀리초)
    private final String privateKeyFile = "refresh_private.pem";
    private final String publicKeyFile = "refresh_public.pem";

    private KeyPair refreshKeyPair;

    public TokenService() {
        loadOrCreateKeyPair();
    }

    private void loadOrCreateKeyPair() {
        try {
            File pubFile = new File(publicKeyFile);
            File privFile = new File(privateKeyFile);

            boolean needGenerate = false;

            if (!pubFile.exists() || !privFile.exists()) {
                needGenerate = true;
            } else {
                // 파일 존재하지만 내용이 비어있거나 null 체크
                if (pubFile.length() == 0 || privFile.length() == 0) {
                    needGenerate = true;
                } else {
                    try {
                        this.refreshKeyPair = loadKeyPairFromFiles();
                        // 만약 키가 null이면 재생성
                        if (this.refreshKeyPair == null
                                || this.refreshKeyPair.getPublic() == null
                                || this.refreshKeyPair.getPrivate() == null) {
                            needGenerate = true;
                        }
                    } catch (Exception e) {
                        // 로딩 실패시 새로 생성 플래그
                        needGenerate = true;
                    }
                }
            }

            if (needGenerate) {
                generateAndSaveKeyPair();
            }
        } catch (Exception e) {
            throw new RuntimeException("키 초기화 실패", e);
        }
    }

    private void generateAndSaveKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        try (JcaPEMWriter pubWriter = new JcaPEMWriter(new FileWriter(publicKeyFile));
             JcaPEMWriter privWriter = new JcaPEMWriter(new FileWriter(privateKeyFile))) {
            pubWriter.writeObject(keyPair.getPublic());
            privWriter.writeObject(keyPair.getPrivate());
        }

        this.refreshKeyPair = keyPair;
        System.out.println("[TokenService] 새 키 쌍 생성 및 저장 완료");
    }

    private KeyPair loadKeyPairFromFiles() throws Exception {
        try (PEMParser pubParser = new PEMParser(new FileReader(publicKeyFile));
             PEMParser privParser = new PEMParser(new FileReader(privateKeyFile))) {

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            Object pubObj = pubParser.readObject();
            PublicKey publicKey;
            if (pubObj instanceof SubjectPublicKeyInfo) {
                publicKey = converter.getPublicKey((SubjectPublicKeyInfo) pubObj);
            } else if (pubObj instanceof PEMKeyPair) {
                publicKey = converter.getPublicKey(((PEMKeyPair) pubObj).getPublicKeyInfo());
            } else {
                throw new IllegalArgumentException("Unexpected public key format: " + pubObj.getClass());
            }

            Object privObj = privParser.readObject();
            PrivateKey privateKey;
            if (privObj instanceof PrivateKeyInfo) {
                privateKey = converter.getPrivateKey((PrivateKeyInfo) privObj);
            } else if (privObj instanceof PEMKeyPair) {
                privateKey = converter.getPrivateKey(((PEMKeyPair) privObj).getPrivateKeyInfo());
            } else {
                throw new IllegalArgumentException("Unexpected private key format: " + privObj.getClass());
            }

            return new KeyPair(publicKey, privateKey);
        }
    }

    @Scheduled(fixedRate = 604800000) // 7일마다 키 재생성
    public void regenerateKeysPeriodically() {
        try {
            generateAndSaveKeyPair();
            System.out.println("[TokenService] 키 재생성 스케줄러 실행");
        } catch (Exception e) {
            System.err.println("[TokenService] 키 재생성 실패: " + e.getMessage());
        }
    }

    public String generateRefreshToken(String userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(userId)                 // sub: username
                .claim("username", username)                  // 커스텀 claim 추가
                .setIssuedAt(now)                     // iat
                .setExpiration(expiryDate)            // exp
                .signWith(refreshKeyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }


    public String getRefreshPublicKeyPEM() {
        try (StringWriter sw = new StringWriter();
             JcaPEMWriter writer = new JcaPEMWriter(sw)) {
            writer.writeObject(refreshKeyPair.getPublic());
            writer.flush();
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException("공개키 PEM 반환 실패", e);
        }
    }

    public String extractUsernameFromRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(refreshKeyPair.getPublic())
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException e) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰", e);
        }
    }
}

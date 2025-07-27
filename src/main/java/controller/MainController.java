package controller;

import model.UserDAO;
import service.AuthService;
import service.DBAuthService;
import service.TokenService;

import java.util.Map;

public class MainController {

    private final TokenService tokenService = new TokenService(); // 싱글톤 제거 후 일반 인스턴스 생성
    private final AuthService authService = new DBAuthService(); // AuthService 인스턴스

    // 로그인 시도 후 성공 시 리프레시 토큰 발급
    public String login(String id, String password) {
        if (id == null || password == null || id.isEmpty() || password.isEmpty()) {
            return "login%error%Missing id or password\n";
        }

        // AuthService를 통해 로그인
        Map<String, String> params = Map.of("id", id, "password", password);
        boolean isAuthenticated = authService.login(params);
        
        if (isAuthenticated) {
            String refreshToken = tokenService.generateRefreshToken(id);
            System.out.println("[Netty] Send refresh token: " + refreshToken);
            return "login%&refreshToken$" + refreshToken + "\n";
        } else {
            return "login%error%Invalid credentials\n";
        }
    }

    // 리프레시 토큰 검증용 공개키 반환
    public String getRefreshTokenPublicKey() {
        String publicKeyPem = tokenService.getRefreshPublicKeyPEM();
        return "refPublicKey%&refKey$" + publicKeyPem.replace("\r\n", "\\n").replace("\n", "\\n");
    }
    
    public static String signup(ParsingController.DataStruct data) {
        Map<String, String> paramMap = dataStructToMap(data);
        AuthService authService = new DBAuthService(); // 실제 구현체 사용
        boolean result = authService.signup(paramMap);
        return result ? "[회원가입 완료]" : "[회원가입 실패]";
    }

    private static Map<String, String> dataStructToMap(ParsingController.DataStruct data) {
        Map<String, String> paramMap = new java.util.HashMap<>();
        
        if (data.id != null && data.id.length > 0) {
            paramMap.put("id", data.id[0]);
        }
        if (data.password != null && data.password.length > 0) {
            paramMap.put("password", data.password[0]);
        }
        if (data.name != null && data.name.length > 0) {
            paramMap.put("name", data.name[0]);
        }
        
        return paramMap;
    }

}

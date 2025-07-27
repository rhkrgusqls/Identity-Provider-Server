package controller;

import model.UserDAO;
import service.AuthService;
import service.TestAuthService;
import service.TokenService;

import java.util.Map;

public class MainController {
    private static Map<String, String> dataStructToMap(ParsingController.DataStruct data) {
        Map<String, String> map = new java.util.HashMap<>();
        if (data.id != null && data.id.length > 0) map.put("id", data.id[0]);
        if (data.password != null && data.password.length > 0) map.put("password", data.password[0]);
        if (data.userAddress != null && data.userAddress.length > 0) map.put("userAddress", data.userAddress[0]); // userAddress 추가
        // 필요시 추가
        return map;
    }

    private final TokenService tokenService = new TokenService(); // 싱글톤 제거 후 일반 인스턴스 생성

    // 로그인 시도 후 성공 시 리프레시 토큰 발급
    public String login(String id, String password) {
        UserDAO userDAO = new UserDAO();
        if (id == null || password == null || id.isEmpty() || password.isEmpty()) {
            return "login%error%Missing id or password\n";
        }

        boolean isAuthenticated = userDAO.login(id, password);
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
        AuthService authService = new TestAuthService(); // 실제 구현체 사용

        String userId = paramMap.get("id");
        if (userId == null || userId.isEmpty()) {
            return "SIGNUP%&result$ERROR&message$Missing ID%";
        }

        if (authService.existsUser(userId)) {
            return "SIGNUP%&result$ERROR&message$ID Already Exists%";
        }

        boolean result = authService.signup(paramMap);
        if (result) {
            return "SIGNUP%&result$SUCCESS%";
        } else {
            return "SIGNUP%&result$ERROR&message$Signup Failed%";
        }
    }
}

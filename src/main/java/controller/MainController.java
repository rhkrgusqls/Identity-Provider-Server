package controller;

import model.UserDAO;
import service.TokenService;

public class MainController {

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
}

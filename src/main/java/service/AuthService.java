package service;

public interface AuthService {
    boolean authenticate(String input);

    default boolean authenticate(java.util.Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> sb.append(k).append("%").append(v).append("%"));
        return authenticate(sb.toString());
    }

    // 회원가입용 메서드 (임시)
    default boolean signup(java.util.Map<String, String> params) {
        model.identityServerConnector connector = new model.identityServerConnector();
        return connector.requestSignupToIdentityServer(params);
    }

    boolean existsUser(String userId);
} 
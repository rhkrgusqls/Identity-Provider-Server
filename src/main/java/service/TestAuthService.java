package service;

public class TestAuthService implements AuthService {
    @Override
    public boolean authenticate(String input) {
        return true;
    }

    @Override
    public boolean authenticate(java.util.Map<String, String> params) {
        return true;
    }

    @Override
    public boolean signup(java.util.Map<String, String> params) {
        model.identityServerConnector connector = new model.identityServerConnector();
        return connector.requestSignupToIdentityServer(params);
    }
} 
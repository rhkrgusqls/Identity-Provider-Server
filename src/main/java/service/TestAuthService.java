package service;

import model.UserDAO;
import model.UserDTO;

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
        UserDAO userDAO = new UserDAO();
        UserDTO userDTO = new UserDTO(
                params.get("id"),
                params.get("password"),
                params.get("userAddress")
        );
        return userDAO.addUser(userDTO);
    }

    @Override
    public boolean existsUser(String userId) {
        UserDAO userDAO = new UserDAO();
        return userDAO.exists(userId);
    }
} 
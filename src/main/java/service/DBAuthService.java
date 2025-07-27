package service;

import model.UserDAO;
import model.UserDTO;
import java.util.Map;

public class DBAuthService implements AuthService {
    
    private UserDAO userDAO;
    
    public DBAuthService() {
        this.userDAO = new UserDAO();
    }
    
    @Override
    public boolean authenticate(String input) {
        // 단순 문자열 인증은 지원하지 않음
        return false;
    }
    
    @Override
    public boolean authenticate(Map<String, String> params) {
        String userId = params.get("id");
        String password = params.get("password");
        
        if (userId == null || password == null) {
            return false;
        }
        
        // 실제 DB에서 사용자 인증 수행
        return userDAO.login(userId, password);
    }
    
    @Override
    public boolean signup(Map<String, String> params) {
        String userId = params.get("id");
        String password = params.get("password");
        String address = params.get("address");
        
        if (userId == null || password == null) {
            return false;
        }
        
        // 회원가입 DTO 생성
        UserDTO userDto = new UserDTO();
        userDto.setUserID(userId);
        userDto.setUserPassword(password);
        userDto.setUserAddress(address != null ? address : "");
        
        // 실제 DB에 사용자 정보 저장
        return userDAO.addUser(userDto);
    }
} 
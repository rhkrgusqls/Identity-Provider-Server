package controller;
import util.ParsingModule;
import controller.MainController;

import java.util.Map;

public class ParsingController {

    public static class DataStruct {
        public String[] id;
        public String[] password;
        public String[] name;
    }

    public static String controllerHandle(String input) {
        String opcode = ParsingModule.extractOpcode(input);
        System.out.println("[DEBUG] 추출된 opcode: '" + opcode + "'");
        DataStruct data = ParsingModule.extractData(input);
        String senderId = ParsingModule.extractSenderUserId(input);

        controller.MainController controller = new controller.MainController();

        switch (opcode) {
            case "LOGIN":
                // null 체크 추가
                if (data.id == null || data.id.length == 0 || data.password == null || data.password.length == 0) {
                    return "login%error%Missing id or password\n";
                }
                return controller.login(data.id[0], data.password[0]);
            case "GET_REFRESH_PUBLIC_KEY":
                return controller.getRefreshTokenPublicKey();
            default:
                return "error%UnknownOpcode";
        }
    }
}

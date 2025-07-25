package controller;
import util.ParsingModule;
import controller.MainController;

import java.util.Map;

public class ParsingController {

    public static class DataStruct {
        public String[] id;
        public String[] password;
        public String[] userAddress; // userAddress 필드 추가
        public String[] name;
        public String[] profileDir;
        public String[] phoneNum;
        public String[] chatRoomNum;
        public String[] chatData;
    }

    public static String controllerHandle(String input) {
        String opcode = ParsingModule.extractOpcode(input);
        System.out.println("[DEBUG] 추출된 opcode: '" + opcode + "'");
        DataStruct data = ParsingModule.extractData(input);
        String senderId = ParsingModule.extractSenderUserId(input);

        controller.MainController controller = new controller.MainController();

        switch (opcode) {
            case "LOGIN":
                return controller.login(data);
            case "SIGNUP":
                return controller.signup(data); // SIGNUP 분기 추가
            default:
                return "error%UnknownOpcode";
        }
    }
}

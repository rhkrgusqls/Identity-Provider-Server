package controller;
import util.ParsingModule;
import controller.MainController;
import model.ProductDAO;
import model.Product;
import java.util.List;

import java.util.Map;

public class ParsingController {

    public static class DataStruct {
        public String[] id;
        public String[] password;
        public String[] name;
        public String[] userAddress;
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
            case "GET_PRODUCT_LIST":
                try {
                    ProductDAO productDAO = new ProductDAO();
                    List<Product> products = productDAO.getAllProducts();
                    
                    if (products.isEmpty()) {
                        return "productList%error%No products found\n";
                    }
                    
                    StringBuilder response = new StringBuilder("productList%&products$");
                    for (int i = 0; i < products.size(); i++) {
                        Product product = products.get(i);
                        if (i > 0) response.append("|");
                        response.append(product.getProductID()).append(",")
                                .append(product.getProductName()).append(",")
                                .append(product.getProductStock()).append(",")
                                .append(product.getProductPrice());
                    }
                    response.append("\n");
                    
                    System.out.println("[Netty] Send product list: " + products.size() + " products");
                    return response.toString();
                    
                } catch (Exception e) {
                    System.err.println("[Netty] Error getting product list: " + e.getMessage());
                    return "productList%error%Database error\n";
                }
            case "SIGNUP":
                return controller.signup(data);
            default:
                return "error%UnknownOpcode";
        }
    }
}

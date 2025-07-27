package main;

import controller.*;
import model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import console.HTTPSConnect;

import static controller.MainController.getProductAccess;
import static controller.MainController.purchaseProduct;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ProductDAO DAO = new ProductDAO();
        SpringApplication.run(Main.class, args);
//        ParsingController.controllerHandle("productLoad%&productId$1234%Tokken");
        System.out.println(ParsingController.controllerHandle("LOGIN%&productId$1234%Tokken"));
        System.out.println(ParsingController.controllerHandle(getProductAccess(1)));
        System.out.println(ParsingController.controllerHandle(purchaseProduct(2)));

        /*
        try {
            HTTPSConnect server = new HTTPSConnect();
            server.connectClient(); // Netty + SSL 서버 실행
        } catch (Exception e) {
            System.err.println("[Fatal][Server] 서버 시작 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
        */
    }
}

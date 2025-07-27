package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class ProductDAO {
    private DBModule dbModule = new DBModule();

    // 상품 추가 (DTO를 받아 DB에 저장)
    public void addProduct(Product product) {
        String sql = "INSERT INTO product (productName, productStock,productPrice) VALUES (?, ?, ?)";
        try (Connection conn = dbModule.getConnection("productdb"); // "productdb" 스키마 사용
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, product.getProductStock());
            pstmt.setInt(3, product.getProductPrice());
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 모든 상품 조회 (DB 데이터를 product 객체 리스트로 반환)
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try (Connection conn = dbModule.getConnection("productdb");
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setProductID(rs.getInt("productID"));
                p.setProductName(rs.getString("productName"));
                p.setProductStock(rs.getInt("productStock"));
                p.setProductPrice(rs.getInt("productPrice"));
                productList.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return productList;
    }

    public Product getProductById(int productId) {
        String sql = "SELECT * FROM product WHERE productID = ?";
        try (Connection conn = dbModule.getConnection("productdb");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId); // 파라미터로 받은 ID를 쿼리에 설정

            try (ResultSet rs = pstmt.executeQuery()) {
                // 결과가 있다면 Product 객체를 만들어 정보 저장
                if (rs.next()) {
                    Product product = new Product();
                    product.setProductID(rs.getInt("productID"));
                    product.setProductName(rs.getString("productName"));
                    product.setProductStock(rs.getInt("productStock"));
                    product.setProductPrice(rs.getInt("productPrice"));
                    return product; // 정보가 담긴 객체 반환
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 상품을 찾지 못했거나 오류가 발생한 경우 null 반환
        return null;
    }

    public boolean decreaseProduct(int productId) {
        String sql = "UPDATE product SET productStock = productStock - 1 WHERE productID = ? AND productStock > 0;";
        try (Connection conn = dbModule.getConnection("productdb");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);

            int affectedRows = pstmt.executeUpdate();

            // 5. 결과 확인: 1개의 행이 업데이트되었다면 성공
            if (affectedRows > 0) {
                System.out.println("성공: 상품(" + productId + ") 재고를 1 차감했습니다.");
                return true; // 성공 반환
            } else {
                System.out.println("실패: 상품(" + productId + ") 재고를 차감하지 못했습니다. (재고가 없거나 상품 ID가 잘못됨)");
                return false; // 실패 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 상품을 찾지 못했거나 오류가 발생한 경우 null 반환
        return false;
    }

}

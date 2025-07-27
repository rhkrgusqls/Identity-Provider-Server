package model;
//final
public class ProductDTO {




	private int productId;
	private String productName;
	private int productStock;
	private int productPrice;

    public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getProductStock() {
		return productStock;
	}

	public void setProductStock(int productStock) {
		this.productStock = productStock;
	}

	public int getProductId() { return productId; }

	public void setProductId(int productId) { this.productId = productId; }
	
	public int getProductPrice() { return productPrice; }

	public void setProductPrice(int productPrice) { this.productPrice = productPrice; }

	public ProductDTO() {}

	public ProductDTO(int productId, String productName, int productStock, int productPrice) {
        this.productId = productId;
		this.productName = productName;
        this.productStock = productStock;
        this.productPrice = productPrice;
    }

}
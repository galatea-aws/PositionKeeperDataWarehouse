package PositionKeeperDataWarehouse.Entity;

public class Product {
	private int productKey;
	private String symbol;
	private String description;
	private boolean isOption;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isOption() {
		return isOption;
	}
	public void setOption(boolean isOption) {
		this.isOption = isOption;
	}
	public int getProductKey() {
		return productKey;
	}
	public void setProductKey(int productKey) {
		this.productKey = productKey;
	}
}

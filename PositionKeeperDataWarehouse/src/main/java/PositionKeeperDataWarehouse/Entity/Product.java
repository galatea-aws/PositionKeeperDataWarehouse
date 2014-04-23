package PositionKeeperDataWarehouse.Entity;

public class Product {
	private int productKey;
	private String symbol;
	private boolean isOption;
	private String fullName;
	private String sector;
	private String industry;
	private String productType;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
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
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}

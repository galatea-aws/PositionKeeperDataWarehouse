package PositionKeeperDataWarehouse.Entity;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PositionDetail {
	private int positionDetailKey;
	private int productKey;
	private int accountKey;
	private int dataLoadLogKey;
	private BigInteger quantity;
	private String direction;
	private double purchasePrice;
	private double currentPrice;
	private BigDecimal totalValue;
	private BigDecimal gainLoss;
	private String productSymbol;
	public int getPositionDetailKey() {
		return positionDetailKey;
	}
	public void setPositionDetailKey(int positionDetailKey) {
		this.positionDetailKey = positionDetailKey;
	}
	public int getProductKey() {
		return productKey;
	}
	public void setProductKey(int productKey) {
		this.productKey = productKey;
	}
	public int getAccountKey() {
		return accountKey;
	}
	public void setAccountKey(int accountKey) {
		this.accountKey = accountKey;
	}
	public int getDataLoadLogKey() {
		return dataLoadLogKey;
	}
	public void setDataLoadLogKey(int dataLoadLogKey) {
		this.dataLoadLogKey = dataLoadLogKey;
	}
	public BigInteger getQuantity() {
		return quantity;
	}
	public void setQuantity(BigInteger quantity) {
		this.quantity = quantity;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public BigDecimal getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}
	public BigDecimal getGainLoss() {
		return gainLoss;
	}
	public void setGainLoss(BigDecimal gainLoss) {
		this.gainLoss = gainLoss;
	}
	public String getProductSymbol() {
		return productSymbol;
	}
	public void setProductSymbol(String productSymbol) {
		this.productSymbol = productSymbol;
	}
	public double getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public double getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}
}

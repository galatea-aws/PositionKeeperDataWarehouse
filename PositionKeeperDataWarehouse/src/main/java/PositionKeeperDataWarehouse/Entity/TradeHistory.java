package PositionKeeperDataWarehouse.Entity;

import java.math.BigDecimal;
import java.util.Date;

public class TradeHistory {
	private int tradeHistoryKey;
	private int accountKey;
	private int dataLoadLogKey;
	private int productKey;
	private Date tradeDate;
	private String orderType;
	private String tradeType;
	private Long quantity;
	private Double price;
	private BigDecimal commission;
	private BigDecimal totalCashValue;
	private String productSymbol;
	public int getTradeHistoryKey() {
		return tradeHistoryKey;
	}
	public void setTradeHistoryKey(int tradeHistoryKey) {
		this.tradeHistoryKey = tradeHistoryKey;
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
	public Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public BigDecimal getTotalCashValue() {
		return totalCashValue;
	}
	public void setTotalCashValue(BigDecimal totalCashValue) {
		this.totalCashValue = totalCashValue;
	}
	public int getProductKey() {
		return productKey;
	}
	public void setProductKey(int productKey) {
		this.productKey = productKey;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public BigDecimal getCommission() {
		return commission;
	}
	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}
	public String getProductSymbol() {
		return productSymbol;
	}
	public void setProductSymbol(String productSymbol) {
		this.productSymbol = productSymbol;
	}
}

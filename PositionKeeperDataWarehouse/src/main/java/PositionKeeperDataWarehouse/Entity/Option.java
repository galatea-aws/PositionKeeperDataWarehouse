package PositionKeeperDataWarehouse.Entity;

import java.util.Date;

public class Option extends Product {
	private int optionKey;
	private Double strikePrice;
	private Date expirationDate;
	private int underlyingStockKey;
	private String underlyingStockSymbol;
	private String optionType;
	public int getOptionKey() {
		return optionKey;
	}
	public void setOptionKey(int optionKey) {
		this.optionKey = optionKey;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public int getUnderlyingStockKey() {
		return underlyingStockKey;
	}
	public void setUnderlyingStockKey(int underlyingStockKey) {
		this.underlyingStockKey = underlyingStockKey;
	}
	public String getOptionType() {
		return optionType;
	}
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}
	public String getUnderlyingStockSymbol() {
		return underlyingStockSymbol;
	}
	public void setUnderlyingStockSymbol(String underlyingStockSymbol) {
		this.underlyingStockSymbol = underlyingStockSymbol;
	}
	public Double getStrikePrice() {
		return strikePrice;
	}
	public void setStrikePrice(Double strikePrice) {
		this.strikePrice = strikePrice;
	}
}

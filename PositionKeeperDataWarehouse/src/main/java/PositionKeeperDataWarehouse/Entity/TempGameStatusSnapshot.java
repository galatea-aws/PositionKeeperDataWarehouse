package PositionKeeperDataWarehouse.Entity;

import java.math.BigDecimal;

public class TempGameStatusSnapshot {
	private int accountKey;
	private int gameKey;
	private BigDecimal profit;
	
	public int getAccountKey() {
		return accountKey;
	}
	public void setAccountKey(int accountKey) {
		this.accountKey = accountKey;
	}
	public int getGameKey() {
		return gameKey;
	}
	public void setGameKey(int gameKey) {
		this.gameKey = gameKey;
	}
	public BigDecimal getProfit() {
		return profit;
	}
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
}

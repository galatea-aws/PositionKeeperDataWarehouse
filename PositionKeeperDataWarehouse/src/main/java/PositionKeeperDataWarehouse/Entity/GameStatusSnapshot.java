package PositionKeeperDataWarehouse.Entity;

import java.math.BigDecimal;

public class GameStatusSnapshot {
	private int gameStatusSnapshotKey;
	private int accountKey;
	private int dataLoadLogKey;
	private int rank;
	private BigDecimal accountValue;
	private BigDecimal cash;
	private BigDecimal profit;
	private BigDecimal buyingPower;
	public int getGameStatusSnapshotKey() {
		return gameStatusSnapshotKey;
	}
	public void setGameStatusSnapshotKey(int gameStatusSnapshotKey) {
		this.gameStatusSnapshotKey = gameStatusSnapshotKey;
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
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public BigDecimal getAccountValue() {
		return accountValue;
	}
	public void setAccountValue(BigDecimal accountValue) {
		this.accountValue = accountValue;
	}
	public BigDecimal getCash() {
		return cash;
	}
	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}
	public BigDecimal getProfit() {
		return profit;
	}
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	public BigDecimal getBuyingPower() {
		return buyingPower;
	}
	public void setBuyingPower(BigDecimal buyingPower) {
		this.buyingPower = buyingPower;
	}
}

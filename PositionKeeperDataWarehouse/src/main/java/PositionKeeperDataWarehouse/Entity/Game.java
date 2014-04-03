package PositionKeeperDataWarehouse.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game {
	private int gameKey;
	private String gameName;
	private String currency;
	private Date startDate;
	private Date endDate;
	private String gameId;
	private DataLoadLog latestDataLoadLog;
	private List<Account> accountList = new ArrayList<Account>();
	public int getGameKey() {
		return gameKey;
	}
	public void setGameKey(int gameKey) {
		this.gameKey = gameKey;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Game Key: ").append(gameKey);
		sb.append("Game Id: ").append(gameId);
		sb.append("Game Name: ").append(gameName);
		sb.append("Currency: ").append(currency);
		sb.append("Start Date: ").append(startDate);
		sb.append("End Date: ").append(endDate);
		return sb.toString();
		
	}
	public DataLoadLog getLatestDataLoadLog() {
		return latestDataLoadLog;
	}
	public void setLatestDataLoadLog(DataLoadLog latestDataLoadLog) {
		this.latestDataLoadLog = latestDataLoadLog;
	}
	public List<Account> getAccountList() {
		return accountList;
	}
	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}
}

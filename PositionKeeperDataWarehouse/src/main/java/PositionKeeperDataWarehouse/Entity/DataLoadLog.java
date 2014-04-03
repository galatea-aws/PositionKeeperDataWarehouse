package PositionKeeperDataWarehouse.Entity;

import java.util.Date;

public class DataLoadLog {
	private int dataLoadLogKey;
	private int gameKey;
	private Date snapshotDate;
	public int getDataLoadLogKey() {
		return dataLoadLogKey;
	}
	public void setDataLoadLogKey(int dataLoadLogKey) {
		this.dataLoadLogKey = dataLoadLogKey;
	}
	public int getGameKey() {
		return gameKey;
	}
	public void setGameKey(int gameKey) {
		this.gameKey = gameKey;
	}
	public Date getSnapshotDate() {
		return snapshotDate;
	}
	public void setSnapshotDate(Date snapshotDate) {
		this.snapshotDate = snapshotDate;
	}
}

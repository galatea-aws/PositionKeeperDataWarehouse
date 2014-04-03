package PositionKeeperDataWarehouse.Dao;

import PositionKeeperDataWarehouse.Entity.DataLoadLog;

public interface IDataLoadLogDao {
	public void createDataLoadLog(DataLoadLog dataLoadLog);
	public DataLoadLog getLastestDataLoadLogByGameKey(int GameKey);
	public void deleteAllDataLoadLog();
}

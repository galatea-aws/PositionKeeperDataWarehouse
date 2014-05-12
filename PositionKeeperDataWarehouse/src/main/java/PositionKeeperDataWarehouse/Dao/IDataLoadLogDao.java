package PositionKeeperDataWarehouse.Dao;

import java.util.List;
import java.util.Map;

import PositionKeeperDataWarehouse.Entity.DataLoadLog;

public interface IDataLoadLogDao {
	public void createDataLoadLog(DataLoadLog dataLoadLog);
	public DataLoadLog getLastestDataLoadLogByGameKey(int GameKey);
	public DataLoadLog getPreviousDataLoadLog(Map<String, Integer> map);
	public DataLoadLog getDataLoadLogByKey(int dataLoadLogKey);
	public void deleteAllDataLoadLog();
	public List<DataLoadLog> getAllDataLoadLog();
	public List<DataLoadLog> getAllDataLoadLogById(int dataLoadLogKey);
}

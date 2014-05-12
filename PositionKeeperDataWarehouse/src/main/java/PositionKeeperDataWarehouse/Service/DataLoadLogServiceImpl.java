package PositionKeeperDataWarehouse.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import PositionKeeperDataWarehouse.Dao.IDataLoadLogDao;
import PositionKeeperDataWarehouse.Entity.DataLoadLog;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Service.Interface.IDataLoadLogService;

public class DataLoadLogServiceImpl implements IDataLoadLogService {

	private IDataLoadLogDao dataLoadLogDao; 
	
	public void createDataLoadLog(List<Game> gameList) {
		for(Game game : gameList){
			DataLoadLog dataLoadLog = new DataLoadLog();
			dataLoadLog.setGameKey(game.getGameKey());
			dataLoadLog.setSnapshotDate(new Date());
			dataLoadLogDao.createDataLoadLog(dataLoadLog);
			
			DataLoadLog latestDataLoadLog = dataLoadLogDao.getLastestDataLoadLogByGameKey(game.getGameKey());
			game.setLatestDataLoadLog(latestDataLoadLog);
		}
	}

	public IDataLoadLogDao getDataLoadLogDao() {
		return dataLoadLogDao;
	}

	public void setDataLoadLogDao(IDataLoadLogDao dataLoadLogDao) {
		this.dataLoadLogDao = dataLoadLogDao;
	}

	public DataLoadLog getLastestDataLoadLogByGameKey(int GameKey) {
		return dataLoadLogDao.getLastestDataLoadLogByGameKey(GameKey);
	}

	public void deleteAllDataLoadLog() {
		dataLoadLogDao.deleteAllDataLoadLog();
	}

	public DataLoadLog getPrevisouDataLoadLog(int dataLoadLogKey) {
		DataLoadLog dataLoadLog = dataLoadLogDao.getDataLoadLogByKey(dataLoadLogKey);
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("dataLoadLogKey", dataLoadLogKey);
		map.put("gameKey", dataLoadLog.getGameKey());
		return dataLoadLogDao.getPreviousDataLoadLog(map);
	}

	public List<DataLoadLog> getAllDataLoadLog() {
		return dataLoadLogDao.getAllDataLoadLog();
	}

	public List<DataLoadLog> getAllDataLoadLogById(int dataLoadLogKey) {
		return dataLoadLogDao.getAllDataLoadLogById(dataLoadLogKey);
	}
}

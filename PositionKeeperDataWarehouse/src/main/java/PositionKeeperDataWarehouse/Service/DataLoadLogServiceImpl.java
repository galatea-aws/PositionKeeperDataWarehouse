package PositionKeeperDataWarehouse.Service;

import java.util.Date;
import java.util.List;

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
}

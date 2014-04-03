package PositionKeeperDataWarehouse.Service;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.DataLoadLog;
import PositionKeeperDataWarehouse.Entity.Game;

public interface IDataLoadLogService {
	public void createDataLoadLog(List<Game> gameList);
	public DataLoadLog getLastestDataLoadLogByGameKey(int gameKey);
}

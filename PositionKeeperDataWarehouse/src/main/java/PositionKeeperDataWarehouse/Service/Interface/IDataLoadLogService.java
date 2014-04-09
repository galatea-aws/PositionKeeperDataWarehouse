package PositionKeeperDataWarehouse.Service.Interface;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.DataLoadLog;
import PositionKeeperDataWarehouse.Entity.Game;

public interface IDataLoadLogService {
	public void createDataLoadLog(List<Game> gameList);
}

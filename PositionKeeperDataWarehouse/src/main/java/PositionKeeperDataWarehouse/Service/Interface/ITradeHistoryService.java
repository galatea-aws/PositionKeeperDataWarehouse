package PositionKeeperDataWarehouse.Service.Interface;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.Game;

public interface ITradeHistoryService {
	public void updateTradeHistory(List<Game> gameList) throws Exception;
}

package PositionKeeperDataWarehouse.Service.Interface;

import java.util.List;
import java.util.Map;

import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Entity.TradeHistory;

public interface ITradeHistoryService {
	public void updateTradeHistory(List<Game> gameList) throws Exception;
	public TradeHistory getLatestTradeHistoryByAccountGame(Map<String, Integer> map);
}

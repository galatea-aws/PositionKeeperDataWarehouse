package PositionKeeperDataWarehouse.Dao;

import java.util.List;
import java.util.Map;

import PositionKeeperDataWarehouse.Entity.TradeHistory;

public interface ITradeHistoryDao {
	public TradeHistory getLatestTradeHistoryByAccountGame(Map<String, Integer> map);
	public void createTradeHistories(List<TradeHistory> tradeHistoryList);
}

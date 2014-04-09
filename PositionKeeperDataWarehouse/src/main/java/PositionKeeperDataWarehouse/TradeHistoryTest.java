package PositionKeeperDataWarehouse;

import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Service.AccountServiceImpl;
import PositionKeeperDataWarehouse.Service.GameServiceImpl;
import PositionKeeperDataWarehouse.Service.TradeHistoryServiceImpl;
import PositionKeeperDataWarehouse.Service.Interface.IGameService;
import PositionKeeperDataWarehouse.Service.Interface.ITradeHistoryService;

public class TradeHistoryTest {
	public static void main(String[] args) {
		ITradeHistoryService tradeHistoryService;
		IGameService gameService;
		ConfigurableApplicationContext context = 
				new ClassPathXmlApplicationContext("App.xml");
		tradeHistoryService = (TradeHistoryServiceImpl)context.getBean("tradeHistoryService");
		gameService = (GameServiceImpl)context.getBean("gameService");
		List<Game> gameList = gameService.getAllGames();
		try {
			tradeHistoryService.updateTradeHistory(gameList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

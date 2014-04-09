package PositionKeeperDataWarehouse;

import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Service.GameServiceImpl;
import PositionKeeperDataWarehouse.Service.TradeHistoryServiceImpl;
import PositionKeeperDataWarehouse.Service.Interface.IGameService;
import PositionKeeperDataWarehouse.Service.Interface.IPositionDetailService;
import PositionKeeperDataWarehouse.Service.Interface.ITradeHistoryService;

public class PositionDetailTest {
	public static void main(String[] args) {
		IPositionDetailService positionDetailService;
		IGameService gameService;
		ConfigurableApplicationContext context = 
				new ClassPathXmlApplicationContext("App.xml");
		positionDetailService = (IPositionDetailService)context.getBean("positionDetailService");
		gameService = (IGameService)context.getBean("gameService");
		List<Game> gameList = gameService.getAllGames();
		try {
			positionDetailService.createPositionDetailSnapshot(gameList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package PositionKeeperDataWarehouse.Service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import PositionKeeperDataWarehouse.Entity.DataLoadLog;
import PositionKeeperDataWarehouse.Entity.Game;

public class DataLoadLogServiceImplTest {

	private DataLoadLogServiceImpl dataLoadLogService;
	private IGameService gameService;
	
    @Before
    public void setUp() {
		ConfigurableApplicationContext context = 
				new ClassPathXmlApplicationContext("App.xml");
		dataLoadLogService = (DataLoadLogServiceImpl)context.getBean("dataLoadLogService");
		gameService = (IGameService)context.getBean("gameService");
    }
 
    @After
    public void tearDown() {
    }
	
    
	@Test
	public void testCreateDataLoadLog() {
		List<Game> gameList = gameService.getAllGames();
		dataLoadLogService.createDataLoadLog(gameList);
	}
	@Test
	public void testGetLastestDataLoadLogByGameKey(){
		List<Game> gameList = gameService.getAllGames();
		for(Game game: gameList){
			DataLoadLog dataLoadLog = dataLoadLogService.getLastestDataLoadLogByGameKey(game.getGameKey());
			System.out.println(dataLoadLog.getDataLoadLogKey());
		}
	}
}

package PositionKeeperDataWarehouse.Service;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import PositionKeeperDataWarehouse.Entity.Game;

public class GameServiceImplTest {
	
	private GameServiceImpl gameService;
	
    @Before
    public void setUp() {
		ConfigurableApplicationContext context = 
				new ClassPathXmlApplicationContext("App.xml");
		gameService = (GameServiceImpl)context.getBean("gameService");
    }
 
    @After
    public void tearDown() {
    }
	
	public void testCreateGames() {
		try {
			gameService.createGames();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testConvertTableToGames(){
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testGetAllGames() {
		try {
			List<Game> gameList = gameService.getAllGames();
			for(Game game : gameList){
				System.out.println(game.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUpdateGameInfo(){
		try {
			gameService.updateGameInfo();
			int gameCount = gameService.getAllGames().size();
			gameService.updateGameInfo();
			int gameCount2 = gameService.getAllGames().size();
			assertEquals(gameCount, gameCount2);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Errors");
		}
	}
}

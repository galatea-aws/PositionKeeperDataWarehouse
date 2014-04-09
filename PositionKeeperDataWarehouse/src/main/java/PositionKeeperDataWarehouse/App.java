package PositionKeeperDataWarehouse;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import PositionKeeperDataWarehouse.Entity.DataLoadLog;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Service.Interface.IAccountService;
import PositionKeeperDataWarehouse.Service.Interface.IDataLoadLogService;
import PositionKeeperDataWarehouse.Service.Interface.IGameService;
import PositionKeeperDataWarehouse.Service.Interface.ITradeHistoryService;

/**
 * Hello world!
 *
 */
public class App 
{
	public static Logger logger = LogManager.getLogger(App.class.getName());
	private IDataLoadLogService dataLoadLogService;
	private IAccountService accountService;
	private IGameService gameService;
	private ITradeHistoryService tradeHistoryService;
	
    public static void main( String[] args )
    {
		ConfigurableApplicationContext context = 
				new ClassPathXmlApplicationContext("App.xml");
		App app = (App)context.getBean("app");
		app.run();
    }

    public void run(){
    	//Update game information
    	try {
			gameService.updateGameInfo();
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("Error in getting game information");
			return;
		}
    	logger.info("Update game information done");
    	
    	//Create DataLoadLog
    	List<Game> gameList = gameService.getAllGames();
		dataLoadLogService.createDataLoadLog(gameList);
		
		//Update AccountInfo
		try {
			getAccountService().updateAccount(gameList);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	public IGameService getGameService() {
		return gameService;
	}

	public void setGameService(IGameService gameService) {
		this.gameService = gameService;
	}

	public IDataLoadLogService getDataLoadLogService() {
		return dataLoadLogService;
	}

	public void setDataLoadLogService(IDataLoadLogService dataLoadLogService) {
		this.dataLoadLogService = dataLoadLogService;
	}

	public IAccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(IAccountService accountService) {
		this.accountService = accountService;
	}

	public ITradeHistoryService getTradeHistoryService() {
		return tradeHistoryService;
	}

	public void setTradeHistoryService(ITradeHistoryService tradeHistoryService) {
		this.tradeHistoryService = tradeHistoryService;
	}
}

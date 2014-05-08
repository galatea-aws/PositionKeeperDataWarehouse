package PositionKeeperDataWarehouse;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import PositionKeeperDataWarehouse.Entity.DataLoadLog;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Service.Interface.IAccountService;
import PositionKeeperDataWarehouse.Service.Interface.IDataLoadLogService;
import PositionKeeperDataWarehouse.Service.Interface.IGameService;
import PositionKeeperDataWarehouse.Service.Interface.IPositionDetailService;
import PositionKeeperDataWarehouse.Service.Interface.IProductService;
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
	private IPositionDetailService positionDetailService;
	private HttpHelper httpHelper;
	private IProductService productService;
	
    public static void main( String[] args )
    {
		ConfigurableApplicationContext context = 
				new ClassPathXmlApplicationContext("App.xml");
		App app = (App)context.getBean("app");
		app.run();
    }

    public void run(){
		if(!httpHelper.isLogin()){

			return;
		}
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
			getAccountService().checkAccountInfo(gameList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Update PositionDetail
		try {
			positionDetailService.createPositionDetailSnapshot(gameList);
			//positionDetailService.updatePositionDetailSnapshot();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Update TradeHistory
		try {
			tradeHistoryService.updateTradeHistory(gameList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			productService.updateProductDetails();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

	public IPositionDetailService getPositionDetailService() {
		return positionDetailService;
	}

	public void setPositionDetailService(IPositionDetailService positionDetailService) {
		this.positionDetailService = positionDetailService;
	}

	public HttpHelper getHttpHelper() {
		return httpHelper;
	}

	public void setHttpHelper(HttpHelper httpHelper) {
		this.httpHelper = httpHelper;
	}

	public IProductService getProductService() {
		return productService;
	}

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}
}

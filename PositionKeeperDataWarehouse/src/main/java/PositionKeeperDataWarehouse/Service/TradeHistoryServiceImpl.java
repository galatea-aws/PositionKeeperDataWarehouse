package PositionKeeperDataWarehouse.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import PositionKeeperDataWarehouse.Dao.IGameStatusSnapshotDao;
import PositionKeeperDataWarehouse.Dao.IProductDao;
import PositionKeeperDataWarehouse.Dao.ITradeHistoryDao;
import PositionKeeperDataWarehouse.Entity.Account;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Entity.Option;
import PositionKeeperDataWarehouse.Entity.Product;
import PositionKeeperDataWarehouse.Entity.TempGameStatusSnapshot;
import PositionKeeperDataWarehouse.Entity.TradeHistory;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Helper.InsertHelper;
import PositionKeeperDataWarehouse.Helper.OptionParser;
import PositionKeeperDataWarehouse.Service.HttpThread.TradeHistoryPageThread;
import PositionKeeperDataWarehouse.Service.Interface.IProductService;
import PositionKeeperDataWarehouse.Service.Interface.ITradeHistoryService;

public class TradeHistoryServiceImpl implements ITradeHistoryService {
	public static Logger logger = LogManager.getLogger(TradeHistoryServiceImpl.class.getName());
	private IGameStatusSnapshotDao gameStatusSnapshotDao;
	private IProductService productService;
	private HttpHelper httpHelper;
	private ITradeHistoryDao tradeHistoryDao;
	
	public void updateTradeHistory(List<Game> gameList) throws Exception {
		for(Game game : gameList){
			List<TempGameStatusSnapshot> tempGameStatusSnapshotList = gameStatusSnapshotDao.getTempGameStatusSnapshotByGameKey(game.getGameKey());
			List<String> symbolList = new ArrayList<String>();
			List<TradeHistory> tradeHistoryList = new ArrayList<TradeHistory>();
			//Get trade history
			int threadCount = 20;
			TradeHistoryPageThread[] threadList = new TradeHistoryPageThread[threadCount+1];
			int pagePerThread = tempGameStatusSnapshotList.size()/(threadCount);
			//int pagePerThread = 2;
			for(int i=0;i<threadList.length;i++){
				int start = pagePerThread *i;
				int end = Math.min(start+pagePerThread,tempGameStatusSnapshotList.size());
				threadList[i] = new TradeHistoryPageThread(httpHelper,game,start,end,tempGameStatusSnapshotList,this);
			}
			int modresult = tempGameStatusSnapshotList.size()%threadCount;
			threadList[threadCount] = new TradeHistoryPageThread(httpHelper,game,pagePerThread*threadCount,pagePerThread*threadCount+modresult,tempGameStatusSnapshotList,this);
			
			for(int i=0;i<threadList.length;i++){
				threadList[i].start();
			}
			
			for(int i=0;i<threadList.length;i++){
				threadList[i].join();
			}
			
			//Update product info
			for(int i=0;i<threadList.length;i++){
				symbolList.addAll(threadList[i].getSymbolList());
			}
			
			logger.info("Update Product");
			productService.updateProduct(symbolList);
			logger.info("Update Done");
			
			//Update trade info
			for(int i=0;i<threadList.length;i++){
				tradeHistoryList.addAll(threadList[i].getTradeHistory());
			}
			
			logger.info("Check Product " + tradeHistoryList.size());
			int i = 0;
			for(TradeHistory tradeHistory: tradeHistoryList){
				Product product = productService.getProductBySymbol(tradeHistory.getProductSymbol());
				if(product==null)
					tradeHistory.setProductKey(-1);
				else
					tradeHistory.setProductKey(product.getProductKey());
				i++;
				if(i%1000==0){
					logger.info(i);
				}
			}
			logger.info("Check Product Done");
			
			logger.info("Insert Product");
			InsertHelper<TradeHistory> insertHelper = new InsertHelper<TradeHistory>();
			insertHelper.insert(tradeHistoryList, "createTradeHistories", tradeHistoryDao, 80);
			logger.info("Insert Product Done");
		}
	}
    
	public TradeHistory getLatestTradeHistoryByAccountGame(
			Map<String, Integer> map) {
		return tradeHistoryDao.getLatestTradeHistoryByAccountGame(map);
	}
	
	public IGameStatusSnapshotDao getGameStatusSnapshotDao() {
		return gameStatusSnapshotDao;
	}

	public void setGameStatusSnapshotDao(IGameStatusSnapshotDao gameStatusSnapshotDao) {
		this.gameStatusSnapshotDao = gameStatusSnapshotDao;
	}


	public HttpHelper getHttpHelper() {
		return httpHelper;
	}


	public void setHttpHelper(HttpHelper httpHelper) {
		this.httpHelper = httpHelper;
	}

	public ITradeHistoryDao getTradeHistoryDao() {
		return tradeHistoryDao;
	}

	public void setTradeHistoryDao(ITradeHistoryDao tradeHistoryDao) {
		this.tradeHistoryDao = tradeHistoryDao;
	}

	public IProductService getProductService() {
		return productService;
	}

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}
}

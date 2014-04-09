package PositionKeeperDataWarehouse.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import PositionKeeperDataWarehouse.Helper.OptionParser;
import PositionKeeperDataWarehouse.Service.HttpThread.TradeHistoryPageThread;
import PositionKeeperDataWarehouse.Service.Interface.ITradeHistoryService;

public class TradeHistoryServiceImpl implements ITradeHistoryService {

	private IGameStatusSnapshotDao gameStatusSnapshotDao;
	private IProductDao productDao;
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
			//int pagePerThread = tempGameStatusSnapshotList.size()/(threadCount);
			int pagePerThread = 50;
			for(int i=0;i<threadList.length;i++){
				int start = pagePerThread *i;
				int end = Math.min(start+pagePerThread,tempGameStatusSnapshotList.size());
				threadList[i] = new TradeHistoryPageThread(httpHelper,game,start,end,tempGameStatusSnapshotList,tradeHistoryDao);
			}
			int modresult = tempGameStatusSnapshotList.size()%threadCount;
			threadList[threadCount] = new TradeHistoryPageThread(httpHelper,game,pagePerThread*threadCount,pagePerThread*threadCount+modresult,tempGameStatusSnapshotList,tradeHistoryDao);
			
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
			updateproduct(symbolList);
			
			//Update trade info
			for(int i=0;i<threadList.length;i++){
				tradeHistoryList.addAll(threadList[i].getTradeHistory());
			}
			createTradeHistory(tradeHistoryList);
		}
	}

	public void updateproduct(List<String> symbolList){
		Set<String> symbolSet = new HashSet<String>(symbolList);
		for(String symbol: symbolSet){
			if(symbol==null){
				System.out.println("Null product symbol");
			}
			if(symbol.length()>6){
				updateOption(symbol);
			}
			else{
				updateProduct(symbol);
			}
		}
	}
	
	public Product updateProduct(String symbol){
		//Product exist
		Product product = productDao.getProductBySymbol(symbol);
		if(product!=null)
			return product;
		
		//Insert product
		product = new Product();
		product.setSymbol(symbol);
		product.setDescription("");
		product.setOption(false);
		productDao.createProduct(product);
		return product;
	}
	
	public Option updateOption(String symbol){
		//Parse option by symbol
		Option option = OptionParser.convertToOption(symbol);
		if(option==null)
			return null;
		
		//Option exist
		Option checkedOption = productDao.getOptionBySymbol(option.getSymbol());
		if(checkedOption!=null)
			return checkedOption;
		
		String underlyingProductSymbol = option.getUnderlyingStockSymbol();
		
		//Check underlying product
		Product underlyingProduct = updateProduct(underlyingProductSymbol);
		option.setUnderlyingStockKey(underlyingProduct.getProductKey());
		
		//Insert option
		Product product = (Product)option;	
		productDao.createProduct(product);
		option.setOptionKey(product.getProductKey());
		productDao.createOption(option);
		return option;
	}
	
    public void createTradeHistory(List<TradeHistory> tradeHistoryList){
    	List<TradeHistory> newTradeHistoryList = new ArrayList<TradeHistory>();
    	for(TradeHistory tradeHistory : tradeHistoryList){
    		Product product = productDao.getProductBySymbol(tradeHistory.getProductSymbol());
    		if(product==null)
    			tradeHistory.setProductKey(-1);
    		else {
    			tradeHistory.setProductKey(product.getProductKey());
			}
    		newTradeHistoryList.add(tradeHistory);
    		if(newTradeHistoryList.size()==30){
    			tradeHistoryDao.createTradeHistories(newTradeHistoryList);
    			newTradeHistoryList.clear();
    		}
    	}
    	if(newTradeHistoryList.size()>0)
    		tradeHistoryDao.createTradeHistories(newTradeHistoryList);
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

	public IProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(IProductDao productDao) {
		this.productDao = productDao;
	}

	public ITradeHistoryDao getTradeHistoryDao() {
		return tradeHistoryDao;
	}

	public void setTradeHistoryDao(ITradeHistoryDao tradeHistoryDao) {
		this.tradeHistoryDao = tradeHistoryDao;
	}
}

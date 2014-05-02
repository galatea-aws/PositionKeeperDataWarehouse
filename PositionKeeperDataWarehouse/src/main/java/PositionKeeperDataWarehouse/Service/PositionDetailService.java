package PositionKeeperDataWarehouse.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import PositionKeeperDataWarehouse.Dao.IAccountDao;
import PositionKeeperDataWarehouse.Dao.IGameStatusSnapshotDao;
import PositionKeeperDataWarehouse.Dao.IPositionDetailDao;
import PositionKeeperDataWarehouse.Dao.IProductDao;
import PositionKeeperDataWarehouse.Entity.Account;
import PositionKeeperDataWarehouse.Entity.DataLoadLog;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Entity.GameStatusSnapshot;
import PositionKeeperDataWarehouse.Entity.PositionDetail;
import PositionKeeperDataWarehouse.Entity.Product;
import PositionKeeperDataWarehouse.Entity.TempGameStatusSnapshot;
import PositionKeeperDataWarehouse.Entity.TradeHistory;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Helper.InsertHelper;
import PositionKeeperDataWarehouse.Service.HttpThread.PositionPageThread;
import PositionKeeperDataWarehouse.Service.HttpThread.TradeHistoryPageThread;
import PositionKeeperDataWarehouse.Service.Interface.IAccountService;
import PositionKeeperDataWarehouse.Service.Interface.IDataLoadLogService;
import PositionKeeperDataWarehouse.Service.Interface.IPositionDetailService;
import PositionKeeperDataWarehouse.Service.Interface.IProductService;
import PositionKeeperDataWarehouse.Service.Interface.ITradeHistoryService;

public class PositionDetailService implements IPositionDetailService {
	public static Logger logger = LogManager.getLogger(PositionDetailService.class.getName());
	private IGameStatusSnapshotDao gameStatusSnapshotDao;
	private IProductService productService;
	private IAccountService accountService;
	private HttpHelper httpHelper;
	private IPositionDetailDao positionDetailDao;
	private IDataLoadLogService dataLoadLogService;
	
	public void createPositionDetailSnapshot(List<Game> gameList)
			throws Exception {
		for(Game game: gameList){
			
			List<TempGameStatusSnapshot> tempGameStatusSnapshotList = getGameStatusSnapshotDao().getTempGameStatusSnapshotByGameKey(game.getGameKey());
			List<GameStatusSnapshot> gameStatusSnapshotList = new ArrayList<GameStatusSnapshot>();
			List<PositionDetail> positionDetailList = new ArrayList<PositionDetail>();
			List<String> symbolList = new ArrayList<String>();
			
			//Get position detail
			int threadCount = 20;
			PositionPageThread[] threadList = new PositionPageThread[threadCount+1];
			int pagePerThread = tempGameStatusSnapshotList.size()/(threadCount);
			//int pagePerThread = 2;
			for(int i=0;i<threadList.length;i++){
				int start = pagePerThread *i;
				int end = Math.min(start+pagePerThread,tempGameStatusSnapshotList.size());
				threadList[i] = new PositionPageThread(getHttpHelper(),game,start,end,tempGameStatusSnapshotList,accountService,productService);
			}
			int modresult = tempGameStatusSnapshotList.size()%threadCount;
			threadList[threadCount] = new PositionPageThread(getHttpHelper(),game,pagePerThread*threadCount,
					pagePerThread*threadCount+modresult,tempGameStatusSnapshotList,accountService,productService);
			
			for(int i=0;i<threadList.length;i++){
				threadList[i].start();
			}
			
			for(int i=0;i<threadList.length;i++){
				threadList[i].join();
			}
			
			for(int i=0;i<threadList.length;i++){
				List<Account> accountList = threadList[i].getAccountList();
				for(Account account : accountList){
					accountService.updateAccount(account);
				}
				gameStatusSnapshotList.addAll(threadList[i].getGameStatusSnapshotList());
				positionDetailList.addAll(threadList[i].getPositionDetailList());
				symbolList.addAll(threadList[i].getSymbolList());
			}
			productService.updateProduct(symbolList);
			
			//Insert GameStatusSnapshot
			InsertHelper<GameStatusSnapshot> insertHelper = new InsertHelper<GameStatusSnapshot>();
			insertHelper.insert(gameStatusSnapshotList, "createGameStatusSnapshots", gameStatusSnapshotDao, 80);
			
			for(PositionDetail positionDetail : positionDetailList){
				Product product = productService.getProductBySymbol(positionDetail.getProductSymbol());
				if(product==null)
					positionDetail.setProductKey(-1);
				else
					positionDetail.setProductKey(product.getProductKey());
			}
			//Insert PositionDetail
			InsertHelper<PositionDetail> positionDetailInsertHelper = new InsertHelper<PositionDetail>();
			positionDetailInsertHelper.insert(positionDetailList, "createPositionDetailSnapshots", positionDetailDao, 80);
		}
		
		for(Game game: gameList){
			List<PositionDetail> positionDetailList = new ArrayList<PositionDetail>();
			positionDetailList = positionDetailDao.getPositionDetailByDataLoadLogKey(game.getLatestDataLoadLog().getDataLoadLogKey());
			updatePositionDetailQuantity(positionDetailList);
			updatePositionDetailDelta(game.getLatestDataLoadLog());
		}
	}
	
	public void updatePositionDetailSnapshot() {
		List<PositionDetail> positionDetailList = positionDetailDao.getAllPositionDetailSnapshot();
		updatePositionDetailQuantity(positionDetailList);
		
		List<DataLoadLog> dataLoadLogList = dataLoadLogService.getAllDataLoadLog();
		for(DataLoadLog dataLoadLog : dataLoadLogList){
			updatePositionDetailDelta(dataLoadLog);
		}

	}
	
	public void updatePositionDetailDelta(DataLoadLog dataLoadLog){
		int i = 0;
		DataLoadLog previousDataLoadLog = dataLoadLogService.getPrevisouDataLoadLog(dataLoadLog.getDataLoadLogKey());
		if(previousDataLoadLog==null)
			return;
		List<PositionDetail> positionDetailList = new ArrayList<PositionDetail>();
		List<PositionDetail> toInsert = new ArrayList<PositionDetail>();
		//First
		positionDetailList = positionDetailDao.getPositionDetailByDataLoadLogKey(dataLoadLog.getDataLoadLogKey());
		toInsert = new ArrayList<PositionDetail>();
		logger.info("DataLoadLog:" + dataLoadLog.getDataLoadLogKey());
		for(PositionDetail positionDetail : positionDetailList){
			if(i%1000==0)
				logger.info(i);
			i++;
			if(positionDetail.getQuantity().compareTo(BigInteger.ZERO)==0)
				continue;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dataLoadLogKey", previousDataLoadLog.getDataLoadLogKey());
			map.put("productKey", positionDetail.getProductKey());
			map.put("accountKey", positionDetail.getAccountKey());
			map.put("direction", positionDetail.getDirection());
			PositionDetail previousPositionDetail = positionDetailDao.getPositionDetailByDataLoadLogKeyAccountKeyProductKey(map);
			if(previousPositionDetail==null){
				previousPositionDetail = positionDetail.clone();
				previousPositionDetail.setDataLoadLogKey(previousDataLoadLog.getDataLoadLogKey());
				previousPositionDetail.setQuantity(BigInteger.ZERO);
				previousPositionDetail.setTotalValue(BigDecimal.ZERO);
				previousPositionDetail.setQuantityDelta(BigInteger.ZERO);
				toInsert.add(previousPositionDetail);
				logger.info(i);
			}
			positionDetail.setQuantityDelta(positionDetail.getQuantity().subtract(previousPositionDetail.getQuantity()));
			positionDetailDao.updatePositionDetailSnapshot(positionDetail);
		}
		
		List<PositionDetail> previousPositionDetailList = positionDetailDao.getPositionDetailByDataLoadLogKey(previousDataLoadLog.getDataLoadLogKey());
		for(PositionDetail previousPositionDetail : previousPositionDetailList){
			if(i%1000==0)
				logger.info(i);
			i++;
			if(previousPositionDetail.getQuantity().compareTo(BigInteger.ZERO)==0)
				continue;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dataLoadLogKey", dataLoadLog.getDataLoadLogKey());
			map.put("productKey", previousPositionDetail.getProductKey());
			map.put("accountKey", previousPositionDetail.getAccountKey());
			map.put("direction", previousPositionDetail.getDirection());
			PositionDetail positionDetail = positionDetailDao.getPositionDetailByDataLoadLogKeyAccountKeyProductKey(map);
			if(positionDetail==null){
				positionDetail = previousPositionDetail.clone();
				positionDetail.setDataLoadLogKey(dataLoadLog.getDataLoadLogKey());
				positionDetail.setQuantity(BigInteger.ZERO);
				positionDetail.setTotalValue(BigDecimal.ZERO);
				positionDetail.setQuantityDelta(positionDetail.getQuantity().subtract(previousPositionDetail.getQuantity()));
				toInsert.add(positionDetail);
				logger.info(i);
			}
		}
		
		//Insert PositionDetail
		InsertHelper<PositionDetail> positionDetailInsertHelper = new InsertHelper<PositionDetail>();
		try {
			positionDetailInsertHelper.insert(toInsert, "createPositionDetailSnapshots", positionDetailDao, 80);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updatePositionDetailQuantity(List<PositionDetail> positionDetailList){
		for(PositionDetail positionDetail : positionDetailList){		
			if(positionDetail.getDirection().equals("Long")&&positionDetail.getQuantity().compareTo(new BigInteger("0"))<0){
				positionDetail.setDirection("Short");	
				positionDetail.setQuantity(positionDetail.getQuantity().multiply(new BigInteger("-1")));
			}
			else if(positionDetail.getDirection().equals("Short")&&positionDetail.getQuantity().compareTo(new BigInteger("0"))<0){
				positionDetail.setDirection("Long");
				positionDetail.setQuantity(positionDetail.getQuantity().multiply(new BigInteger("-1")));
				positionDetailDao.updatePositionDetailSnapshot(positionDetail);
				continue;
			}
			
			
			if(positionDetail.getDirection().equals("Short")&&positionDetail.getQuantity().compareTo(new BigInteger("0"))>0){
				positionDetail.setQuantity(positionDetail.getQuantity().multiply(new BigInteger("-1")));
				positionDetailDao.updatePositionDetailSnapshot(positionDetail);
				continue;
			}
		}
	}
	
	public HttpHelper getHttpHelper() {
		return httpHelper;
	}

	public void setHttpHelper(HttpHelper httpHelper) {
		this.httpHelper = httpHelper;
	}

	public IGameStatusSnapshotDao getGameStatusSnapshotDao() {
		return gameStatusSnapshotDao;
	}

	public void setGameStatusSnapshotDao(IGameStatusSnapshotDao gameStatusSnapshotDao) {
		this.gameStatusSnapshotDao = gameStatusSnapshotDao;
	}

	public IPositionDetailDao getPositionDetailDao() {
		return positionDetailDao;
	}

	public void setPositionDetailDao(IPositionDetailDao positionDetailDao) {
		this.positionDetailDao = positionDetailDao;
	}

	public IAccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(IAccountService accountService) {
		this.accountService = accountService;
	}

	public IProductService getProductService() {
		return productService;
	}

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	public IDataLoadLogService getDataLoadLogService() {
		return dataLoadLogService;
	}

	public void setDataLoadLogService(IDataLoadLogService dataLoadLogService) {
		this.dataLoadLogService = dataLoadLogService;
	}

}

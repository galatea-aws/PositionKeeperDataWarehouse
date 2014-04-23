package PositionKeeperDataWarehouse.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import PositionKeeperDataWarehouse.Dao.IAccountDao;
import PositionKeeperDataWarehouse.Dao.IGameStatusSnapshotDao;
import PositionKeeperDataWarehouse.Dao.IPositionDetailDao;
import PositionKeeperDataWarehouse.Dao.IProductDao;
import PositionKeeperDataWarehouse.Entity.Account;
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
import PositionKeeperDataWarehouse.Service.Interface.IPositionDetailService;
import PositionKeeperDataWarehouse.Service.Interface.IProductService;
import PositionKeeperDataWarehouse.Service.Interface.ITradeHistoryService;

public class PositionDetailService implements IPositionDetailService {

	private IGameStatusSnapshotDao gameStatusSnapshotDao;
	private IProductService productService;
	private IAccountService accountService;
	private HttpHelper httpHelper;
	private IPositionDetailDao positionDetailDao;
	
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

}

package PositionKeeperDataWarehouse.Service;

import java.util.List;

import PositionKeeperDataWarehouse.Dao.IAccountDao;
import PositionKeeperDataWarehouse.Dao.IGameStatusSnapshotDao;
import PositionKeeperDataWarehouse.Dao.IProductDao;
import PositionKeeperDataWarehouse.Entity.Account;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Entity.TempGameStatusSnapshot;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Service.HttpThread.PositionPageThread;
import PositionKeeperDataWarehouse.Service.HttpThread.TradeHistoryPageThread;
import PositionKeeperDataWarehouse.Service.Interface.IPositionDetailService;

public class PositionDetailService implements IPositionDetailService {

	private IGameStatusSnapshotDao gameStatusSnapshotDao;
	private IProductDao productDao;
	private IAccountDao accountDao;
	private HttpHelper httpHelper;
	
	public void createPositionDetailSnapshot(List<Game> gameList)
			throws Exception {
		for(Game game: gameList){
			List<TempGameStatusSnapshot> tempGameStatusSnapshotList = getGameStatusSnapshotDao().getTempGameStatusSnapshotByGameKey(game.getGameKey());
			
			//Get position detail
			int threadCount = 20;
			PositionPageThread[] threadList = new PositionPageThread[threadCount+1];
			//int pagePerThread = tempGameStatusSnapshotList.size()/(threadCount);
			int pagePerThread = 50;
			for(int i=0;i<threadList.length;i++){
				int start = pagePerThread *i;
				int end = Math.min(start+pagePerThread,tempGameStatusSnapshotList.size());
				threadList[i] = new PositionPageThread(getHttpHelper(),game,start,end,tempGameStatusSnapshotList,getAccountDao());
			}
			int modresult = tempGameStatusSnapshotList.size()%threadCount;
			threadList[threadCount] = new PositionPageThread(getHttpHelper(),game,pagePerThread*threadCount,pagePerThread*threadCount+modresult,tempGameStatusSnapshotList,getAccountDao());
			
			for(int i=0;i<threadList.length;i++){
				threadList[i].start();
			}
			
			for(int i=0;i<threadList.length;i++){
				threadList[i].join();
			}
			
			for(int i=0;i<threadList.length;i++){
				List<Account> accountList = threadList[i].getAccountList();
				for(Account account : accountList){
					getAccountDao().updateAccount(account);
				}
			}
		}
	}

	public IProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(IProductDao productDao) {
		this.productDao = productDao;
	}

	public IAccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(IAccountDao accountDao) {
		this.accountDao = accountDao;
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

}

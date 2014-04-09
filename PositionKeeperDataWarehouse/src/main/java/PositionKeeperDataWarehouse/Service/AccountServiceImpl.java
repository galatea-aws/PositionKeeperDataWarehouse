package PositionKeeperDataWarehouse.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import PositionKeeperDataWarehouse.App;
import PositionKeeperDataWarehouse.Dao.IAccountDao;
import PositionKeeperDataWarehouse.Dao.IGameStatusSnapshotDao;
import PositionKeeperDataWarehouse.Entity.Account;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Entity.TempGameStatusSnapshot;
import PositionKeeperDataWarehouse.Helper.HtmlHelper;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Service.HttpThread.GameRankPageThread;
import PositionKeeperDataWarehouse.Service.Interface.IAccountService;

public class AccountServiceImpl implements IAccountService {

	public static Logger logger = LogManager.getLogger(AccountServiceImpl.class.getName());
	private IAccountDao accountDao;
	private IGameStatusSnapshotDao gameStatusSnapshotDao;
	private HttpHelper httpHelper;

	public void updateAccount(List<Game> gameList) throws Exception {
		for (Game game : gameList) {
			updateAccount(game);
		}
	}

	public void updateAccount(Game game) throws Exception {
		
		List<Account> accountList = new ArrayList<Account>();
		List<TempGameStatusSnapshot> tempGameStatusSnapshotList = new ArrayList<TempGameStatusSnapshot>();
		//Get game rank page count
		
		String url = "http://www.investopedia.com/simulator/ranking/?RGID="
				+ game.getGameKey();
		String html = httpHelper.getHtml(url);
		
		int pageCount = HtmlHelper.getPageCount(html);
		int threadCount = 10;
		//Start 10 threads to get accounts info
		GameRankPageThread[] threadList = new GameRankPageThread[threadCount+1];
		int pagePerThread = pageCount/(threadCount);
		for(int i=0;i<threadList.length;i++){
			int startPage = 1 + pagePerThread *i;
			int endPage = Math.min(startPage+pagePerThread-1,pageCount);
			threadList[i] = new GameRankPageThread(httpHelper,game,startPage,endPage);
		}
		int modresult = pageCount%threadCount;
		int startPage = pagePerThread*threadCount+1;
		threadList[threadCount] = new GameRankPageThread(httpHelper,game,startPage,modresult+startPage);
		
		for(int i=0;i<threadList.length;i++){
			threadList[i].start();
		}
		
		for(int i=0;i<threadList.length;i++){
			threadList[i].join();
		}
		
		//Update accounts info
		for(int i=0;i<threadList.length;i++){
			accountList = threadList[i].getAccountList();
			createAccounts(accountList);
		}
		
		//Update 
		for(int i=0;i<threadList.length;i++){
			tempGameStatusSnapshotList = threadList[i].getTempGameStatusSnapshotList();
			createTempGameStatusSnapshot(tempGameStatusSnapshotList);
		}
	}

	public void createAccounts(List<Account> accountList) {
		List<Account> newAccountList = new ArrayList<Account>();
		List<TempGameStatusSnapshot> tempGameStatusSnapshotList = new ArrayList<TempGameStatusSnapshot>();
		for (Account account : accountList) {
			Account checkedAccount = accountDao.getAccountByAccountKey(account.getAccountKey());
			if (checkedAccount == null) {
				newAccountList.add(account);
				if (newAccountList.size() > 200) {
					accountDao.createAccounts(newAccountList);
					newAccountList.clear();
				}
			} else
				accountDao.updateAccount(account);
		}
		
		if (newAccountList.size() > 0)
			accountDao.createAccounts(newAccountList);		
	}
	
	public void createTempGameStatusSnapshot(List<TempGameStatusSnapshot> tempGameStatusSnapshotList){
		List<TempGameStatusSnapshot> newTempGameStatusSnapshotList = new ArrayList<TempGameStatusSnapshot>();
		for(TempGameStatusSnapshot tempGameStatusSnapshot : tempGameStatusSnapshotList){
			newTempGameStatusSnapshotList.add(tempGameStatusSnapshot);
			if(newTempGameStatusSnapshotList.size()>200){
				gameStatusSnapshotDao.createTempGameStatusSnapshots(newTempGameStatusSnapshotList);
				newTempGameStatusSnapshotList.clear();
			}
		}
		if(newTempGameStatusSnapshotList.size()>0){
			gameStatusSnapshotDao.createTempGameStatusSnapshots(newTempGameStatusSnapshotList);
		}
	}

	public List<Account> getAllAccounts() {
		return accountDao.getAllAccounts();
	}

	public Account getAccountByAccountKey(int accountKey) {
		return accountDao.getAccountByAccountKey(accountKey);
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

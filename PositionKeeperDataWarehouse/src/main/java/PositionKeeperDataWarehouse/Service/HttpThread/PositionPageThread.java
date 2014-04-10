package PositionKeeperDataWarehouse.Service.HttpThread;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import PositionKeeperDataWarehouse.Dao.IAccountDao;
import PositionKeeperDataWarehouse.Entity.Account;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Entity.GameStatusSnapshot;
import PositionKeeperDataWarehouse.Entity.TempGameStatusSnapshot;
import PositionKeeperDataWarehouse.Helper.HttpHelper;

public class PositionPageThread extends Thread {
	public static Logger logger = LogManager.getLogger(TradeHistoryPageThread.class.getName());
	private HttpHelper httpHelper;
	private Game game;
	private int start;
	private int end;
	private List<TempGameStatusSnapshot> tempGameStatusSnapshotList;
	private List<GameStatusSnapshot> gameStatusSnapshotList = new ArrayList<GameStatusSnapshot>();
	private IAccountDao accountDao;
	private List<Account> accountList = new ArrayList<Account>();
	public PositionPageThread(HttpHelper httpHelper,Game game, int start, int end,List<TempGameStatusSnapshot> tempGameStatusSnapshotList,IAccountDao accountDao){
		this.httpHelper = httpHelper;
		this.start = start;
		this.end = end;
		this.game = game;
		this.tempGameStatusSnapshotList = tempGameStatusSnapshotList;
		this.accountDao = accountDao;
	}
	
	@Override
	public void run() {
		for(int i = start; i<end; i++){
			TempGameStatusSnapshot tempGameStatusSnapshot = tempGameStatusSnapshotList.get(i);
			int accountKey = tempGameStatusSnapshot.getAccountKey();
			String url = "http://www.investopedia.com/simulator/ranking/viewportfolio.aspx?UserID=" + accountKey + "&GameID=" + game.getGameKey();
			String html;
			try {
				html = httpHelper.getHtml(url);
				logger.info("Processing Position Detail Game: " + game.getGameKey() + " User: "
						+ accountKey);
				updateAccountDetail(html, accountKey);
				createGameStatusSnapshot(html,tempGameStatusSnapshot);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	
	public void updateAccountDetail(String html, int accountKey){
		Account account = accountDao.getAccountByAccountKey(accountKey);
		Document doc = Jsoup.parse(html);
		// Member Since
		Element memberSince = doc.select("span[id=spSince]").first();
		if(memberSince == null)
			return;
		account.setMemberSince(memberSince.ownText().trim());

		// Experience
		Element experience = doc.select("span[id=spExperience]").first();
		account.setExperience(experience.ownText().trim());

		// PrimaryInvestingStyle
		Element primaryInvestingStyle = doc.select("span[id=spInvStyle]").first();
		account.setPrimaryInvestingStyle(primaryInvestingStyle.ownText().trim());

		// TimeHorizon
		Element timeHorizon = doc.select("span[id=spTimeHorizon]").first();
		account.setTimeHorizon(timeHorizon.ownText().trim());
		accountList.add(account);
	}
	
	public void createGameStatusSnapshot(String html, TempGameStatusSnapshot tempGameStatusSnapshot){
		Document doc = Jsoup.parse(html);
		GameStatusSnapshot gameStatusSnapshot = new GameStatusSnapshot();
		//AccountKey
		gameStatusSnapshot.setAccountKey(tempGameStatusSnapshot.getAccountKey());
		//DataLoadLogKey
		gameStatusSnapshot.setDataLoadLogKey(game.getLatestDataLoadLog().getDataLoadLogKey());
		//Rank
		Element rank = doc.select("span[id=arrowRank]").first();
		if(rank==null)
			return;
		gameStatusSnapshot.setRank(Integer.valueOf(rank.text()));
		//AccountValue
		Element accountValue = doc.select("span[id=arrowAccount]").first();
		if(accountValue!=null){
			gameStatusSnapshot.setAccountValue(new BigDecimal(accountValue.text().replaceAll(",|\\$|\\s", "")));
		}
		else{
			gameStatusSnapshot.setAccountValue(new BigDecimal(-999999));
		}
		//BuyingPower
		Element buyingPower = doc.select("span[id=ctl00_MainPlaceHolder_currencyFilter_ctrlPortfolioDetails_PortfolioSummary_lblBuyingPower]").first();
		if(buyingPower!=null){
			gameStatusSnapshot.setBuyingPower(new BigDecimal(buyingPower.text().replaceAll(",|\\$|\\s", "")));
		}
		else{
			gameStatusSnapshot.setBuyingPower(new BigDecimal(-999999));
		}
		//Cash
		Element cash = doc.select("span[id=ctl00_MainPlaceHolder_currencyFilter_ctrlPortfolioDetails_PortfolioSummary_lblCash]").first();
		if(cash!=null){
			gameStatusSnapshot.setCash(new BigDecimal(cash.text().replaceAll(",|\\$|\\s", "")));
		}
		else{
			gameStatusSnapshot.setCash(new BigDecimal(-999999));
		}
		//Profit
		gameStatusSnapshot.setProfit(tempGameStatusSnapshot.getProfit());
		
		getGameStatusSnapshotList().add(gameStatusSnapshot);
	}
	
	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	public List<GameStatusSnapshot> getGameStatusSnapshotList() {
		return gameStatusSnapshotList;
	}

	public void setGameStatusSnapshotList(List<GameStatusSnapshot> gameStatusSnapshotList) {
		this.gameStatusSnapshotList = gameStatusSnapshotList;
	}
}

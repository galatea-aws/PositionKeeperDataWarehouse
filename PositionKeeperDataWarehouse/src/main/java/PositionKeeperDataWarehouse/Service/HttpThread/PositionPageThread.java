package PositionKeeperDataWarehouse.Service.HttpThread;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import PositionKeeperDataWarehouse.Dao.IAccountDao;
import PositionKeeperDataWarehouse.Entity.Account;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Entity.GameStatusSnapshot;
import PositionKeeperDataWarehouse.Entity.PositionDetail;
import PositionKeeperDataWarehouse.Entity.Product;
import PositionKeeperDataWarehouse.Entity.TempGameStatusSnapshot;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Service.Interface.IAccountService;
import PositionKeeperDataWarehouse.Service.Interface.IProductService;

public class PositionPageThread extends Thread {
	public static Logger logger = LogManager.getLogger(TradeHistoryPageThread.class.getName());
	private HttpHelper httpHelper;
	private Game game;
	private int start;
	private int end;
	private List<TempGameStatusSnapshot> tempGameStatusSnapshotList;
	private List<GameStatusSnapshot> gameStatusSnapshotList = new ArrayList<GameStatusSnapshot>();
	private List<PositionDetail> positionDetailList = new ArrayList<PositionDetail>();
	private List<Account> accountList = new ArrayList<Account>();
	private List<String> symbolList = new ArrayList<String>();
	private IAccountService accountService;
	private IProductService productService;
	public PositionPageThread(HttpHelper httpHelper,Game game, int start, int end,List<TempGameStatusSnapshot> tempGameStatusSnapshotList,
			IAccountService accountService, IProductService productService){
		this.httpHelper = httpHelper;
		this.start = start;
		this.end = end;
		this.game = game;
		this.tempGameStatusSnapshotList = tempGameStatusSnapshotList;
		this.accountService = accountService;
		this.productService = productService;
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
				logger.info("PositionDetail Game: " + game.getGameKey() + " User: "
						+ accountKey);
				updateAccountDetail(html, accountKey);
				createGameStatusSnapshot(html,tempGameStatusSnapshot);
				createPositionDetailSnapshot(html,accountKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	
	public void updateAccountDetail(String html, int accountKey){
		Account account = accountService.getAccountByAccountKey(accountKey);
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
		if(rank.text().equals("\"N/A\"")||rank.text().equals("N/A"))
			gameStatusSnapshot.setRank(-1);
		else
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
	
	public void createPositionDetailSnapshot(String html, int accountKey){
		Document doc = Jsoup.parse(html);
		Elements tables = doc.select("table");
		if(tables==null||tables.size()<5)
			return;

			Element stockPositionTable = tables.get(2);
			parsePositionDetail(stockPositionTable,accountKey,"Long");
			Element optionPositionTable = tables.get(3);
			parsePositionDetail(optionPositionTable,accountKey,"Long");
			Element shortShockPositionTable = tables.get(4);
			parsePositionDetail(shortShockPositionTable,accountKey,"Short");
		
	}
	
	public void parsePositionDetail(Element table, int accountKey,
			String direction) {
		Elements rows = table.select("tr");
		rows.remove(0);
		for (Element row : rows) {
			String id = row.id();
			//Skip SPS_LONG_2_HP
			Pattern idPattern = Pattern.compile(".*HP");
			Matcher mathcer = idPattern.matcher(id);
			if (mathcer.matches())
				continue;
			
			//Check SPS_LONG_2
			idPattern = Pattern.compile(".*LONG.*");
			mathcer = idPattern.matcher(id);
			if (!mathcer.matches())
				continue;
			
			Iterator<Element> detail = row.select("td").iterator();
			PositionDetail positionDetail = new PositionDetail();
			// AccountKey
			positionDetail.setAccountKey(accountKey);
			// DataLoadLogKey
			positionDetail.setDataLoadLogKey(game.getLatestDataLoadLog()
					.getDataLoadLogKey());
			// TogglePanel
			detail.next();
			// Operation
			detail.next();
			// Symbol
			String symbol = detail.next().text();
			positionDetail.setProductSymbol(symbol);
			symbolList.add(symbol);
			// Description
			detail.next();
			// Quantity
			String quantity = detail.next().text().replaceAll(",|\\$|\\s", "");
			positionDetail.setQuantity(new BigInteger(quantity));
			// PurchasePrice
			String purchasePrice = detail.next().text()
					.replaceAll(",|\\$|\\s", "");
			positionDetail.setPurchasePrice(new Double(purchasePrice));
			// CurrentPrice
			String currentPrice = detail.next().text()
					.replaceAll(",|\\$|\\s", "");
			positionDetail.setCurrentPrice(new Double(currentPrice));
			// TotalValue
			String totalValue = detail.next().text()
					.replaceAll(",|\\$|\\s|\\(.*\\)", "");
			positionDetail.setTotalValue(new BigDecimal(totalValue));
			// Today's Change
			detail.next();
			// GainLoss
			String gainLoss = detail.next().text()
					.replaceAll(",|\\$|\\s|\\(.*\\)", "");
			positionDetail.setGainLoss(new BigDecimal(gainLoss));
			// Direction
			positionDetail.setDirection(direction);
			positionDetail.setQuantityDelta(BigInteger.ZERO);
			if(positionDetail.getGainLoss().compareTo(BigDecimal.ZERO)==0
					&&(positionDetail.getCurrentPrice()!=positionDetail.getPurchasePrice())){
				System.out.println(row.html());
			}
			positionDetailList.add(positionDetail);

		}
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

	public List<PositionDetail> getPositionDetailList() {
		return positionDetailList;
	}

	public void setPositionDetailList(List<PositionDetail> positionDetailList) {
		this.positionDetailList = positionDetailList;
	}

	public List<String> getSymbolList() {
		return symbolList;
	}

	public void setSymbolList(List<String> symbolList) {
		this.symbolList = symbolList;
	}
}

package PositionKeeperDataWarehouse.Service.HttpThread;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import PositionKeeperDataWarehouse.Dao.ITradeHistoryDao;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Entity.Option;
import PositionKeeperDataWarehouse.Entity.Product;
import PositionKeeperDataWarehouse.Entity.TempGameStatusSnapshot;
import PositionKeeperDataWarehouse.Entity.TradeHistory;
import PositionKeeperDataWarehouse.Helper.HtmlHelper;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Helper.OptionParser;
import PositionKeeperDataWarehouse.Helper.TransactionParser;
import PositionKeeperDataWarehouse.Service.AccountServiceImpl;

public class TradeHistoryPageThread extends Thread {
	public static Logger logger = LogManager.getLogger(TradeHistoryPageThread.class.getName());
	private HttpHelper httpHelper;
	private ITradeHistoryDao tradeHistoryDao;
	private Game game;
	private int start;
	private int end;
	private List<TempGameStatusSnapshot> tempGameStatusSnapshotList;
	private List<String> symbolList = new ArrayList<String>();
	private List<TradeHistory> tradeHistory = new ArrayList<TradeHistory>();
	public TradeHistoryPageThread(HttpHelper httpHelper, Game game, int start, int end,List<TempGameStatusSnapshot> tempGameStatusSnapshotList,ITradeHistoryDao tradeHistoryDao) {
		this.httpHelper = httpHelper;
		this.game = game;
		this.start = start;
		this.end = end;
		this.tempGameStatusSnapshotList = tempGameStatusSnapshotList;
		this.tradeHistoryDao = tradeHistoryDao;
	}

	@Override
	public void run() {
		for(int i = start; i<end; i++){
			
			int accountKey = tempGameStatusSnapshotList.get(i).getAccountKey();
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("accountKey", accountKey);
			map.put("gameKey", game.getGameKey());
			TradeHistory latestHistory = tradeHistoryDao.getLatestTradeHistoryByAccountGame(map);
			
			String url = "http://www.investopedia.com/simulator/trade/tradeoverview.aspx?UserID=" + accountKey + "&GameID=" + game.getGameKey();
			String html;
			try {
				html = httpHelper.getHtml(url);
				int pageCount = HtmlHelper.getPageCount(html);
				for(int j=1;j<=pageCount;j++){
					logger.info("Processing Game: " + game.getGameKey() + " User: "
							+ accountKey + " Page:" + j);
					String detailUrl = url + "&page=" + j;
					String detailHtml = httpHelper.getHtml(detailUrl);
					List<TradeHistory> tradeHistoryOnPage = convertToTradeHistories(detailHtml,accountKey);
					
					if(!needCheckNextPage(tradeHistoryOnPage,latestHistory)){
						break;
					}
					else{
						getTradeHistory().addAll(getNewTradeHistories(tradeHistoryOnPage,latestHistory));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<TradeHistory> convertToTradeHistories(String html, int accountKey) throws ParseException{
		List<TradeHistory> tradeHistoryList = new ArrayList<TradeHistory>();
		Document doc = Jsoup.parse(html);
		Element table = doc.getElementById("gvTradeHistory");
		if(table==null)
			return tradeHistoryList;
		Elements rows = table.select("tr");
		rows.remove(0);
		for(Element row : rows){
			Iterator<Element> detail = row.select("td").iterator();
			TradeHistory tradeHistory = new TradeHistory();
			//Trade Date
			String tradeDate = detail.next().text();
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			tradeHistory.setTradeDate(dateFormat.parse(tradeDate));
			//Trade Type Order Type
			String transactionType = detail.next().text();
			TransactionParser.parseTradeTypeOrderType(tradeHistory, transactionType);
			//Symbol
			String symbol = detail.next().text();
			symbol = symbol.replaceAll("\\(.*\\)", "").trim();
			tradeHistory.setProductSymbol(symbol);
			symbolList.add(symbol);
			//Quantity
			String quantity = detail.next().text();
			tradeHistory.setQuantity(new Long(quantity));
			//Target Price
			detail.next();
			//Price
			String price = detail.next().text().replaceAll(",|\\$|\\s","");
			if(price.equals("")){
				price = "-1";
			}
			tradeHistory.setPrice(new Double(price));
			//Commission
			String commission = detail.next().text();
			if(!commission.equals(""))
				commission = commission.replace(",", "").replaceAll(",|\\$|\\s","");
			else 
				commission = "-1";
			tradeHistory.setCommission(new BigDecimal(commission));
			//TotalCashValue
			String totalCashValue = detail.next().text();
			String oritotalCashValue = totalCashValue;
			if(!totalCashValue.equals(""))
				totalCashValue= totalCashValue.replaceAll(",|\\$|\\s","");
			else
				totalCashValue = "-1";
			tradeHistory.setTotalCashValue(new BigDecimal(totalCashValue));
			//DataLoadLogKey
			tradeHistory.setDataLoadLogKey(game.getLatestDataLoadLog().getDataLoadLogKey());
			//AccountKey
			tradeHistory.setAccountKey(accountKey);
			tradeHistoryList.add(tradeHistory);
		}
		return tradeHistoryList;
	}
	
	public List<TradeHistory> getNewTradeHistories(List<TradeHistory> tradeHistoryOnPage, TradeHistory latestHistory){
		if(latestHistory==null)
			return tradeHistoryOnPage;
		List<TradeHistory> newTradeHistories = new ArrayList<TradeHistory>();
		for(TradeHistory tradeHistory : tradeHistoryOnPage){
			if(tradeHistory.getTradeDate().after(latestHistory.getTradeDate()))
				newTradeHistories.add(tradeHistory);
		}
		return newTradeHistories;
	}
	
	public boolean needCheckNextPage(List<TradeHistory> tradeHistoryOnPage, TradeHistory latestHistory){
		if(latestHistory==null)
			return true;
		for(TradeHistory tradeHistory : tradeHistoryOnPage){
			if(tradeHistory.getTradeDate().before(latestHistory.getTradeDate()))
				return false;
		}
		return true;
	}

	public List<String> getSymbolList() {
		return symbolList;
	}

	public List<TradeHistory> getTradeHistory() {
		return tradeHistory;
	}
}

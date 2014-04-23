package PositionKeeperDataWarehouse.Service;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.transaction.Transaction;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import PositionKeeperDataWarehouse.Dao.IGameStatusSnapshotDao;
import PositionKeeperDataWarehouse.Dao.ITradeHistoryDao;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Entity.Option;
import PositionKeeperDataWarehouse.Entity.TempGameStatusSnapshot;
import PositionKeeperDataWarehouse.Entity.TradeHistory;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Helper.OptionParser;
import PositionKeeperDataWarehouse.Helper.TransactionParser;
import PositionKeeperDataWarehouse.Service.Interface.IGameService;

public class TradeHistoryServiceImplTest {

	TradeHistoryServiceImpl tradeHistoryService;
	IGameService gameService;
	String tradeHistoryPage = "";
	ITradeHistoryDao sqlTradeHistoryDao;
    @Before
    public void setUp() throws IOException {
/*		ConfigurableApplicationContext context = 
				new ClassPathXmlApplicationContext("App.xml");
		tradeHistoryService = (TradeHistoryServiceImpl)context.getBean("tradeHistoryService");
		gameService = (GameServiceImpl)context.getBean("gameService");
		sqlTradeHistoryDao = (ITradeHistoryDao)context.getBean("tradeHistoryDao");
		InputStream inputStream = new FileInputStream(new File("testpage.htm"));
		InputStreamReader inputReader = new InputStreamReader(inputStream);
		BufferedReader bufferReader = new BufferedReader(inputReader);
		
		String line = null;
		StringBuffer strBuffer = new StringBuffer();

		while ((line = bufferReader.readLine()) != null) {
			strBuffer.append(line);
		}
		
		tradeHistoryPage = strBuffer.toString();*/
    }
 
    @After
    public void tearDown() {
    }
    
	public void testUpdateTradeHistory() throws Exception {
		HttpHelper httpHelper = EasyMock.createMock(HttpHelper.class);
		String url = "http://www.investopedia.com/simulator/trade/tradeoverview.aspx?UserID=1&GameID=1";
		String detailUrl = url + "&page=1";
		EasyMock.expect(httpHelper.getHtml(url)).andReturn(tradeHistoryPage);
		EasyMock.expect(httpHelper.getHtml(detailUrl)).andReturn(tradeHistoryPage);
		EasyMock.replay(httpHelper);
		tradeHistoryService.setHttpHelper(httpHelper);
		
		List<TempGameStatusSnapshot> tempGameStatusSnapshotList = new ArrayList<TempGameStatusSnapshot>();
		TempGameStatusSnapshot tempGameStatusSnapshot = new TempGameStatusSnapshot();
		tempGameStatusSnapshot.setGameKey(1);
		tempGameStatusSnapshot.setAccountKey(1);
		tempGameStatusSnapshotList.add(tempGameStatusSnapshot);
		
		IGameStatusSnapshotDao gameStatusSnapshotDao = EasyMock.createMock(IGameStatusSnapshotDao.class);
		EasyMock.expect(gameStatusSnapshotDao.getTempGameStatusSnapshotByGameKey(1)).andReturn(tempGameStatusSnapshotList);
		EasyMock.replay(gameStatusSnapshotDao);
		tradeHistoryService.setGameStatusSnapshotDao(gameStatusSnapshotDao);
		
		List<Game> gameList = new ArrayList<Game>();
		Game game = new Game();
		game.setGameKey(1);
		gameList.add(game);
		try {
			tradeHistoryService.updateTradeHistory(gameList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testUpdateProduct(){;
	}
	
	public void testInsertTradeHistory(){
		List<TradeHistory> tradeHistoryList = new ArrayList<TradeHistory>();
		TradeHistory tradeHisotry = new TradeHistory();
		tradeHisotry.setAccountKey(1);
		tradeHisotry.setDataLoadLogKey(1);
		tradeHisotry.setPrice(16d);
		tradeHisotry.setQuantity(10000l);
		tradeHisotry.setTotalCashValue(new BigDecimal(1000000));
		tradeHisotry.setCommission(new BigDecimal(1000000));
		tradeHisotry.setOrderType("Limit");
		tradeHisotry.setTradeType("Buy");
		tradeHisotry.setProductKey(1);
		tradeHisotry.setTradeDate(new Date());
		tradeHistoryList.add(tradeHisotry);
		sqlTradeHistoryDao.createTradeHistories(tradeHistoryList);
	}
	
	@Test
	public void testGetLatestTradeHistoryByAccountGame(){
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("accountKey", 1);
		map.put("gameKey", 1);
	}
	
	@Test
	public void testOptionParser(){
		Option option = OptionParser.convertToOption("AAPL1417E23");
		System.out.println(option.getSymbol());
		String symbol = "AAPL1417E23(111)";
		symbol = symbol.replaceAll("\\(.*\\)", "").trim();
		System.out.println(symbol);
	}
	
	@Test
	public void testBigDecimal(){
		String p = "- $297.90(-5.25 %)";
		String gainLoss = p.replaceAll(",|\\$|\\s|\\(.*\\)", "");
		System.out.println(new BigDecimal(gainLoss));
	}
	
	@Test
	public void testTransactionParser(){
		TradeHistory tradeHistory = new TradeHistory();
		String transactionType = "Cover Stock: Cover at Limit";
		TransactionParser.parseTradeTypeOrderType(tradeHistory, transactionType);
		assertEquals("Cover Stock",tradeHistory.getTradeType());
		assertEquals("Limit",tradeHistory.getOrderType());
		
		transactionType = "Cover Stock: Cover at Market";
		TransactionParser.parseTradeTypeOrderType(tradeHistory, transactionType);
		assertEquals("Cover Stock",tradeHistory.getTradeType());
		assertEquals("Market",tradeHistory.getOrderType());
		
		transactionType = "Cover Stock: Cover at Market Open";
		TransactionParser.parseTradeTypeOrderType(tradeHistory, transactionType);
		assertEquals("Cover Stock",tradeHistory.getTradeType());
		assertEquals("Market Open",tradeHistory.getOrderType());
		
		transactionType = "Cover Stock: Cover at Stop";
		TransactionParser.parseTradeTypeOrderType(tradeHistory, transactionType);
		assertEquals("Cover Stock",tradeHistory.getTradeType());
		assertEquals("Stop",tradeHistory.getOrderType());
		
		transactionType = "Stock: Cover at Trailing Stop";
		TransactionParser.parseTradeTypeOrderType(tradeHistory, transactionType);
		assertEquals("Cover Stock",tradeHistory.getTradeType());
		assertEquals("Trailing Stop",tradeHistory.getOrderType());
		
		transactionType = "Portfolio Reset";
		TransactionParser.parseTradeTypeOrderType(tradeHistory, transactionType);
		assertEquals("Portfolio Reset",tradeHistory.getTradeType());
		assertEquals("",tradeHistory.getOrderType());
		
		transactionType = "Stock: Split Adjustment Buy";
		TransactionParser.parseTradeTypeOrderType(tradeHistory, transactionType);
		assertEquals("Stock: Split Adjustment Buy",tradeHistory.getTradeType());
		assertEquals("",tradeHistory.getOrderType());
	}

}

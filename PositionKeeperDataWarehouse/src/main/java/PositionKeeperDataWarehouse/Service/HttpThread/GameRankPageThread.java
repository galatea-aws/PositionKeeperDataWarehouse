package PositionKeeperDataWarehouse.Service.HttpThread;

import java.math.BigDecimal;
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

import PositionKeeperDataWarehouse.Entity.Account;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Entity.TempGameStatusSnapshot;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Service.AccountServiceImpl;

public class GameRankPageThread extends Thread {
	public static Logger logger = LogManager.getLogger(GameRankPageThread.class.getName());
	private HttpHelper httpHelper;
	private List<Account> accountList = new ArrayList<Account>();
	private List<TempGameStatusSnapshot> tempGameStatusSnapshotList = new ArrayList<TempGameStatusSnapshot>();
	private Game game;
	private int startPage;
	private int endPage;
	public GameRankPageThread(HttpHelper httpHelper, Game game, int startPage, int endPage) {
		this.httpHelper = httpHelper;
		this.game = game;
		this.startPage = startPage;
		this.endPage = endPage;
	}

	@Override
	public void run() {
		for(int i = startPage; i<=endPage; i++){
			String url = "http://www.investopedia.com/simulator/ranking/?RGID="
					+ game.getGameKey() + "&page=" + String.valueOf(i);
			String html;
			try {
				long start = System.currentTimeMillis();
				html = httpHelper.getHtml(url);
				long end = System.currentTimeMillis();
				logger.info("Processing Game: " + game.getGameKey() + " Page: "
						+ i + " Duration: " + (end-start));
				convertTableToAccounts(html);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public void convertTableToAccounts(String html)
			throws Exception {
		Document doc = Jsoup.parse(html);
		Elements rows = doc.select("tr[class=table_data]");
		for (Element element : rows) {
			Account account = new Account();

			// Account Name
			String accountName = element.select("a").first().text();
			account.setAccountName(accountName);
			if (accountName.equals("User not found"))
				continue;
			// User Id
			String link = element.select("a[href]").first().attr("abs:href");
			String pattern = ".*?UserID=(\\d+)";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(link);
			if (m.find()) {
				String userId = m.group(1);
				account.setAccountKey(Integer.parseInt(userId));
			} else
				continue;

			// Overall%
			String overall = element.select("span").get(4).text().replace("%", "").replace("+", "");
			if (overall.equals("0.00"))
				continue;
			account.setMemberSince("");
			account.setExperience("");
			account.setPrimaryInvestingStyle("");
			account.setTimeHorizon("");
			accountList.add(account);
			
			TempGameStatusSnapshot tempGameStatusSnapshot = new TempGameStatusSnapshot();
			tempGameStatusSnapshot.setGameKey(game.getGameKey());
			tempGameStatusSnapshot.setAccountKey(account.getAccountKey());
			tempGameStatusSnapshot.setProfit(new BigDecimal(overall));
			tempGameStatusSnapshotList.add(tempGameStatusSnapshot);
		}
	}

	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	public List<TempGameStatusSnapshot> getTempGameStatusSnapshotList() {
		return tempGameStatusSnapshotList;
	}

	public void setTempGameStatusSnapshotList(
			List<TempGameStatusSnapshot> tempGameStatusSnapshotList) {
		this.tempGameStatusSnapshotList = tempGameStatusSnapshotList;
	}
}
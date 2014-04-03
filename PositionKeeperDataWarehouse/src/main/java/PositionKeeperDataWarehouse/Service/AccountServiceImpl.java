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
import PositionKeeperDataWarehouse.Entity.Account;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Helper.HttpHelper;

public class AccountServiceImpl implements IAccountService {

	public static Logger logger = LogManager.getLogger(AccountServiceImpl.class
			.getName());
	private IAccountDao accountDao;
	private HttpHelper httpHelper;

	public void updateAccount(List<Game> gameList) throws Exception {
		for (Game game : gameList) {
			updateAccount(game);
		}
	}

	public void updateAccount(Game game) throws Exception {
		List<Account> accountList = new ArrayList<Account>();
		int pageCount = getPageCount(game.getGameId());

		GetGameRankPageThread[] threadList = new GetGameRankPageThread[10];
		int pagePerThread = pageCount/10;
		for(int i=0;i<threadList.length;i++){
			int startPage = 1 + pagePerThread *i;
			int endPage = Math.min(startPage+pagePerThread-1,pageCount);
			threadList[i] = new GetGameRankPageThread(httpHelper,game.getGameId(),startPage,endPage);
		}
		
		for(int i=0;i<threadList.length;i++){
			threadList[i].start();
		}
		
		for(int i=0;i<threadList.length;i++){
			threadList[i].join();
		}
		
		for(int i=0;i<threadList.length;i++){
			accountList = new ArrayList<Account>();
			
			List<String> pageList = threadList[i].getPageList();
			for (int j = 0; j < pagePerThread; j++) {
				accountList.addAll(convertTableToAccounts(pageList.get(j)));
			}
			pageList.clear();
			createAccounts(accountList);
			game.getAccountList().addAll(accountList);
		}
	}

	public void createAccounts(List<Account> accountList) {
		List<Account> newAccountList = new ArrayList<Account>();
		for (Account account : accountList) {
			Account checkedAccount = accountDao.getAccountByUserId(account.getUserId());
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

	public int getPageCount(String gameId) throws Exception {
		String url = "http://www.investopedia.com/simulator/ranking/?RGID="
				+ gameId;
		String html = httpHelper.getHtml(url);
		Document doc = Jsoup.parse(html);
		Element pageCountNode = doc.select("td[class=PagerInfoCell]").first();
		if (pageCountNode == null)
			return 1;
		String pageCountText = pageCountNode.text().trim();
		String pattern = ".*of\\s(\\d+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(pageCountText);
		if (m.find()) {
			String pageCount = m.group(1).trim();
			return Integer.valueOf(pageCount);
		}
		return -1;
	}

	public List<Account> convertTableToAccounts(String html)
			throws Exception {
		List<Account> accountsOnPage = new ArrayList<Account>();
		
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
				account.setUserId(userId);
			} else
				continue;

			// Overall%
			String overall = element.select("span").get(4).text();
			if (overall.equals("+0.00%"))
				continue;
			account.setMemberSince("");
			account.setExperience("");
			account.setPrimaryInvestingStyle("");
			account.setTimeHorizon("");
			/*
			 * //Account Detail html = httpHelper.getHtml(link); doc =
			 * Jsoup.parse(html);
			 * 
			 * //Member Since String memberSince =
			 * doc.select("span[id=spSince]").first().ownText().trim();
			 * account.setMemberSince(memberSince);
			 * 
			 * //Experience String experience =
			 * doc.select("span[id=spExperience]").first().ownText().trim();
			 * account.setExperience(experience);
			 * 
			 * //PrimaryInvestingStyle String primaryInvestingStyle =
			 * doc.select("span[id=spInvStyle]").first().ownText().trim();
			 * account.setPrimaryInvestingStyle(primaryInvestingStyle);
			 * 
			 * //TimeHorizon String timeHorizon =
			 * doc.select("span[id=spTimeHorizon]").first().ownText().trim();
			 * account.setTimeHorizon(timeHorizon);
			 */

			accountsOnPage.add(account);
		}
		return accountsOnPage;
	}

	public List<Account> getAllAccounts() {
		return accountDao.getAllAccounts();
	}

	public Account getAccountByName(String userId) {
		return accountDao.getAccountByUserId(userId);
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

	public class GetGameRankPageThread extends Thread {
		private HttpHelper httpHelper;
		private List<String> pageList = new ArrayList<String>();
		private String gameId;
		private int startPage;
		private int endPage;
		public GetGameRankPageThread(HttpHelper httpHelper, String gameId, int startPage, int endPage) {
			this.httpHelper = httpHelper;
			this.gameId = gameId;
			this.startPage = startPage;
			this.endPage = endPage;
		}

		/**
		 * Executes the GetMethod and prints some status information.
		 */
		@Override
		public void run() {
			for(int i = startPage; i<=endPage; i++){
				String url = "http://www.investopedia.com/simulator/ranking/?RGID="
						+ gameId + "&page=" + String.valueOf(i);
				String html;
				logger.info("Processing Game: " + gameId + " Page: "
						+ i);
				try {
					html = httpHelper.getHtml(url);
					getPageList().add(html);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		public List<String> getPageList() {
			return pageList;
		}

		public void setPageList(List<String> pageList) {
			this.pageList = pageList;
		}

	}

}

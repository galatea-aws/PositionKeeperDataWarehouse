package PositionKeeperDataWarehouse.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import PositionKeeperDataWarehouse.Dao.IGameDao;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Helper.HttpHelper;

public class GameServiceImpl implements IGameService {
	private IGameDao gameDao;
	private HttpHelper httpHelper;

	public void updateGameInfo() throws Exception{
		List<Game> gameList = gameDao.getAllGames();
		if(gameList.size()>0)
			return;
		else
			createGames();
	}
	
	public void createGames() throws Exception {
		ArrayList<Game> gameList = convertTableToGames();
		for(Game game: gameList){
			gameDao.createGame(game);
		}
	}

	public ArrayList<Game> convertTableToGames() throws Exception {
		ArrayList<Game> gameList = new ArrayList<Game>();
		String url = "http://www.investopedia.com/simulator/game/listgames.aspx?type=Active";
		String html = httpHelper.getHtml(url);
		
		Document doc = Jsoup.parse(html);
		Elements tables = doc.select("table");
		Element gameTable = tables.get(2);
		Elements rows = gameTable.select("tr");
		
		//Create Game List
		for (int i = 1; i < rows.size(); i++) {
			Iterator<Element> ite = rows.get(i).select("td").iterator();
			Game game = new Game();
			
			// Game Name
			game.setGameName(ite.next().text().trim());
			
			// Currency
			game.setCurrency(ite.next().text().trim());
			
			// Start Date
			Element startDate = ite.next();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			try {
				game.setStartDate(formatter.parse(startDate.text().trim()));
			} catch (ParseException e) {
				game.setStartDate(new Date(0));
				e.printStackTrace();
			}
			
			// End Date
			Element endDate = ite.next();
			if (endDate.text().trim().equals("No End")) {
				game.setEndDate(new Date(0));
			} else {
				try {
					game.setEndDate(formatter.parse(endDate.text().trim()));
				} catch (ParseException e) {
					game.setEndDate(new Date(0));
					e.printStackTrace();
				}
			}
			
			// Game Players
			ite.next();
			
			// Game Id			
			Element gameId = ite.next();
			String link = gameId.select("a[href]").first().attr("abs:href");
			String pattern = ".*?RGID=(.*)";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(link);
			if (m.find()) {
				game.setGameId(m.group(1));
				if(afterYear(game.getStartDate(),2009))
					gameList.add(game);
			}
		}
		
		return gameList;
	}

	public boolean afterYear(Date datetime, int year){
		Calendar cal = Calendar.getInstance();
		cal.set(year, Calendar.DECEMBER, 31);
		return datetime.after(cal.getTime());
	}
	public List<Game> getAllGames() {
		return gameDao.getAllGames();
	}

	public IGameDao getGameDao() {
		return gameDao;
	}

	public void setGameDao(IGameDao gameDao) {
		this.gameDao = gameDao;
	}

	public HttpHelper getHttpHelper() {
		return httpHelper;
	}

	public void setHttpHelper(HttpHelper httpHelper) {
		this.httpHelper = httpHelper;
	}

}

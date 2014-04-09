package PositionKeeperDataWarehouse.Service;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import PositionKeeperDataWarehouse.Entity.DataLoadLog;
import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Service.HttpThread.TradeHistoryPageThread;

public class TradeHistoryPageThreadTest {

	@Test
	public void testConvertToTradeHistories() throws IOException, ParseException {
		Game game= new Game();
		DataLoadLog dataLoadLog = new DataLoadLog();
		dataLoadLog.setDataLoadLogKey(1);
		game.setLatestDataLoadLog(dataLoadLog);
		TradeHistoryPageThread tradeHistoryPageThread = new TradeHistoryPageThread(
				null, game, 1, 2, null,null);
		InputStreamReader inputReader = null;
		BufferedReader bufferReader = null;
		InputStream inputStream = new FileInputStream(new File("testpage.htm"));
		inputReader = new InputStreamReader(inputStream);
		bufferReader = new BufferedReader(inputReader);
		
		String line = null;
		StringBuffer strBuffer = new StringBuffer();

		while ((line = bufferReader.readLine()) != null) {
			strBuffer.append(line);
		}
		bufferReader.close();
		tradeHistoryPageThread.convertToTradeHistories(strBuffer.toString(),1);
	}
	
	@Test
	public void testDateConvert() throws ParseException{
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		Date testdate = dateFormat.parse("3/28/2014 10:54 AM");
		System.out.println(testdate);
	}

}

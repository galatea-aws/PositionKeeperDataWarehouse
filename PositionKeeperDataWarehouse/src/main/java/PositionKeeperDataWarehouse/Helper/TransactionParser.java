package PositionKeeperDataWarehouse.Helper;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import PositionKeeperDataWarehouse.Entity.TradeHistory;

public class TransactionParser {
	public static void parseTradeTypeOrderType(TradeHistory tradeHistory, String transactionType){
		//Portfolio Reset
		if(transactionType.equals("Portfolio Reset")){
			tradeHistory.setTradeType(transactionType);
			tradeHistory.setOrderType("");
			return;
		}
		
		//Split Adjustment
		Pattern splitPattern = Pattern.compile(".*Split.*");
		Matcher splitMathcer = splitPattern.matcher(transactionType);
		if(splitMathcer.matches()){
			tradeHistory.setTradeType(transactionType);
			tradeHistory.setOrderType("");
			return;
		}
		
		//Option, Stock TradeType
		HashMap<String, String> tradeTypePatternMap = new HashMap<String, String>();
		tradeTypePatternMap.put(".*Option:\\sBuy.*", "Option Buy");
		tradeTypePatternMap.put(".*Option:\\sSell.*", "Option Sell");
		tradeTypePatternMap.put(".*Stock:\\sBuy.*", "Stock Buy");
		tradeTypePatternMap.put(".*Stock:\\sSell.*", "Stock Sell");
		tradeTypePatternMap.put(".*Stock:\\sCover.*", "Cover Stock");
		tradeTypePatternMap.put(".*Cover\\sStock:.*", "Cover Stock");
		tradeTypePatternMap.put(".*Short\\sStock:.*", "Short Stock");
		
		for(Entry<String, String> entry : tradeTypePatternMap.entrySet()){
			Pattern pattern = Pattern.compile(entry.getKey());
			Matcher mathcer = pattern.matcher(transactionType);
			if(mathcer.matches()){
				tradeHistory.setTradeType(entry.getValue());
				break;
			}
		}
		
		//Option, Stock OrderType
		HashMap<String, String> orderTypePatternMap = new HashMap<String, String>();
		orderTypePatternMap.put(".*Limit$", "Limit");
		orderTypePatternMap.put(".*Market$", "Market");
		orderTypePatternMap.put(".*Market\\sOpen$", "Market Open");
		orderTypePatternMap.put(".*Trailing\\sStop$", "Trailing Stop");
		orderTypePatternMap.put(".*at\\sStop$", "Stop");
		
		for(Entry<String, String> entry : orderTypePatternMap.entrySet()){
			Pattern pattern = Pattern.compile(entry.getKey());
			Matcher mathcer = pattern.matcher(transactionType);
			if(mathcer.matches()){
				tradeHistory.setOrderType(entry.getValue());
				break;
			}
		}
	}
}

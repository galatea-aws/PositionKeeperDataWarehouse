package PositionKeeperDataWarehouse.Helper;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import PositionKeeperDataWarehouse.Entity.Option;

public class OptionParser {
	public static Option convertToOption(String symbol){
		Option option = new Option();
		option.setOption(true);
		option.setDescription("");
		String optionPattern = "([a-zA-Z]+)(\\d{2}?)(\\d{2}?)([a-zA-Z]+)(\\d+\\.\\d+|\\d+)";
		String miniOptionPattern = "([a-zA-Z]+\\d)(\\d{2}?)(\\d{2}?)([a-zA-Z]+)(\\d+\\.\\d+|\\d+)";
		Pattern pattern = Pattern.compile(optionPattern);
		Matcher m = pattern.matcher(symbol);
		Pattern pattern1 = Pattern.compile(miniOptionPattern);
		Matcher m1 = pattern1.matcher(symbol);
		boolean optionPatternFind = m.find();
		boolean miniOptionPatternFind = m1.find();
		if (optionPatternFind||miniOptionPatternFind) {

			
			if(optionPatternFind){
				//UnderlyingStockSymbol
				option.setSymbol(m.group().trim());
				String underlyingStockSymbol = m.group(1).trim();
				option.setUnderlyingStockSymbol(underlyingStockSymbol);
			}
			else{
				option.setSymbol(m1.group().trim());
				String underlyingStockSymbol = m1.group(1).trim();
				underlyingStockSymbol = underlyingStockSymbol.substring(0,underlyingStockSymbol.length()-1);
				option.setUnderlyingStockSymbol(underlyingStockSymbol);
				m = m1;
			}
			//Expiration Date & Option Type
			String year =  "20" + m.group(2).trim();
			String day =  m.group(3).trim();
			String month =  m.group(4).trim();
		    int monthIndex = month.getBytes()[0]-64;
		    if(monthIndex>13){
		    	monthIndex = monthIndex-13;
		    	option.setOptionType("Put");
		    }
		    else{
		    	option.setOptionType("Call");
		    }
		    
		    //StrikePrice
		    String strikePrice =  m.group(5).trim();
		    try {
			    option.setStrikePrice(Double.valueOf(strikePrice));
			} catch (Exception e) {
				System.out.println(symbol);
				return null;
			}
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.valueOf(year),monthIndex, Integer.valueOf(day));
			cal.set(Calendar.HOUR_OF_DAY,0);
			cal.set(Calendar.MINUTE,0);
			cal.set(Calendar.SECOND,0);
			cal.set(Calendar.MILLISECOND,0);
			option.setExpirationDate(cal.getTime());
		}
		else{
			System.out.println(symbol);
			return null;
		}
		return option;
	}
}

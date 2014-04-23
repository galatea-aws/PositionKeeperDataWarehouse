package PositionKeeperDataWarehouse.Service.HttpThread;

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

import PositionKeeperDataWarehouse.Entity.Product;
import PositionKeeperDataWarehouse.Helper.HtmlHelper;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Service.ProductServiceImpl;

public class CompanyProfilePageThread extends Thread {
	public static Logger logger = LogManager.getLogger(CompanyProfilePageThread.class.getName());
	private HttpHelper httpHelper;
	private List<Product> productList;
	private List<Product> updatedProductList = new ArrayList<Product>();
	private int start;
	private int end;
	
	public CompanyProfilePageThread(List<Product> productList,int start, int end, HttpHelper httpHelper){
		this.productList = productList;
		this.start = start;
		this.end = end;
		this.httpHelper = httpHelper;
	}
	
	@Override
	public void run() {
		for(int i = start; i < end; i++){
			try {
				Product product = productList.get(i);
				String url = "http://finance.yahoo.com/q/pr?s=" + product.getSymbol() + "+Profile";
				logger.info("Processing Product:" + product.getSymbol());
				String html = httpHelper.getHtml(url);
				getProductDetails(product,html);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.info("Done");
	}
	
	public void getProductDetails(Product product, String html){
		Document doc = Jsoup.parse(html);
		Element div = doc.getElementById("yfi_rt_quote_summary");
		if(div == null){
			product.setProductType("Others");
			product.setFullName("N/A");
			updatedProductList.add(product);
			return;
		}
		
		Pattern stockPattern = Pattern.compile("<h3>Company</h3>");
		Matcher stockMatcher = stockPattern.matcher(html);
		
		Pattern etfPattern = Pattern.compile("<h3>ETF</h3>");
		Matcher etfMatcher = etfPattern.matcher(html);
		
		if(stockMatcher.find()){
			product.setProductType("Stock");
		}
		else if (etfMatcher.find()) {
			product.setProductType("ETF");
		}
		else{
			product.setProductType("Others");
		}

		
		Element title = div.select("h2").first();
		product.setFullName(title.text());
		

		Element table = doc.select("table[class=yfnc_datamodoutline1]").first();
		if(table==null){
			product.setProductType("Others");
			updatedProductList.add(product);
			return;
		}	
		table = table.select("table").first();
		Elements details = table.select("td[class=yfnc_tabledata1]");
		Iterator<Element> iterator = details.iterator();

		try {
			if(product.getProductType().equals("Stock")){
				iterator.next().text();
				product.setSector(iterator.next().text());
				product.setIndustry(iterator.next().text());
				product.setProductType("Stock");
			}
			else if (product.getProductType().equals("ETF")) {
				product.setSector(iterator.next().text());
				product.setProductType("ETF");
			}
			else{
				product.setProductType("Others");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		updatedProductList.add(product);
	}

	public List<Product> getUpdatedProductList() {
		return updatedProductList;
	}

	public void setUpdatedProductList(List<Product> updatedProductList) {
		this.updatedProductList = updatedProductList;
	}
}

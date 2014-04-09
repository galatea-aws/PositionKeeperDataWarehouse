package PositionKeeperDataWarehouse.Helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HtmlHelper {
	public static int getPageCount(String html) throws Exception {
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
}

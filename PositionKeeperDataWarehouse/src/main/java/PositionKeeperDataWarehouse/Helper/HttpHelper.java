package PositionKeeperDataWarehouse.Helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author wsun
 * Helper class for visiting investopedia
 */
public class HttpHelper {
	private CloseableHttpClient httpclient;
	private boolean isLogin = false;
	public HttpHelper(){
		try {
			login();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Login investopedia
	 * @throws Exception
	 */
	private void login() throws Exception{
		String loginUrl = "http://www.investopedia.com/accounts/login.aspx?returnurl=http%3A%2F%2Fwww.investopedia.com%2Fsimulator%2Fhome.aspx";
		Document doc = Jsoup.connect(loginUrl).get();
		Map<String, String> requestParameters = getLoginFormParams(doc.html(),
				"sun@galatea-associates.com", "sunwei2323");

		BasicCookieStore cookieStore = new BasicCookieStore();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	    cm.setMaxTotal(100);
	    cm.setDefaultMaxPerRoute(40);
		httpclient = HttpClients.custom()
				.setDefaultCookieStore(cookieStore).setConnectionManager(cm).build();
		RequestBuilder rb = RequestBuilder.post().setUri(new URI(loginUrl));
		for (Entry<String, String> entry : requestParameters.entrySet()) {
			rb = rb.addParameter(entry.getKey(), entry.getValue());
		}
		HttpUriRequest login = rb.build();
		CloseableHttpResponse response = httpclient.execute(login);
		try {
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		setLogin(true);
	}

	private Map<String, String> getLoginFormParams(String html, String username,
			String password) throws UnsupportedEncodingException {
		Document doc = Jsoup.parse(html);

		Element loginform = doc.getElementById("account-api-form");
		Elements inputElements = loginform.getElementsByTag("input");
		Map<String, String> paramList = new HashMap<String, String>();
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");

			if (key.equals("email"))
				value = username;
			else if (key.equals("password"))
				value = password;
			paramList.put(key, value);
		}
		return paramList;
	}
	
	public String getHtml(String url) throws Exception{
		RequestBuilder rb = RequestBuilder.get().setUri(new URI(url));
		HttpUriRequest login = rb.build();
		String html = "";
		CloseableHttpResponse response = httpclient.execute(login);
		try {
			HttpEntity entity = response.getEntity();
			 html = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return html;
	}


	public boolean isLogin() {
		return isLogin;
	}


	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

}

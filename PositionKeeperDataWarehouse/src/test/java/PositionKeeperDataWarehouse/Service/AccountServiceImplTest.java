package PositionKeeperDataWarehouse.Service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import PositionKeeperDataWarehouse.App;
import PositionKeeperDataWarehouse.Dao.IAccountDao;
import PositionKeeperDataWarehouse.Entity.Account;
import PositionKeeperDataWarehouse.Helper.HtmlHelper;
import PositionKeeperDataWarehouse.Helper.HttpHelper;

public class AccountServiceImplTest {

	private AccountServiceImpl accountService;
	private IAccountDao accountDao;
	private HttpHelper httpHelper;
	
    @Before
    public void setUp() {
    	accountDao = EasyMock.createMock(IAccountDao.class);
    	httpHelper = EasyMock.createMock(HttpHelper.class);
    	accountService = new AccountServiceImpl();
    }
 
    @After
    public void tearDown() {
    }
    
/*    @Test
	public void testCreateNewAccount() {
		String userId = "5642167";
		Account account = new Account();
		account.setUserId(userId);
        EasyMock.expect(accountDao.getAccountByUserId(userId)).andReturn(null);
        accountDao.createAccount(account);
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(accountDao);
        
        accountService.setAccountDao(accountDao);
        accountService.createAccount(account);
        
		EasyMock.verify(accountDao);
	}*/
    
    @Test
    public void testCreateExistingAcount(){
/*    	String userId = "5642167";
		Account account = new Account();
		account.setUserId(userId);
		account.setAccountName("");
		account.setExperience("");
		account.setMemberSince("2009");
		account.setPrimaryInvestingStyle("");
		account.setTimeHorizon("");
        EasyMock.expect(accountDao.getAccountByUserId(userId)).andReturn(account);
        accountDao.updateAccount(account);
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(accountDao);
        
		List<Account> accountList = new ArrayList<Account>();
		accountList.add(account);
        accountService.setAccountDao(accountDao);
        accountService.createAccounts(accountList);
        
		EasyMock.verify(accountDao);*/
    }

	public void testGetAllAccounts() {
	}

	public void testGetAccountByName() {
	}
	
	@Test
	public void testGetPageCount(){
		String html = "<head></<head>><body><table><tr><td class=\"PagerInfoCell\"><strong>Page:</strong> 1 of 567&nbsp;&nbsp;&nbsp;</td></tr></table></body>";
		EasyMock.replay(httpHelper);
		
		try {
			int pageCount = HtmlHelper.getPageCount(html);
			assertEquals(567,pageCount);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception");
		}
		
		EasyMock.verify(httpHelper);
	}
	
	@Test
	public void testConvertTableToAccounts(){
/*		String gameRankUrl = "http://www.investopedia.com/simulator/ranking/?RGID=254785&page=2";
		String gameRankHtml = "<head></<head>><body><table>"
				+"	<tr class=\"table_data\">"
				+"	<td>"
				+"		<span style=\"\">7</span>."
				+"		<a style=\"\"  href=\"http://www.investopedia.com/simulator/ranking/viewportfolio.aspx?UserID=3476061&GameID=254785\">MobileVe7</a>"
				+"						<a style=\"\" href=\"http://www.investopedia.com/simulator/trade/tradeoverview.aspx?UserID=3476061&GameID=254785&Currency=USD\">(history)</a>"
				+"								<a  href=\"http://www.investopedia.com/simulator/messages/messages.aspx?nickname=MobileVe7\"><img"
				+"				src=\"http://i.investopedia.com/Research/community/message_user.png\" alt=\"Send Message\" width=\"10\""
				+"				height=\"10\" border=\"0\"></a>"
				+"		</td>"
				+"			<td align=\"center\">"
				+"		<span style=\"\">7</span></td>"
				+"	<td align=\"center\">"
				+"		<span style=\"\">$1,034,114.98</span></td>"
				+"	<td align=\"center\">"
				+"		<span style=\"color:Black;\">$0.00</span>"
				+"	</td>"
				+"	<td align=\"center\">"
				+"		<span style=\"color:Green;\">+934.11%</span>"
				+"	</td>"
				+"</tr>"
				+ "</table></body>";
		
		String accountDetailLink = "http://www.investopedia.com/simulator/ranking/viewportfolio.aspx?UserID=3476061&GameID=254785";
		String accountDetialHtml = "<head></<head>><body>"
				+"<span id=\"spSince\" style=\"width:160px;float:left;padding-top:5px;\"><strong>Member Since: </strong>2011</span><br>"
				+"<span id=\"spExperience\" style=\"width:160px;float:left;\"><strong>Experience: </strong>Beginner</span><br>"
				+"<span id=\"spInvStyle\" style=\"width:160px;float:left;\"><strong>Primary Investing Style(s): </strong>Swing Trader</span><br>"
				+"<span id=\"spTimeHorizon\" style=\"width:160px;float:left;\"><strong>Time Horizon: </strong>Short-term Trader</span><br>";
		try {
			//EasyMock.expect(httpHelper.getHtml(gameRankUrl)).andReturn(gameRankHtml);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		EasyMock.replay(httpHelper);
		
		
		accountService.setHttpHelper(httpHelper);
		try {
			List<Account> accountList = accountService.convertTableToAccounts(gameRankHtml);
			assertEquals(1,accountList.size());
			assertEquals("3476061",accountList.get(0).getUserId());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception");
		}
		
		EasyMock.verify(httpHelper);*/
	}
	
	public void testCreateAccounts(){
/*		List<Account> accountList = new ArrayList<Account>();
		for(int i = 1;i<30000;i++){
			Account account = new Account();
			account.setUserId(String.valueOf(i));
			account.setAccountName("");
			account.setExperience("");
			account.setMemberSince("2009");
			account.setPrimaryInvestingStyle("");
			account.setTimeHorizon("");
			accountList.add(account);
		}
		ConfigurableApplicationContext context = 
				new ClassPathXmlApplicationContext("App.xml");
		accountDao = (IAccountDao)context.getBean("accountDao");
        accountService.setAccountDao(accountDao);
		System.out.println(new Date());
        accountService.createAccounts(accountList);
		System.out.println(new Date());*/
	}

}

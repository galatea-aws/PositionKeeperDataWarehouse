package PositionKeeperDataWarehouse.Entity;

public class Account {
	private int accountKey;
	private String accountName;
	private String memberSince;
	private String experience;
	private String primaryInvestingStyle;
	private String timeHorizon;
	public int getAccountKey() {
		return accountKey;
	}
	public void setAccountKey(int accountKey) {
		this.accountKey = accountKey;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getMemberSince() {
		return memberSince;
	}
	public void setMemberSince(String memberSince) {
		this.memberSince = memberSince;
	}
	public String getExperience() {
		return experience;
	}
	public void setExperience(String experience) {
		this.experience = experience;
	}
	public String getPrimaryInvestingStyle() {
		return primaryInvestingStyle;
	}
	public void setPrimaryInvestingStyle(String primaryInvestingStyle) {
		this.primaryInvestingStyle = primaryInvestingStyle;
	}
	public String getTimeHorizon() {
		return timeHorizon;
	}
	public void setTimeHorizon(String timeHorizon) {
		this.timeHorizon = timeHorizon;
	}
}

package PositionKeeperDataWarehouse.Dao;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.Account;

public interface IAccountDao {
	public void createAccount(Account account);
	public void createAccounts(List<Account> accountList);
	public List<Account> getAllAccounts();
	public Account getAccountByUserId(String userId);
	public int countAccountByUserId(String userId);
	public void updateAccount(Account account);
}

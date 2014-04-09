package PositionKeeperDataWarehouse.Service.Interface;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.Account;
import PositionKeeperDataWarehouse.Entity.Game;

public interface IAccountService {
	public List<Account> getAllAccounts();
	public Account getAccountByAccountKey(int accountKey);
	public void updateAccount(List<Game> gameList) throws Exception;
	public void updateAccount(Game game) throws Exception;
}
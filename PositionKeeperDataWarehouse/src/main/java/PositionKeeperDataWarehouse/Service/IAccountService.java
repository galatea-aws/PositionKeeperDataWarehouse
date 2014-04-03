package PositionKeeperDataWarehouse.Service;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.Account;
import PositionKeeperDataWarehouse.Entity.Game;

public interface IAccountService {
	public List<Account> getAllAccounts();
	public Account getAccountByName(String accountName);
	public void updateAccount(List<Game> gameList) throws Exception;
	public void updateAccount(Game game) throws Exception;
}

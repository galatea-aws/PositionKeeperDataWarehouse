package PositionKeeperDataWarehouse.Dao;

import java.util.ArrayList;
import java.util.List;

import PositionKeeperDataWarehouse.Entity.Game;

public interface IGameDao {
	public void createGame(Game game);
	public void deleteAllGames();
	public List<Game> getAllGames();
}

package PositionKeeperDataWarehouse.Service.Interface;

import java.util.ArrayList;
import java.util.List;

import PositionKeeperDataWarehouse.Entity.Game;

public interface IGameService {
	public List<Game> getAllGames();
	public void updateGameInfo() throws Exception;
}

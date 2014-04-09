package PositionKeeperDataWarehouse.Service.Interface;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.Game;

public interface IPositionDetailService {
	public void createPositionDetailSnapshot(List<Game> gameList) throws Exception;
}

package PositionKeeperDataWarehouse.Service.Interface;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.Game;
import PositionKeeperDataWarehouse.Entity.Product;

public interface IPositionDetailService {
	public void createPositionDetailSnapshot(List<Game> gameList) throws Exception;
}

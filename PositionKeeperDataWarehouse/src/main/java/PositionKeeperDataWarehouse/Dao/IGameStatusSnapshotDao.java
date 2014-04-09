package PositionKeeperDataWarehouse.Dao;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.GameStatusSnapshot;
import PositionKeeperDataWarehouse.Entity.TempGameStatusSnapshot;

public interface IGameStatusSnapshotDao {
	public void deleteAllTempGameStatusSnapshot();
	public void createTempGameStatusSnapshots(List<TempGameStatusSnapshot> tempGameStatusSnapshotList);
	public List<TempGameStatusSnapshot> getTempGameStatusSnapshotByGameKey(int gameKey);
	public void createGameStatusSnapshots(List<GameStatusSnapshot> gameStatusSnapshotList);
}

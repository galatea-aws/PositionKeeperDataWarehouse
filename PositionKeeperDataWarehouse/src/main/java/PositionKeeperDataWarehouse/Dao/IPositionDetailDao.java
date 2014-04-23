package PositionKeeperDataWarehouse.Dao;

import PositionKeeperDataWarehouse.Entity.PositionDetail;

import java.util.List;

public interface IPositionDetailDao {
	public void createPositionDetailSnapshots(List<PositionDetail> positionDetailList);
}

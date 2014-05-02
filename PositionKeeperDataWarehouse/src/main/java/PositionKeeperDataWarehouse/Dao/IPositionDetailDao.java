package PositionKeeperDataWarehouse.Dao;

import PositionKeeperDataWarehouse.Entity.PositionDetail;

import java.util.List;
import java.util.Map;

public interface IPositionDetailDao {
	public void createPositionDetailSnapshots(List<PositionDetail> positionDetailList);
	public List<PositionDetail> getAllPositionDetailSnapshot();
	public List<PositionDetail> getPositionDetailByDataLoadLogKey(int dataLoadLogKey);
	public PositionDetail getPositionDetailByDataLoadLogKeyAccountKeyProductKey(Map<String, Object> map);
	public void updatePositionDetailSnapshot(PositionDetail positionDetail);
}

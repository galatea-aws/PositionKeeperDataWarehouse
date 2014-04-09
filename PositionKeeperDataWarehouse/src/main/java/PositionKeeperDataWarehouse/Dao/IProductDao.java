package PositionKeeperDataWarehouse.Dao;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.Option;
import PositionKeeperDataWarehouse.Entity.Product;

public interface IProductDao {
	public void createProduct(Product product);
	public void createOption(Option option);
	public Product getProductBySymbol(String symbol);
	public Option getOptionBySymbol(String symbol);
}

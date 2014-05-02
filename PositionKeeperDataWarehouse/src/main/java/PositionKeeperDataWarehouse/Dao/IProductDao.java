package PositionKeeperDataWarehouse.Dao;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.Option;
import PositionKeeperDataWarehouse.Entity.Product;

public interface IProductDao {
	public void createProduct(Product product);
	public void createOption(Option option);
	public Product getProductBySymbol(String symbol);
	public Product getProductByProductKey(int productKey);
	public Option getOptionBySymbol(String symbol);
	public List<Product> getProductsForUpdateDetails();
	public void updateProductDetails(Product product);
	public List<Product> getProductOption();
}

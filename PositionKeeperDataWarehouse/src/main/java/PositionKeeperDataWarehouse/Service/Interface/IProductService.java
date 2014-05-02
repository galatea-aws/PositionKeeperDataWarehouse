package PositionKeeperDataWarehouse.Service.Interface;

import java.util.List;

import PositionKeeperDataWarehouse.Entity.Product;

public interface IProductService {
	public void updateProduct(List<String> symbolList);
	public Product getProductBySymbol(String symbol);
	public void updateProductDetails() throws Exception;
	public void setOptionFullName();
}

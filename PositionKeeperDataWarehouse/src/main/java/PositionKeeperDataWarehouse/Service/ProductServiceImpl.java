package PositionKeeperDataWarehouse.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import PositionKeeperDataWarehouse.Dao.IProductDao;
import PositionKeeperDataWarehouse.Entity.Option;
import PositionKeeperDataWarehouse.Entity.Product;
import PositionKeeperDataWarehouse.Helper.HttpHelper;
import PositionKeeperDataWarehouse.Helper.OptionParser;
import PositionKeeperDataWarehouse.Service.HttpThread.CompanyProfilePageThread;
import PositionKeeperDataWarehouse.Service.HttpThread.GameRankPageThread;
import PositionKeeperDataWarehouse.Service.HttpThread.PositionPageThread;
import PositionKeeperDataWarehouse.Service.Interface.IProductService;

public class ProductServiceImpl implements IProductService {
	public static Logger logger = LogManager.getLogger(ProductServiceImpl.class.getName());
	private IProductDao productDao;
	private HttpHelper httpHelper;
	
	public void updateProduct(List<String> symbolList){
		Set<String> symbolSet = new HashSet<String>(symbolList);
		for(String symbol: symbolSet){
			if(symbol==null){
				System.out.println("Null product symbol");
			}
			if(symbol.length()>6){
				updateOption(symbol);
			}
			else{
				updateStock(symbol);
			}
		}
	}
	
	public Product updateStock(String symbol){
		//Product exist
		Product product = productDao.getProductBySymbol(symbol);
		if(product!=null)
			return product;
		
		//Insert product
		product = new Product();
		product.setSymbol(symbol);
		product.setOption(false);
		product.setProductType("");
		product.setFullName("");
		product.setIndustry("");
		product.setSector("");
		productDao.createProduct(product);
		return product;
	}
	
	public Option updateOption(String symbol){
		//Parse option by symbol
		Option option = OptionParser.convertToOption(symbol);
		if(option==null)
			return null;
		
		//Option exist
		Option checkedOption = productDao.getOptionBySymbol(option.getSymbol());
		if(checkedOption!=null)
			return checkedOption;
		
		String underlyingProductSymbol = option.getUnderlyingStockSymbol();
		
		//Check underlying product
		Product underlyingProduct = updateStock(underlyingProductSymbol);
		option.setUnderlyingStockKey(underlyingProduct.getProductKey());
		
		//Insert option
		Product product = (Product)option;	
		productDao.createProduct(product);
		option.setOptionKey(product.getProductKey());
		productDao.createOption(option);
		return option;
	}

	public IProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(IProductDao productDao) {
		this.productDao = productDao;
	}

	public Product getProductBySymbol(String symbol) {
		return productDao.getProductBySymbol(symbol);
	}

	public void updateProductDetails() throws Exception {
		List<Product> productList = productDao.getProductsForUpdateDetails();
		//Get product detail
		int threadCount = 20;
		CompanyProfilePageThread[] threadList = new CompanyProfilePageThread[threadCount+1];
		int pagePerThread = productList.size()/(threadCount);
		//int pagePerThread = 2;
		for(int i=0;i<threadList.length;i++){
			int start = pagePerThread *i;
			int end =  Math.min(start+pagePerThread,productList.size());
			logger.info(start + "-" + end);
			threadList[i] = new CompanyProfilePageThread(productList, start, end, httpHelper);
		}
		
		for(int i=0;i<threadList.length;i++){
			threadList[i].start();
		}
		
		for(int i=0;i<threadList.length;i++){
			threadList[i].join();
		}
		
		logger.info("Start Update");
		for(int i=0;i<threadList.length;i++){
			List<Product> updatedProductList = threadList[i].getUpdatedProductList();
			for(Product product : updatedProductList){
				productDao.updateProductDetails(product);
			}
		}
		logger.info("Update End");
		
	}

	public HttpHelper getHttpHelper() {
		return httpHelper;
	}

	public void setHttpHelper(HttpHelper httpHelper) {
		this.httpHelper = httpHelper;
	}
}

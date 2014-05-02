package PositionKeeperDataWarehouse;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import PositionKeeperDataWarehouse.Service.Interface.IPositionDetailService;
import PositionKeeperDataWarehouse.Service.Interface.IProductService;

public class ProductDetailsTest {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
				"App.xml");
		IProductService productService = (IProductService) context
				.getBean("productService");
		productService.setOptionFullName();
	}
}

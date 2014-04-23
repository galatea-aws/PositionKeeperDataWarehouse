package PositionKeeperDataWarehouse.Helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import PositionKeeperDataWarehouse.Entity.GameStatusSnapshot;

public class InsertHelper<T>{
	public void insert(List<T> toInsertList, String methodName,  Object toInvoke, int count) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		Method method = toInvoke.getClass().getMethod(methodName, new Class[]{List.class});
		List<T> newList = new ArrayList<T>();
		for(T obj : toInsertList){
			newList.add(obj);
			if(newList.size()>count){
				method.invoke(toInvoke, new Object[]{newList});
				newList.clear();
			}
		}
		if(newList.size()>0){
			method.invoke(toInvoke, new Object[]{newList});
		}
	}
}

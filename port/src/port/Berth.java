package port;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import warehouse.Container;
import warehouse.Warehouse;

public class Berth {
    private int id;
    private Warehouse portWarehouse;
    
    public Berth(int id, Warehouse warehouse) {
	this.id = id;
	portWarehouse = warehouse;
    }

    public int getId() {
	return id;
    }

    public boolean add(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
	boolean result = false;
	Lock portWarehouseLock = portWarehouse.getLock();	
	boolean portLock = false;

	try{
    	    portLock = portWarehouseLock.tryLock(30, TimeUnit.SECONDS);
	    if (portLock) {
                /*Старая версия:
                  int newConteinerCount = portWarehouse.getRealSize()	+ numberOfConteiners;
		  if (newConteinerCount <= portWarehouse.getFreeSize()) {
		    result = doMoveFromShip(shipWarehouse, numberOfConteiners);	
                  }
                  ошибка в том, что к значению количества добавляемых контейнеров прибавляется значение
                  количества контейнеров, имеющихся на складе, и после эта сумма сравнивается со свободным местом
                  на складе. Таким образом, если склад заполнен на половину, добавить в него контейнеры мы уже не сможем
                */
		if (numberOfConteiners <= portWarehouse.getFreeSize()) {
		    result = doMoveFromShip(shipWarehouse, numberOfConteiners);	
		}
	    }
	} 
        finally{
	    if (portLock) {
		portWarehouseLock.unlock();
	    }
        }
	return result;
    }
	
    private boolean doMoveFromShip(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException{
	//блокировка склада корабля необязательна, поскольку склад порта один на все причалы
	//и проблем синхронизации возникать не должно. Однако это не ошибка.
	Lock shipWarehouseLock = shipWarehouse.getLock();
	boolean shipLock = false;
		
	try{
	    shipLock = shipWarehouseLock.tryLock(30, TimeUnit.SECONDS);
	    if (shipLock) {
		if(shipWarehouse.getRealSize() >= numberOfConteiners){
                    List<Container> containers = shipWarehouse.getContainer(numberOfConteiners);
                    portWarehouse.addContainer(containers);
                    return true;
		}
	    }
	}
        finally{
	    if (shipLock) {
		shipWarehouseLock.unlock();
	    }
	}		
	return false;		
    }

    public boolean get(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
	boolean result = false;
	Lock portWarehouseLock = portWarehouse.getLock();	
	boolean portLock = false;

	try{
	    portLock = portWarehouseLock.tryLock(30, TimeUnit.SECONDS);
		if (portLock) {
		    if (numberOfConteiners <= portWarehouse.getRealSize()) {
			result = doMoveFromPort(shipWarehouse, numberOfConteiners);	
		    }
		}
	} 
        finally{
	    if (portLock) {
		portWarehouseLock.unlock();
	    }
	}
        return result;
    }
	
    private boolean doMoveFromPort(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException{
	Lock shipWarehouseLock = shipWarehouse.getLock();
	boolean shipLock = false;
		
	try{
	    shipLock = shipWarehouseLock.tryLock(30, TimeUnit.SECONDS);
	    if (shipLock) {
		/*Старая версия:
                int newConteinerCount = shipWarehouse.getRealSize() + numberOfConteiners;
		if(newConteinerCount <= shipWarehouse.getFreeSize()){
		    List<Container> containers = portWarehouse.getContainer(numberOfConteiners);
		    shipWarehouse.addContainer(containers);
		    return true;
		}
                ошибка в том, что к значению количества добавляемых контейнеров прибавляется значение
                количества контейнеров, имеющихся на складе, и после эта сумма сравнивается со свободным местом
                на складе. Таким образом, если склад заполнен на половину, добавить в него контейнеры мы уже не сможем
                */
		if(numberOfConteiners <= shipWarehouse.getFreeSize()){
		    List<Container> containers = portWarehouse.getContainer(numberOfConteiners);
		    shipWarehouse.addContainer(containers);
	    	    return true;
		}
	    }
	}
        finally{
	    if (shipLock) {
		shipWarehouseLock.unlock();
	    }
	}	
        return false;		
    }
    
}

package org.eclipse.symfony.index;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SymfonyIndexer {
	
	private static SymfonyIndexer instance = null;
	
	private SymfonyDbFactory factory;
	private Connection connection;
	private IServiceDao serviceDao;
	
	private SymfonyIndexer() throws Exception {

		factory = SymfonyDbFactory.getInstance();
		connection = factory.createConnection();
		serviceDao = factory.getServiceDao();
		
	}
	

	public static SymfonyIndexer getInstance() throws Exception {
		
		if (instance == null)
			instance = new SymfonyIndexer();
		
		return instance;		
		
	}
	
	
	public void addService(String id, String phpClass, String path, int timestamp) {
		
		try {
			serviceDao.insert(connection, path, id, phpClass, timestamp);
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
	}
	
	public void startIndexing(String path) {

		serviceDao.deleteServices(connection, path);		
		
	}

	public void commit() throws Exception {
		
		serviceDao.commitInsertions();
		
	}


	public void findServices(String string, IServiceHandler iServiceHandler) {

		serviceDao.findServicesByPath(connection, string, iServiceHandler);		

	}


	public void findService(String id, String path,
			IServiceHandler iServiceHandler) {

		
		serviceDao.findService(connection, id, path, iServiceHandler);
		
	}
}
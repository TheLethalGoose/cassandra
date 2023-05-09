package de.fh.dortmund.service;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import de.fh.dortmund.helper.Timer;

public class REST {

	Session session;
	Timer timer = new Timer();
	boolean debug = false;

	public REST(Session session, boolean debug){
		this.session = session;
		this.debug = debug;
	}

}

package br.edu.ufcg.lsd.oursim.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WraperTask {

	String cmd;

	List<String> inputs = new ArrayList<String>();

	List<String> outputs = new ArrayList<String>();
	
	Map<String,String> labels = new HashMap<String, String>();

	@Override
	public String toString() {
		return cmd;
	}

}

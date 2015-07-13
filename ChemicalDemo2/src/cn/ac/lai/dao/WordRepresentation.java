package cn.ac.lai.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class WordRepresentation {
	private Map<String, String> dict = new HashMap<String, String>(); 
	
	public WordRepresentation(String fname) {
		load(fname); 
	}
	
	private void load(String fname) {
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "UTF-8"));
			
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				if (nextLine.trim().equals("")) {
					continue; 
				}
				String [] parts = nextLine.trim().split("\\s"); 
				
				if (parts.length != 3) {
					throw new IllegalArgumentException("The format of file \"" + fname + "\" is wrong at #line: "+ nLine); 
				}
				
				this.dict.put(parts[1], parts[0]); 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public String get(String word) {
		return this.dict.get(word); 
	}
}

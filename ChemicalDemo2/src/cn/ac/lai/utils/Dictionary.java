package cn.ac.lai.utils; 

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Dictionary {
	private List<String> dict; 
	private boolean isIgnoreCase; 
	
	public Dictionary(boolean isIgnoreCase) {
		this.dict = new ArrayList<String>(); 
		this.isIgnoreCase = isIgnoreCase; 
	}
	
	public Dictionary() {
		this(true); 
	}
	
	public Dictionary(String fname) {
		this(true); 
		load(fname); 
	}
	
	public Dictionary(String fname, boolean isIgnoreCase) {
		this(isIgnoreCase); 
		load(fname); 
	}
	
	public int getNumWords() {
		return this.dict.size(); 
	}
	
	private int load(String fname) {
		try {
			FileReader file = new FileReader(fname); 
			BufferedReader buff = new BufferedReader(file); 
			
			for (String line; (line = buff.readLine()) != null; ) {
				add(line); 
			}
			
			file.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		return this.dict.size(); 
	}
	
	public boolean contains(String word) {
		return this.isIgnoreCase? this.dict.contains(word.trim().toLowerCase()): this.dict.contains(word.trim()); 
	}
	
	public String get(int index) {
		if (index >= this.dict.size()) {
			return null; 
		}
		
		return this.dict.get(index); 
	}
	
	public int indexOf(String word) {
		return this.isIgnoreCase? this.dict.indexOf(word.trim().toLowerCase()): this.dict.indexOf(word.trim()); 
	}
	
	public int add(String word) {
		word = this.isIgnoreCase? word.trim().toLowerCase(): word.trim(); 
		if (word.length() == 0) {
			return -1; 
		}
		if (!this.dict.contains(word)) {
			this.dict.add(word); 
		}
		
		return this.dict.indexOf(word); 
	}
	
	public int add(CharSequence word) {
		return add(word.toString()); 
	}
	
	public int add(String [] words) {
		for (int i = 0; i < words.length; i++) {
			add(words[i]); 
		}
		
		return words.length; 
	}
	
	public int add(CharSequence [] words) {
		for (int i = 0; i < words.length; i++) {
			add(words[i]); 
		}
		
		return words.length; 
	}
	
	public void delete(String word) {
		word = this.isIgnoreCase? word.trim().toLowerCase(): word.trim(); 
		
		if (this.dict.contains(word)) {
			this.dict.remove(word);
		}
	}
	
	public void print(PrintStream out) {
		for (int i = 0; i < this.dict.size(); i++) {
			out.println(this.dict.get(i)); 
		}
	}
	
	public Dictionary join(Dictionary dict2) {
		Dictionary commonDict = new Dictionary(this.isIgnoreCase); 
		
		for (int i = 0; i < this.dict.size(); i++) {
			if (dict2.contains(this.dict.get(i))) {
				commonDict.add(this.dict.get(i)); 
			}
		}
		
		return commonDict; 
	}
	
	public Dictionary notContains(Dictionary dict2) {
		Dictionary diffDict = new Dictionary(this.isIgnoreCase); 
		
		for (int i = 0; i < this.dict.size(); i++) {
			if (!dict2.contains(this.dict.get(i))) {
				diffDict.add(this.dict.get(i)); 
			}
		}
		
		return diffDict; 
	}
	
	public Dictionary union(Dictionary dict2) {
		Dictionary commonDict = new Dictionary(this.isIgnoreCase); 
		
		for (int i = 0; i < this.dict.size(); i++) {
			commonDict.add(this.dict.get(i)); 
		}
		
		for (int i = 0; i < dict2.getNumWords(); i++) {
			commonDict.add(dict2.get(i)); 
		}
		
		return commonDict; 
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		/*Dictionary dictDevelopment = new Dictionary("data/development/dict.txt"); 
		Dictionary dictTrain = new Dictionary("data/train/dict.txt"); 
		
		Dictionary development_train = dictDevelopment.notContains(dictTrain); 
		Dictionary train_development = dictTrain.notContains(dictDevelopment); 
		
		development_train.print(new PrintStream("data/development_train_dict.txt")); 
		train_development.print(new PrintStream("data/train_development_dict.txt")); 
		*/
		
		Dictionary dictDevelopmentEntityToken = new Dictionary("data/development/dict-entity-token.txt", false); 
		Dictionary dictTrainEntityToken = new Dictionary("data/train/dict-entity-token.txt", false); 
		dictDevelopmentEntityToken.union(dictTrainEntityToken).print(new PrintStream("data/dict-entity-token.txt")); 
		
		Dictionary dictDevelopmentEntity = new Dictionary("data/development/dict-entity.txt", false); 
		Dictionary dictTrainEntity = new Dictionary("data/train/dict-entity.txt", false);
		dictDevelopmentEntity.union(dictTrainEntity).print(new PrintStream("data/dict-entity.txt")); 
		
		System.out.println("done."); 
	}
}
package cn.ac.lai.dao;

import java.io.FileNotFoundException;
import java.util.List;

import cn.ac.lai.utils.TextPolicy;
import cn.ac.lai.utils.AnnotatedEntity;
import cn.ac.lai.utils.ArticleProfile;


public class TrainCorpus extends Corpus {
	
	public TrainCorpus(DBConnect dbcon) {
		super(dbcon, "training"); 
	}
	
	// @type: "T": training or "D": development
	@Override
	protected void check(String type) {
		type = type.trim().toUpperCase(); 
		if (type.equalsIgnoreCase("T") && type.equalsIgnoreCase("H")) {
			throw new IllegalArgumentException("The parameter @type must be \"T\" or \"H\"."); 
		}
	}
	
	public String insert(ArticleProfile article) {
		return insert(article, "T"); 
	}
	
	public String insert(String fileID, String fileContent) {
		return insert(fileID, fileContent, "T"); 
	}
	
	public void load(String fname) {
		load(fname, "T"); 
	}
	
	@Override
	List<AnnotatedEntity> getEntity(String fileID, TextPolicy policy) {
		TrainAnnotation annotation = new TrainAnnotation(this.dbcon);
		
		return annotation.getEntity(fileID, policy); 
	}

	public static void main(String[] args) throws FileNotFoundException {
		DBConnect dbcon = new DBConnect(); 
		dbcon.openDB();
		
		TrainCorpus train = new TrainCorpus(dbcon); 
		
		if (train.size() == 0) {
			//insert training data set
			train.load("data/corpus/training.txt", "T"); 
			
			//insert development data set
			//train.load("data/corpus/harmonized.txt", "H"); 
		} 
		
		System.out.println("done."); 
		dbcon.closePSTMT(); 
	}
}

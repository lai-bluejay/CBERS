package cn.ac.lai.dao;

import java.util.List;

import cn.ac.lai.utils.TextPolicy;
import cn.ac.lai.utils.AnnotatedEntity;
import cn.ac.lai.utils.ArticleProfile;


public class TestCorpus extends Corpus {
	
	public TestCorpus(DBConnect dbcon) {
		super(dbcon, "testing");  
	}
	
	// @type: "Y": testing or "N": background
	@Override
	protected void check(String type) {
		type = type.trim();  
		if (type.equalsIgnoreCase("Y") && type.equalsIgnoreCase("N")) {
			throw new IllegalArgumentException("The parameter @type must be \"Y\" or \"N\"."); 
		}
	}
	
	public String insert(ArticleProfile article) {
		return insert(article, "Y"); 
	}
	
	public String insert(String fileID, String fileContent) {
		return insert(fileID, fileContent, "Y"); 
	}
	
	public void load(String fname) {
		load(fname, "Y"); 
	}
	
	
	@Override
	List<AnnotatedEntity> getEntity(String fileID, TextPolicy policy) {
		TestAnnotation annotation = new TestAnnotation(this.dbcon);
		
		return annotation.getEntity(fileID, policy);
	}

	public static void main(String[] args) {
		DBConnect dbcon = new DBConnect(); 
		dbcon.openDB();
		
		TestCorpus test = new TestCorpus(dbcon); 
		
		//insert testing annotation data set
		if (test.size() == 0) {
			test.load("data/corpus/harmonized.txt");
			//test.updateType("data/test_annotation/chemdner_abs_test_fileID_label.txt");
		}
		
		System.out.println("done."); 
		dbcon.closePSTMT(); 
	}
}

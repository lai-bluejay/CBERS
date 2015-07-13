package cn.ac.lai.dao;

import java.io.FileNotFoundException;

public class TestAnnotation extends Annotation {

	public TestAnnotation(DBConnect dbcon) {
		super(dbcon, "testing_annotation"); 
	}
	
	// @type: "Y": testing or "N": background
	@Override
	protected void check(String type) {
		type = type.trim();  
		if (type.equalsIgnoreCase("Y") && type.equalsIgnoreCase("N")) {
			throw new IllegalArgumentException("The parameter @type must be \"Y\" or \"N\"."); 
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		DBConnect dbcon = new DBConnect(); 
		dbcon.openDB();
		
		TestAnnotation test = new TestAnnotation(dbcon); 
		
		if (test.size() == 0) {
			//insert test annotation data set
			test.load("data/corpus/harmonized.ann"); 
		}
		
		//test.print(new PrintStream("data/test_annotation/golden-entities.txt")); 
		//test.printNestedEntities(new PrintStream("data/test_annotation/nest-entities.txt"), true); 
		
		System.out.println("done."); 
		dbcon.closePSTMT(); 
	}
}

package cn.ac.lai.dao;

import java.io.FileNotFoundException;

public class TrainAnnotation extends Annotation {
	
	public TrainAnnotation(DBConnect dbcon) {
		super(dbcon, "training_annotation"); 
	}
	
	// @type: "T": training or "D": development
	@Override
	protected void check(String type) {
		type = type.trim().toUpperCase(); 
		if (type.equalsIgnoreCase("T") && type.equalsIgnoreCase("D")) {
			throw new IllegalArgumentException("The parameter @type must be \"T\" or \"D\"."); 
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		DBConnect dbcon = new DBConnect(); 
		dbcon.openDB();
		
		TrainAnnotation train = new TrainAnnotation(dbcon); 
		
		if (train.size() == 0) {
			//insert training annotation data set
			train.load("data/corpus/training.ann"); 
			
			//insert development annotation data set
			//train.load("data/corpus/harmonized.ann"); 
		}
		
		//train.print(new PrintStream("data/train/golden-entities.txt")); 
		//train.printNestedEntities(new PrintStream("data/train/nest-entities2.txt"), true); 
		
		System.out.println("done."); 
		dbcon.closePSTMT(); 
	}
}

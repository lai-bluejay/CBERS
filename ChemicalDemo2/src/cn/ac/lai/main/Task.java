package cn.ac.lai.main;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import cn.ac.lai.converter.FormatConverter;
import cn.ac.lai.dao.DBConnect;
import cn.ac.lai.dao.TestAnnotation;
import cn.ac.lai.dao.TestCorpus;
import cn.ac.lai.dao.TrainAnnotation;
import cn.ac.lai.dao.TrainCorpus;
import cn.ac.lai.dao.WordRepresentation;
import cn.ac.lai.metrics.Evaluation;
import cn.ac.lai.tools.ConfusionMatrix;
import cn.ac.lai.tools.Metrics;
import cn.ac.lai.utils.PredictedEntity;

public class Task {
	private DBConnect dbcon; 
	
	private TrainCorpus trainCorpus; 
	private TestCorpus testCorpus; 
	
	private TrainAnnotation trainAnn;
	private TestAnnotation testAnn; 
	
	private final String fnameTrainCorpus = "data/corpus/training.txt"; 
	//private final String fnameDevelopmentCorpus = "data/corpus/harmonized.txt"; 
	private final String fnameTestCorpus = "data/corpus/harmonized.txt"; 
	
	private final String fnameTrainAnnotation = "data/corpus/training.ann"; 
	//private final String fnameDevelopmentAnnotation = "data/corpus/harmonized.ann"; 
	private final String fnameTestAnnotation = "data/corpus/harmonized.ann"; 
	
	private final String fnameTestfileIDLabel = "data/corpus/full_fileid_lable.txt"; 
	
	private int[] costs = {-3, -2, -1, 0, 1, 2, 3}; 
	//连接备份数据库用
	public Task(DBConnect dbcon) {
		this.dbcon = dbcon; 
		this.trainCorpus = new TrainCorpus(dbcon); 
		this.testCorpus = new TestCorpus(dbcon);
		this.trainAnn = new TrainAnnotation(dbcon); 
		this.testAnn = new TestAnnotation(dbcon); 
	}
	
	public void load() {
		if (this.trainCorpus.size("T") == 0) {
			this.trainCorpus.load(this.fnameTrainCorpus, "T"); 
			this.trainAnn.load(this.fnameTrainAnnotation); 
		}
		
		/*if (this.trainCorpus.size("H") == 0) {
			this.trainCorpus.load(this.fnameDevelopmentCorpus, "H"); 
			this.trainAnn.load(this.fnameDevelopmentAnnotation); 
		}*/
		
		if (this.testCorpus.size("F") == 0) {
			this.testCorpus.load(this.fnameTestCorpus, "H"); 
			this.testCorpus.updateType(this.fnameTestfileIDLabel);
			this.testAnn.load(this.fnameTestAnnotation); 
		}
	}
	
	public void printTokenWithBrownFormat(PrintStream out) {
		this.trainCorpus.printTokenWithBrownFormat(out); 
		this.testCorpus.printTokenWithBrownFormat(out); 
	}
	
	//@flag: true (train data set), false (test data set)
	public void printFeatures(PrintStream outInst, PrintStream outID, WordRepresentation brown, boolean flag) {
		if (flag) {
			this.trainCorpus.printFeatures(outInst, outID, brown); 
		} else {
			this.testCorpus.printFeatures(outInst, outID, brown, "Y"); 
		}
	}
	
	// String[] cases = {"without", "500", "1000", "1500", "2000"}; 
	public void printCVBatch(int fold, String[] cases) {
		for (String cs: cases) {
			printCVBatch(fold, cs); 
		}
	}
	
	//int fold = 10; 
	public void printCVBatch(int fold, String cs) {
		String fname = "data/cv-" + cs + ".bat"; 
		try {
			PrintStream out = new PrintStream(fname);
			
			for (int v = 0; v < fold; v++) {
				for (int c: this.costs) {
					out.println("@echo 2^" + c + " " + v);
					out.println("\"../tools/CRF++-0.58-win/crf_learn\" -f 2 -p 4 -c " + Math.pow(2, c) 
							+ " crf/PatentCorpus-" + cs + ".template"
							+ " crf/cv/" + cs + "/" + v + ".train"
							+ " crf/cv/" + cs + "/" + c+ "/" + v + ".model"); 
					out.println("\"../tools/CRF++-0.58-win/crf_test\" -v 1 -n 1" 
							+ " -m crf/cv/" + cs+ "/" + c+ "/" + v + ".model"
							+ " -o crf/cv/" + cs + "/" + c + "/" + v + ".out"
							+ " crf/cv/" + cs + "/" + v + ".test"); 
				}
			}
				
			out.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	public double getBestCost(int fold, String cs) {
		double cost = -10;
		double maxF = 0; 
		
		for (int c: this.costs) {
			ConfusionMatrix m = new ConfusionMatrix(); 
			
			for (int v = 4; v < 7; v++) {
				String fnameID = "data/crf/cv/" + cs + "/" + v + ".test-id"; 
				String fnameOut = "data/crf/cv/" + cs + "/" + c + "/" + v + ".out"; 
				
				String fnameAnnotation = "data/crf/cv/" + cs + "/" + v + ".test.cem"; 
				String fnamePrediction = "data/crf/cv/" + cs + "/" + c + "/" + v + ".cem"; 
				FormatConverter converter = new FormatConverter(this.dbcon, true); 
				converter.readResult(fnameID, fnameOut, 1); 
				List<PredictedEntity> entities = converter.getPredictedEntity();
				List<PredictedEntity> entities2=entities;
				//entities = converter.postprocess(entities, true); 
				try {
					converter.printCEMFormat(new PrintStream(fnamePrediction), entities, false);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}  
				
				Evaluation e = new Evaluation(fnameAnnotation, fnamePrediction); 
				m.add(e.getConfusionMatrix()); 
				System.out.println(v);
			}
			for (int v = 8; v < fold; v++) {
				String fnameID = "data/crf/cv/" + cs + "/" + v + ".test-id"; 
				String fnameOut = "data/crf/cv/" + cs + "/" + c + "/" + v + ".out"; 
				
				String fnameAnnotation = "data/crf/cv/" + cs + "/" + v + ".test.cem"; 
				String fnamePrediction = "data/crf/cv/" + cs + "/" + c + "/" + v + ".cem"; 
				FormatConverter converter = new FormatConverter(this.dbcon, true); 
				converter.readResult(fnameID, fnameOut, 1); 
				List<PredictedEntity> entities = converter.getPredictedEntity(); 
				//entities = converter.postprocess(entities, true); 
				try {
					converter.printCEMFormat(new PrintStream(fnamePrediction), entities, false);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}  
				
				Evaluation e = new Evaluation(fnameAnnotation, fnamePrediction); 
				m.add(e.getConfusionMatrix()); 
				System.out.println(v);
			}
			double f = Metrics.f(m); 
			System.out.println(f); 
			
			if (maxF < f) {
				maxF = f; 
				cost = c; 
			}
		}
		
		return cost; 
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		DBConnect dbcon = new DBConnect(); 
		dbcon.openDB();
		
		Task task = new Task(dbcon); 
		//brown聚类词条已经用TokenizerForPubmed生成
     //	task.printTokenWithBrownFormat(new PrintStream("data/brown/BrownFormat.token")); 
		
	/*	task.printFeatures(new PrintStream("data/crf/PatentCorpus-without.train"), 
				new PrintStream("data/crf/PatentCorpus-without.train-id"), null, true);
		task.printFeatures(new PrintStream("data/crf/PatentCorpus-without.test"), 
				new PrintStream("data/crf/PatentCorpus-without.test-id"), null, false);*/
		/*
		String[] cases = {"500", "1000", "1500", "2000"}; 
		for (String cs: cases) {
			WordRepresentation brown = new WordRepresentation("data/brown/" + cs + ".paths"); 
			
			
			task.printFeatures(new PrintStream("data/crf/BioCreative-" + cs + ".train"), 
					new PrintStream("data/crf/BioCreative-" + cs + ".train-id"), 
					brown, 
					true);
			
			task.printFeatures(new PrintStream("data/crf/BioCreative-" + cs + ".test"), 
					new PrintStream("data/crf/BioCreative-" + cs + ".test-id"), 
					brown, 
					false);
		}*/
		
		
		//String[] cases = {"without", "500", "1000", "1500", "2000"}; 
		//task.printCVBatch(10, cases); 
		//task.printCVBatch(10, "without");
		
		//task.getBestCost(10, "without"); 
		
		System.out.println("done."); 
		dbcon.closePSTMT(); 
	}
}

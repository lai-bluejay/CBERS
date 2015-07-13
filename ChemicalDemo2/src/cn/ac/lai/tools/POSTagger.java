package cn.ac.lai.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Sequence;

public class POSTagger {
	private static POSTaggerME tagger = null; 
	private static final String modelFile = "models/en-pos-maxent.bin"; 
	
	private static void loadModel(String fname) {
		POSModel model = null; 
		try {
			InputStream stream = new FileInputStream(fname); 
			
			try {
				model = new POSModel(stream); 
			} catch (IOException e) {
				e.printStackTrace(); 
			} finally {
				if (stream != null) {
					try {
						stream.close(); 
					} catch (IOException e) {
						e.printStackTrace(); 
					}
				}
			}
			
			tagger = new POSTaggerME(model); 
		} catch (FileNotFoundException e) {
			e.printStackTrace(); 
		}
	}
	
	public static String[] tag(String[] sent) {
		if (tagger == null) {
			loadModel(modelFile); 
		}
		
		return tagger.tag(sent); 
	}
	
	/*
	public static double[] probs(String[] sent) {
		if (tagger == null) {
			loadModel(modelFile); 
		}
		
		tagger.tag(sent); 
		return tagger.probs(); 
	}
	*/
	
	public static Sequence[] topKSequences(String[] sent) {
		if (tagger == null) {
			loadModel(modelFile); 
		}
		
		return tagger.topKSequences(sent); 
	}
	
	public static void main(String[] args) {
		String sent[] = new String[] {"Most", "large", "cities", "in", "the", "US", "had", 
				"moring", "and", "afternoon", "newspapers"};
		String tags[] = POSTagger.tag(sent); 
		for (int i = 0; i < sent.length; i++) {
			System.out.println(sent[i] + "_" + tags[i]); 
		}
	}
}

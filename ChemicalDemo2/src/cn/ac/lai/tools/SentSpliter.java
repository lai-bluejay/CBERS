package cn.ac.lai.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

public class SentSpliter {
	private static SentenceDetectorME detector = null; 
	private static final String modelFile = "models/en-sent.bin"; 
	
	private static void loadModel(String fname) {
		SentenceModel model = null; 
		try {
			InputStream stream = new FileInputStream(fname); 
			
			try {
				model = new SentenceModel(stream); 
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
			
			detector = new SentenceDetectorME(model); 
		} catch (FileNotFoundException e) {
			e.printStackTrace(); 
		}
	}
	
	static public String[] sentDetect(String str) {
		if (detector == null) {
			loadModel(modelFile); 
		}
		
		return detector.sentDetect(str); 
	}
	
	static public Span[] sentSpanDetect(String str) {
		if (detector == null) {
			loadModel(modelFile); 
		}
		
		return detector.sentPosDetect(str); 
	}
	
	public static void main(String[] args) {
		String str = "Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29. Mr. Vinken is"
			+ "chairman of Elsevier N.V., the Dutch publishing group. Rudolph Agnew, 55 years"
			+ "old and former chairman of Consolidated Gold Fields PLC, was named a director of this"
			+ "British industrial conglomerate."; 
		String[] sents = SentSpliter.sentDetect(str); 
		for (String sent: sents) {
			System.out.println(sent); 
		}
	}
}

package cn.ac.lai.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class Tokenizer {
	private static TokenizerME tokenizer = null; 
	private static final String modelFile = "models/en-token.bin"; 
	
	private static void loadModel(String fname) {
		TokenizerModel model = null; 
		try {
			InputStream stream = new FileInputStream(fname); 
			
			try {
				model = new TokenizerModel(stream); 
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
			
			tokenizer = new TokenizerME(model); 
		} catch (FileNotFoundException e) {
			e.printStackTrace(); 
		}
	}
	
	public static String[] tokenize(String str) {
		if (tokenizer == null) {
			loadModel(modelFile); 
		}
		
		return tokenizer.tokenize(str); 
	}
	
	public static Span[] tokenizePos(String str) {
		if (tokenizer == null) {
			loadModel(modelFile); 
		}
		
		return tokenizer.tokenizePos(str); 
	}
	
	public static void main(String[] args) {
		String fname="";
		String str = "3-[2-(3-Fluorophenyl)ethenyl]-picolinamide	2-cyano-3-picoline	3-fluorobenzaldehyde	"
				+ "tetrahydrofuran	potassium t-butoxide	toluene	toluene	3-[2-(3-Fluorophenyl)ethyl]-picolinamide	"
				+ "3-[2-(3-fluorophenyl)ethenyl]-picolinamide	acetic acid	3-[2-(3-fluorophenyl)ethyl]-picolinamide	"
				+ "3-[2-(3-Fluorophenyl)ethyl]-picolinic acid	3-[2-(3-fluorophenyl)ethyl]-picolinamide	"
				+ "ethanol	8-Fluoro-6,11-dihydro-5H-benzo-[5,6]-cyclohepta[1,2-b]pyridin-11-one	3-[2-(3-fluorophenyl)ethyl]-picolinic"
				+ " acid	tetrachloroethane	Oxalylchloride	Benzene	benzene	benzene	butyl acetate	ethyl acetate";
		String[] tokens = Tokenizer.tokenize(str); 
		for (int i = 0; i < tokens.length; i++) {
			System.out.println(tokens[i]); 
		}
	}
}

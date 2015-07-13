package cn.ac.lai.main;

import java.io.IOException;

public class CRFTestCaller {
	private final String fnameEXE = "data/tools/CRF++-0.58-win/crf_test.exe"; 
	
	private int nbest = 1; 
	private int verbose = 1; 
	
	private String fnameOut; 
	private String fnameTest; 
	private String fnameModel; 
	
	public CRFTestCaller(String fnameTest, String fnameModel, String fnameOut) {
		this.fnameTest = fnameTest; 
		this.fnameModel = fnameModel; 
		this.fnameOut = fnameOut; 
	}

	public int getNbest() {
		return this.nbest;
	}

	public void setNbest(int nbest) {
		this.nbest = nbest;
	}

	public int getVerbose() {
		return this.verbose;
	}

	public void setVerbose(int verbose) {
		this.verbose = verbose;
	}
	
	public void run() {
		Runtime r = Runtime.getRuntime(); 
		
		try {
			r.exec(new String[]{this.fnameEXE, 
					"-n " + getNbest(), 
					"-v " + getVerbose(), 
					"-m " + this.fnameModel, 
					"-o " + this.fnameOut,
					this.fnameTest});
		} catch (IOException e) {
			e.printStackTrace(); 
		} 
	}
	
	public static void main(String[] args) {
		String fnameTest = "data/tools/CRF++-0.58-win/example/chunking/test.data"; 
		String fnameModel = "data/tools/CRF++-0.58-win/example/chunking/model"; 
		String fnameOut = "data/tools/CRF++-0.58-win/example/chunking/test.out2"; 
		
		CRFTestCaller test = new CRFTestCaller(fnameTest, fnameModel, fnameOut); 
		
		test.run(); 
		
		System.out.println("done."); 
	}
}

package cn.ac.lai.main;

import java.io.IOException;

public class CRFLearnCaller {
	private final String fnameEXE = "data/tools/CRF++-0.58-win/crf_learn.exe"; 
	private double cost = 1.0; 
	private int freq = 1; 
	private int maxIter = 10000; 
	private int shrinkingSize = 20; 
	
	private String fnameTemplate; 
	private String fnameTrain; 
	private String fnameModel; 
	
	public CRFLearnCaller(String fnameTemplate, String fnameTrain, String fnameModel) {
		this.fnameTemplate = fnameTemplate; 
		this.fnameTrain = fnameTrain; 
		this.fnameModel = fnameModel; 
	}
	
	public double getCost() {
		return this.cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public int getFreq() {
		return this.freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	public int getMaxIter() {
		return this.maxIter;
	}

	public void setMaxIter(int maxIter) {
		this.maxIter = maxIter;
	}

	public int getShrinkingSize() {
		return this.shrinkingSize;
	}

	public void setShrinkingSize(int shrinkingSize) {
		this.shrinkingSize = shrinkingSize;
	}

	public void run() {
		Runtime r = Runtime.getRuntime(); 
		
		try {
			r.exec(new String[]{this.fnameEXE, 
					"-f " + getFreq(), 
					"-m " + getMaxIter(), 
					"-c " + getCost(), 
					"-H " + getShrinkingSize(),
					this.fnameTemplate, 
					this.fnameTrain, 
					this.fnameModel}); 
		} catch (IOException e) {
			e.printStackTrace(); 
		} 
	}
	
	public static void main(String[] args) {
		String fnameTemplate = "data/tools/CRF++-0.58-win/example/chunking/template"; 
		String fnameTrain = "data/tools/CRF++-0.58-win/example/chunking/train.data"; 
		String fnameModel = "data/tools/CRF++-0.58-win/example/chunking/model2"; 
		
		CRFLearnCaller learn = new CRFLearnCaller(fnameTemplate, fnameTrain, fnameModel); 
		learn.setCost(2.0); 
		
		learn.run(); 
		
		System.out.println("done."); 
	}
}

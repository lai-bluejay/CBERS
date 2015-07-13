package cn.ac.lai.metrics;

import opennlp.tools.util.Span;

class PredictedCEM extends AnnotatedCEM {
	private int rank; 
	private double confidence; 
	
	public PredictedCEM(String type, Span offset, int rank, double confidence) {
		super(type, offset);
		this.rank = rank; 
		this.confidence = confidence; 
	}
	
	public PredictedCEM(String type, int startOffset, int endOffset, int rank, double confidence) {
		super(type, startOffset, endOffset);
		this.rank = rank; 
		this.confidence = confidence; 
	}

	public int getRank() {
		return this.rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public double getConfidence() {
		return this.confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	@Override
	public String toString() {
		return super.toString() + "\t" + getRank() + "\t" + getConfidence();
	}
}

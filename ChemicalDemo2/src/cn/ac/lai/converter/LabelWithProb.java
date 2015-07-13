package cn.ac.lai.converter;

public class LabelWithProb {
	private String label; 
	private double prob; 
	
	public LabelWithProb(String label) {
		this.label = label; 
		this.prob = 1.0; 
	}
	
	public LabelWithProb(String label, double prob) {
		this(label); 
		this.prob = prob; 
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getProb() {
		return this.prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}

	@Override
	public String toString() {
		return this.label + "/" + this.prob;
	}
	
}

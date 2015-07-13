package cn.ac.lai.tools;

public class ConfusionMatrix {
	private int TP; 
	private int TN; 
	private int FP; 
	private int FN; 
	
	public ConfusionMatrix(int TP, int TN, int FP, int FN) {
		this.TP = TP; 
		this.TN = TN; 
		this.FP = FP; 
		this.FN = FN; 
	}
	
	public ConfusionMatrix() {
		this(0, 0, 0, 0); 
	}

	public int getTP() {
		return this.TP;
	}

	public void setTP(int TP) {
		this.TP = TP;
	}

	public int getTN() {
		return this.TN;
	}

	public void setTN(int TN) {
		this.TN = TN;
	}

	public int getFP() {
		return this.FP;
	}

	public void setFP(int FP) {
		this.FP = FP;
	}

	public int getFN() {
		return this.FN;
	}

	public void setFN(int FN) {
		this.FN = FN;
	}
	
	public void add(ConfusionMatrix another) {
		this.TP += another.getTP(); 
		this.TN += another.getTN(); 
		this.FP += another.getFP(); 
		this.FN += another.getFN(); 
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(getTP() + "(TP)\t" + getFN() + "(FN)\n")
		  .append(getFP() + "(FP)\t" + getTN() + "(TN)"); 
		return sb.toString();
	}
}

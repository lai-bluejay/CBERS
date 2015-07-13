package cn.ac.lai.tools;

public class Metrics {
	public static double recall(int TP, int FN) {
		return (double)TP / (double)(TP + FN); 
	}
	
	public static double recall(ConfusionMatrix m) {
		return recall(m.getTP(), m.getFN()); 
	}
	
	public static double precision(int TP, int FP) {
		return (double)TP / (double)(TP + FP); 
	}
	
	public static double precision(ConfusionMatrix m) {
		return precision(m.getTP(), m.getFP()); 
	}
	
	public static double f(int TP, int FP, int FN, double beta) {
		double p = precision(TP, FP);
		double r = recall(TP, FN); 
		double beta2 = beta * beta; 
		
		return (1 + beta2) * p * r / (beta2 * p + r); 
	}
	
	public static double f(ConfusionMatrix m, double beta) {
		return f(m.getTP(), m.getFP(), m.getFN(), beta); 
	}
	
	public static double f(int TP, int FP, int FN) {
		return f(TP, FP, FN, 1.0); 
	}
	
	public static double f(ConfusionMatrix m) {
		return f(m, 1.0); 
	}
}

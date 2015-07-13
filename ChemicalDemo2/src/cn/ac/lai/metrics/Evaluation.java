package cn.ac.lai.metrics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.lai.tools.ConfusionMatrix;
import cn.ac.lai.tools.Metrics;

public class Evaluation {
	private String fnameAnnotation; 
	private String fnamePrediction; 
	
	private Map<String, List<AnnotatedCEM>> annotatedList; 
	private Map<String, List<PredictedCEM>> predictedList; 
	
	public Evaluation(String fnameAnnotation, String fnamePrediction) {
		this.fnameAnnotation = fnameAnnotation; 
		this.fnamePrediction = fnamePrediction; 
		
		load(); 
	}
	
	private void load() {
		this.annotatedList = new HashMap<String, List<AnnotatedCEM>>(); 
		loadAnnotation(); 
		
		this.predictedList = new HashMap<String, List<PredictedCEM>>(); 
		loadPrediction(); 
	}
	
	private void loadAnnotation() {
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.fnameAnnotation), "UTF-8"));
			
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				String [] parts = nextLine.trim().split("[\t|:]"); 
				
				if (parts.length != 4) {
					throw new IllegalArgumentException("The format of file \"" + this.fnameAnnotation + "\" is wrong at #line: "+ nLine); 
				}
				
				if (!this.annotatedList.containsKey(parts[0])) {
					this.annotatedList.put(parts[0], new ArrayList<AnnotatedCEM>()); 
				}
				
				AnnotatedCEM e = new AnnotatedCEM(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3])); 
				this.annotatedList.get(parts[0]).add(e); 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void loadPrediction() {		
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.fnamePrediction), "UTF-8"));
			
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				String [] parts = nextLine.trim().split("[\t|:]"); 
				
				if (parts.length != 6) {
					throw new IllegalArgumentException("The format of file \"" + this.fnamePrediction + "\" is wrong at #line: "+ nLine); 
				}
				
				if (!this.predictedList.containsKey(parts[0])) {
					this.predictedList.put(parts[0], new ArrayList<PredictedCEM>()); 
				}
				
				PredictedCEM e = new PredictedCEM(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), 
						Integer.parseInt(parts[4]), Double.parseDouble(parts[5])); 
				this.predictedList.get(parts[0]).add(e); 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	//@flag: true for annotation, false for prediction
	public int size(boolean flag) {
		return flag? this.annotatedList.size(): this.predictedList.size(); 
	}
	
	//@flag: true for annotation, false for prediction
	public void print(PrintStream out, boolean flag) {
		if (flag) {
			for (Map.Entry<String, List<AnnotatedCEM>> entry: this.annotatedList.entrySet()) {
				for (AnnotatedCEM e: entry.getValue()) {
					out.println(entry.getKey() + "\t" + e); 
				}
			}
		} else {
			for (Map.Entry<String, List<PredictedCEM>> entry: this.predictedList.entrySet()) {
				for (PredictedCEM e: entry.getValue()) {
					out.println(entry.getKey() + "\t" + e); 
				}
			}
		}
	}
	
	private ConfusionMatrix calculate(List<AnnotatedCEM> aCEMs, List<PredictedCEM> pCEMs) {
		ConfusionMatrix matrix = new ConfusionMatrix(); 
		
		if (aCEMs == null && pCEMs == null) {
			return matrix; 
		}
		
		if (aCEMs == null && pCEMs != null) {
			matrix.setFP(pCEMs.size()); 
			return matrix; 
		}
		
		if (aCEMs != null && pCEMs == null) {
			matrix.setFN(aCEMs.size()); 
			return matrix; 
		}
		
		//Collections.sort(aCEMs); 
		//Collections.sort(pCEMs); 
		for (AnnotatedCEM a: aCEMs) {
			for (PredictedCEM p: pCEMs) {
				if (a.getType().equalsIgnoreCase(p.getType()) && a.getOffset().equals(p.getOffset())) {
					matrix.setTP(matrix.getTP() + 1); 
				}
			}
		}
		matrix.setFN(aCEMs.size() - matrix.getTP()); 
		matrix.setFP(pCEMs.size() - matrix.getTP()); 
		
		return matrix; 
	}
	
	public ConfusionMatrix getConfusionMatrix() {
		ConfusionMatrix matrix = new ConfusionMatrix(); 
		
		for (Map.Entry<String, List<AnnotatedCEM>> entry: this.annotatedList.entrySet()) {
			matrix.add(calculate(entry.getValue(), this.predictedList.get(entry.getKey()))); 
		}
		
		for (Map.Entry<String, List<PredictedCEM>> entry: this.predictedList.entrySet()) {
			matrix.add(calculate(this.annotatedList.get(entry.getKey()), entry.getValue())); 
		}
		
		// TP has been doubled
		matrix.setTP(matrix.getTP() / 2); 
		
		return matrix; 
	}
	
	public static void main(String[] args) {
		Evaluation e = new Evaluation("data/test_annotation/cem_ann_test_13-09-13.txt", "data/test/team_259_cem_run1.txt"); 
		
		ConfusionMatrix m = e.getConfusionMatrix(); 
		System.out.println(m); 
		System.out.println("Precision: " + Metrics.precision(m) + "Recall: " + Metrics.recall(m) + "F: " + Metrics.f(m)); 
		
		System.out.println("done."); 
	}
}

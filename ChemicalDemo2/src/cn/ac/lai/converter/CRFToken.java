package cn.ac.lai.converter;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

public class CRFToken {
	private String fileID; 
	private String name; 
	private String type; // "T": Training; "H": Harmonized;"F":FUll
	private String termID;
	private Span offset; 
	private List<LabelWithProb> labels; 
	
	public CRFToken(String fileID, String type, Span offset) {
		this.fileID = fileID; 
		this.type = type; 
		this.offset = offset; 
		this.labels = new ArrayList<LabelWithProb>(); 
	}
	
	public CRFToken(String fileID, String type, Span offset, List<LabelWithProb> labels) {
		this.fileID = fileID; 
		this.type = type; 
		this.offset = offset; 
		this.labels = labels; 
	}

	public String getfileID() {
		return this.fileID;
	}

	public void setfileID(String fileID) {
		this.fileID = fileID;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String gettermID() {
		return this.termID;
	}

	public void settermID(String termID) {
		this.termID = termID;
	}
	public Span getOffset() {
		return this.offset;
	}

	public void setOffset(Span offset) {
		this.offset = offset;
	}

	public List<LabelWithProb> getLabel() {
		return this.labels;
	}
	
	public LabelWithProb getLabel(int idx) {
		return this.labels.get(idx); 
	}
	
	public void addLabel(LabelWithProb label) {
		this.labels.add(label); 
	}
	
	public void addLabel(int idx, LabelWithProb label) {
		this.labels.add(idx, label); 
	}

	public void setLabel(List<LabelWithProb> labels) {
		this.labels = labels;
	}
	
	public int getNumOfLabels() {
		return this.labels.size(); 
	}
}

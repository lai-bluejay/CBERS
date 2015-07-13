package cn.ac.lai.utils;

import opennlp.tools.util.Span;

public class PredictedEntity extends Entity implements Comparable<PredictedEntity> {
	private double confidence; 
	
	public PredictedEntity(String fileID, String type, Span offset, String entityName, double confidence) {
		super(fileID, type, offset, entityName);
		this.confidence = confidence; 
	}
	
	public double getConfidence() {
		return confidence;
	}
	
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	public int compareTo(PredictedEntity another) {
		if (!getfileID().equalsIgnoreCase(another.getfileID())) {
			return getfileID().compareTo(another.getfileID()); 
		}
		
		if (getConfidence() > another.getConfidence()) {
			return -1; 
		} else if (getConfidence() < another.getConfidence()) {
			return 1; 
		} else {
			return getOffset().compareTo(another.getOffset()); 
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || !(obj instanceof PredictedEntity)) {
			return false; 
		}
		
		if (this == obj) {
			return true; 
		}
		
		PredictedEntity other = (PredictedEntity) obj;
		
		return super.equals(obj) && getConfidence() == other.getConfidence();
	}

	public String toString() {		
		return getfileID() + "\t" + getType() + ":" + getOffset().getStart() + ":" + getOffset().getEnd(); 
	}
}

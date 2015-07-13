package cn.ac.lai.utils;

import opennlp.tools.util.Span;

public class AnnotatedEntity extends Entity {
	private String className; 
	
	public AnnotatedEntity(String fileID, String type, String className, Span offset, String entityName) {
		super(fileID, type, offset, entityName); 
		this.className = className; 
	}
	
	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	@Override
	public String toString() {
		return getfileID().trim() + "\t" + getType().trim() + "\t" +gettermID().trim() + "\t" + getClassName().trim() + "\t"+ getOffset().getStart() + "\t" 
			+ getOffset().getEnd() + "\t" + getEntityName().trim() ; 
	}
}

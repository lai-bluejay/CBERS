package cn.ac.lai.metrics;

import opennlp.tools.util.Span;

class AnnotatedCEM implements Comparable<AnnotatedCEM> {
	private String type; // "A": abstract; "T": title
	private Span offset; 
	
	public AnnotatedCEM(String type, Span offset) {
		this.type = type; 
		this.offset = offset; 
	}
	
	public AnnotatedCEM(String type, int startOffset, int endOffset) {
		this(type, new Span(startOffset, endOffset)); 
	}
	
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Span getOffset() {
		return this.offset;
	}

	public void setOffset(Span offset) {
		this.offset = offset;
	}

	@Override
	public String toString() {
		return getType() + ":" + getOffset().getStart() + ":" + getOffset().getEnd();
	}

	@Override
	public int compareTo(AnnotatedCEM another) {
		if (!getType().equalsIgnoreCase(another.getType())) {
			return getType().toUpperCase().compareTo(another.getType().toUpperCase()); 
		}
		
		return getOffset().compareTo(another.getOffset()); 
	}
}

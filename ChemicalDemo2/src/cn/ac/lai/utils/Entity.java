package cn.ac.lai.utils;

import opennlp.tools.util.Span;

class Entity {
	private String fileID; 
	
	private String type; // "T": Training or "A": Harmonized F:"Full"
	private String termID;
	private Span offset; 
	private String entityName; 
	
	// "T": title or "A": abstract
	public Entity(String fileID, String type, Span offset, String entityName) {
		this.fileID = fileID; 
		this.type = type; 
		this.offset = offset; 
		this.entityName = entityName; 
	}
	
	public String getfileID() {
		return this.fileID;
	}

	public void setfileID(String fileID) {
		this.fileID = fileID;
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

	public String getEntityName() {
		return this.entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	@Override
	public int hashCode() {
		int hashfileID = (this.fileID != null? this.fileID.hashCode(): 0); 
		int hashType = (this.type != null? this.type.hashCode(): 0);
		int hashOffset = (this.offset != null? this.offset.hashCode(): 0); 
		
    	return (hashfileID * hashType + hashOffset);
	}
	
	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || !(obj instanceof Entity)) {
			return false; 
		}
		
		if (this == obj) {
			return true; 
		}
		
		Entity other = (Entity) obj;
		
		return this.fileID.equalsIgnoreCase(other.getfileID()) 
			&& this.getType().trim().equalsIgnoreCase(other.getType().trim()) 
			&& this.offset.equals(other.getOffset());
	}
}

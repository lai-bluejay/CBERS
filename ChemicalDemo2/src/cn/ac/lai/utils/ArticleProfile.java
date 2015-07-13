package cn.ac.lai.utils;

public class ArticleProfile {
	private String fileID; 
	private String fileContent; 
	
	public ArticleProfile(String fileID, String fileContent) {
		this.fileID = fileID; 
		this.fileContent = fileContent; 
	}

	public String getfileID() {
		return this.fileID;
	}

	public void setfileID(String fileID) {
		this.fileID = fileID;
	}

	public String getfileContent() {
		return this.fileContent;
	}

	public void setfileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	
	@Override
	public int hashCode() {
    	return (this.fileID != null? this.fileID.hashCode(): 0);
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || !(obj instanceof ArticleProfile)) {
			return false; 
		}
		
		if (this == obj) {
			return true; 
		}
		
		ArticleProfile other = (ArticleProfile) obj;
		
		return this.fileID.trim().equalsIgnoreCase(other.getfileID().trim());
	}

	@Override
	public String toString() {
		return this.fileID.trim() + "\t" + this.fileContent.trim() + "\t" ; 
	}
}

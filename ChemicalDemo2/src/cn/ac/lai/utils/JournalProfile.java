package cn.ac.lai.utils;

public class JournalProfile {
	private String name; 
	private String issn; 
	private double impactFactor; 
	
	public JournalProfile(String name, String issn, double impactFactor) {
		this.name = name; 
		this.issn = issn; 
		this.impactFactor = impactFactor; 
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIssn() {
		return this.issn;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}

	public double getImpactFactor() {
		return this.impactFactor;
	}

	public void setImpactFactor(double impactFactor) {
		this.impactFactor = impactFactor;
	}

	@Override
	public String toString() {
		return this.name + "\t" + this.issn + "\t" + this.impactFactor;
	}
}

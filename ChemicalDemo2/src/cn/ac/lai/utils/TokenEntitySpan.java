package cn.ac.lai.utils;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

public class TokenEntitySpan {
	private String str; 
	
	//下面两个list具有相同的长度
	private List<Span> tokenSpans = null; 
	private List<String> tokenTypes = null; 
	//下面三个list具有相同的长度
	private List<Span> entitySpans = null; 
	private List<String> entityTypes = null; 
	private List<Span> tokenSpansOfEntity = null; 
	
	public TokenEntitySpan(String str, List<Span> tokenSpans) {
		this.str = str; 
		this.tokenSpans = tokenSpans; 
		
		initTokenTypes(); 
		
		this.entityTypes = new ArrayList<String>(); 
		this.tokenSpansOfEntity = new ArrayList<Span>(); 
	}
	
	public TokenEntitySpan(String str, List<Span> tokenSpans, List<Span> entitySpans) {
		this(str, tokenSpans); 
		
		this.entitySpans = entitySpans; 
		this.entityTypes = new ArrayList<String>(this.entitySpans.size()); 
		this.tokenSpansOfEntity = new ArrayList<Span>(this.entitySpans.size()); 
	}
	
	private void initTokenTypes() {
		this.tokenTypes = new ArrayList<String>(); 
		
		for (int i = 0; i < this.tokenSpans.size(); i++) {
			this.tokenTypes.add("O"); 
		}
	}

	public String getStr() {
		return this.str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public List<Span> getTokenSpans() {
		return this.tokenSpans;
	}

	public void setTokenSpans(List<Span> tokenSpans) {
		this.tokenSpans = tokenSpans;
	}

	public List<Span> getEntitySpans() {
		return this.entitySpans;
	}

	public void setEntitySpans(List<Span> entitySpans) {
		this.entitySpans = entitySpans;
	}

	public List<String> getEntityTypes() {
		return this.entityTypes;
	}

	public void setEntityTypes(List<String> entityTypes) {
		this.entityTypes = entityTypes;
	}
	
	public void setEntityTypes(int idx, String label) {
		if ((idx < 0) || (idx >= this.entityTypes.size())) {
			return; 
		}
		
		this.entityTypes.set(idx, label); 
	}
	
	public void addEntityTypes(String label) {
		this.entityTypes.add(label); 
	}
	
	public void addEntityTypes(int idx, String label) {
		this.entityTypes.add(idx, label); 
	}

	public List<Span> getTokenSpansOfEntity() {
		return this.tokenSpansOfEntity;
	}

	public void setTokenSpansOfEntity(List<Span> tokenSpansOfEntity) {
		this.tokenSpansOfEntity = tokenSpansOfEntity;
	}
	
	public void setTokenSpansOfEntity(int idx, Span e) {
		if ((idx < 0) || (idx >= this.tokenSpansOfEntity.size())) {
			return; 
		}
		
		this.tokenSpansOfEntity.set(idx, e); 
	}
	
	public void addTokenSpansOfEntity(Span e) {
		this.tokenSpansOfEntity.add(e); 
	}
	
	public void addTokenSpansOfEntity(int idx, Span e) {
		this.tokenSpansOfEntity.add(idx, e); 
	}

	public List<String> getTokenTypes() {
		return tokenTypes;
	}

	public void setTokenTypes(List<String> tokenTypes) {
		this.tokenTypes = tokenTypes;
	}
	
	public void setTokenTypes(int idxStart, int idxEnd, String label) {
		if ((idxStart < 0) || (idxEnd >= this.tokenTypes.size())) {
			return; 
		}
		
		for (int i = idxStart; i < idxEnd; i++) {
			this.tokenTypes.set(i, label); 
		}
	}
}

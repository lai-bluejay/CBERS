package cn.ac.lai.utils;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

public class TokenSpan {
	private String str; 
	private List<Span> spans; 
	
	public TokenSpan(String str, List<Span> tokenSpans) {
		this.str = str; 
		this.spans = tokenSpans; 
	}

	public String getStr() {
		return this.str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public List<Span> getSpans() {
		return this.spans;
	}

	public void setSpans(ArrayList<Span> spans) {
		this.spans = spans;
	}
}

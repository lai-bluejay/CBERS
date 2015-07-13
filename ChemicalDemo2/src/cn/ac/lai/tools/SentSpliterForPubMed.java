package cn.ac.lai.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.util.Span;
import cn.ac.lai.utils.Util;

public class SentSpliterForPubMed {
	
	static public Span[] spanDetect(String str) {
		Span[] spans = SentSpliter.sentSpanDetect(str); 
		List<Span> newSpans = new ArrayList<Span>(Arrays.asList(spans)); 
		
		for (int i = 0; i < newSpans.size() - 1; ) {
			String currSent = newSpans.get(i).getCoveredText(str).toString();  
			String nextSent = newSpans.get(i+1).getCoveredText(str).toString(); 

			if (currSent.endsWith(",") || Character.isLowerCase(nextSent.charAt(0)) || 
					!Util.isMatch(currSent) || 	currSent.matches("^.+?(var|sp|cv|syn|adv|ex)\\.$") ||
					(currSent.endsWith("HCl.") && nextSent.startsWith("Leu-")) ||
					(!currSent.endsWith(".") && !currSent.endsWith("?") && nextSent.startsWith("["))) {
				Span e = new Span(newSpans.get(i).getStart(), newSpans.get(i+1).getEnd()); 
				newSpans.set(i, e); 
				newSpans.remove(i + 1); 
			} else {
				i++; 
			}
		}
		
		return newSpans.toArray(new Span[newSpans.size()]); 
	}
}

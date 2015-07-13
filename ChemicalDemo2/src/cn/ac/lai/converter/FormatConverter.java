package cn.ac.lai.converter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.util.Span;
import cn.ac.lai.dao.DBConnect;
import cn.ac.lai.dao.TestCorpus;
import cn.ac.lai.dao.TrainCorpus;
import cn.ac.lai.utils.ArticleProfile;
import cn.ac.lai.utils.PredictedEntity;
import cn.ac.lai.utils.Util;

public class FormatConverter {
	private List<CRFToken[]> sents; 
	private TrainCorpus train; 
	private TestCorpus test; 
	private boolean flag = false; 
	
	//@flag: true from train data set, false from test data set
	public FormatConverter(DBConnect dbcon) {	
		this.sents = new ArrayList<CRFToken[]>(); 
		this.train = new TrainCorpus(dbcon); 
		this.test = new TestCorpus(dbcon); 
	}
	
	public FormatConverter(DBConnect dbcon, boolean flag) {
		this(dbcon);
		this.flag = flag; 
	}
	
	public void readResult(String fnameID, String fnameCRFResult, int topN) {
		readIDFile(fnameID); 
		
		readResultFile(fnameCRFResult, topN); 
	}
	
	private void readResultFile(String fname, int topN) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "UTF-8"));
			int idxSent = 0, idxToken = 0;
			int idxTop = 0; 
				
			for (String nextLine; ((nextLine = reader.readLine()) != null); ) {
				if (nextLine.trim().length() == 0) {
					idxToken = 0; 
					if (idxTop == topN - 1) {
						idxSent++;
					}
					continue; 
				} else if (nextLine.trim().matches("^#\\s\\d\\s0.\\d{6}$") || nextLine.trim().matches("^#\\s\\d\\s1.0{6}$")) {
					idxTop = Integer.parseInt(nextLine.trim().split("\\s")[1]); 
					continue; 
				}
				
				String [] parts = nextLine.trim().split("\t"); 
				
				if (idxTop == 0) {
					this.sents.get(idxSent)[idxToken].setName(parts[0]);
				}
				String []tmp = parts[parts.length - 1].split("/"); //last column
				LabelWithProb label = new LabelWithProb(tmp[0], Double.parseDouble(tmp[1])); 
				this.sents.get(idxSent)[idxToken].addLabel(idxTop, label); 
				idxToken++; 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	
	private void readIDFile(String fname) {
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "UTF-8"));
			ArrayList<CRFToken> tokens = new ArrayList<CRFToken>(); 
				
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				if (nextLine.trim().length() == 0) {
					this.sents.add(tokens.toArray(new CRFToken[tokens.size()])); 
					tokens = new ArrayList<CRFToken>(); 
					continue; 
				}
				
				String [] parts = nextLine.trim().split("\t"); 
				
				if (parts.length != 4) {
					throw new IllegalArgumentException("The format of file \"" + fname + "\" is wrong at #line: "+ nLine); 
				}
				
				Span offset = new Span(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
				tokens.add(new CRFToken(parts[0], parts[1], offset)); 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	
	public List<PredictedEntity> getPredictedEntity(int idx) {
		List<PredictedEntity> entities = new ArrayList<PredictedEntity>(); 
		
		for (CRFToken [] tokens: this.sents) {
			
			for (int i = 0; i < tokens.length - 1; i++) {
				LabelWithProb currLabel = tokens[i].getLabel().get(idx); 
				Boolean isEqual=currLabel.getLabel().equals("O") || currLabel.getLabel().endsWith("-I") || currLabel.getLabel().endsWith("-E");
				if (isEqual) {
					continue; 
				}
				
				int idxStart = i; 
				
				for (int j = i + 1; j < tokens.length; j++) {
					LabelWithProb nextLabel = tokens[j].getLabel().get(idx); 
					
					int idxEnd; 
					if (nextLabel.getLabel().endsWith("-E")) {
						idxEnd = j; 
					} else if (nextLabel.getLabel().equals("O")) {
						idxEnd = j - 1; 
					} else {
						continue; 
					}
									
					Span offset = new Span(tokens[idxStart].getOffset().getStart(), tokens[idxEnd].getOffset().getEnd()); 
					
					double confidence = 0.0; 
					for (int k = idxStart; k <= idxEnd; k++) {
						confidence += tokens[k].getLabel().get(idx).getProb(); 
					}
					confidence /= idxEnd - idxStart + 1; 
					
					ArticleProfile article = this.flag? 
							this.train.getArticle(tokens[idxStart].getfileID(), true): 
							this.test.getArticle(tokens[idxStart].getfileID(), true); 
					System.out.println(article); 
					String entityName = offset.getCoveredText(article.getfileContent()).toString();
					
					PredictedEntity e = new PredictedEntity(tokens[idxStart].getfileID(), tokens[idxStart].getType(), offset, entityName, confidence); 
					entities.add(e); 
					
					break; 
				}
			}
		}
		
		return entities; 
	}
	
	public List<PredictedEntity> getPredictedEntity() {
		return getPredictedEntity(0); 
	}
	
	//flag: true(train data set), false (test data set)
	public PredictedEntity postprocess(PredictedEntity e, String leftStr, String rightStr, boolean flag) {
		Map<String, ArticleProfile> article = flag?
				this.train.getArticle(e.getfileID()): 
				this.test.getArticle(e.getfileID()); 
				
		Map<String, String> source = new HashMap<String, String>(); 
		if (e.getType().equalsIgnoreCase("T")) { // fileContent
			source.put("T", article.get("T").getfileContent()); 
			source.put("F", article.get("F").getfileContent()); 
		} else { // abstract
/*			source.put("T", article.get("T").getAbs()); 
			source.put("F", article.get("F").getAbs()); */
		}
		
		String entityName = e.getOffset().getCoveredText(source.get("T")).toString(); 
		
		if (Util.countOccurrenceOf(entityName, leftStr) == Util.countOccurrenceOf(entityName, rightStr) + 1) { 
			// #leftStr=#rightStr+1, right is rightStr
			
			int beginIndex = e.getOffset().getEnd(); 
			int endIndex = beginIndex + rightStr.length();
			if (e.getOffset().getEnd() < source.get("T").length() - rightStr.length() 
					&& source.get("T").substring(beginIndex, endIndex).equalsIgnoreCase(rightStr)) {
				//System.out.println("(1)left: " + leftStr + "\t" + "right: " + rightStr); 
				//System.out.println("(1)pre: " + e); 
				
				e.setOffset(new Span(e.getOffset().getStart(), endIndex));
				e.setEntityName(e.getOffset().getCoveredText(source.get("F")).toString()); 
				
				//System.out.println("(1)pos: " + e); 
			} else {
				return null; 
			}
		} else if (Util.countOccurrenceOf(entityName, leftStr) == Util.countOccurrenceOf(entityName, rightStr) - 1) { 
			// #leftStr=#rightStr-1, left is rightStr
			
			int endIndex = e.getOffset().getStart(); 
			int beginIndex = endIndex - leftStr.length();
			if (e.getOffset().getStart() > leftStr.length() - 1 
					&& source.get("T").substring(beginIndex, endIndex).equalsIgnoreCase(leftStr)) {
				//System.out.println("(2)left: " + leftStr + "\t" + "right: " + rightStr); 
				//System.out.println("(2)pre: " + e); 
				
				e.setOffset(new Span(beginIndex, e.getOffset().getEnd()));
				e.setEntityName(e.getOffset().getCoveredText(source.get("F")).toString()); 
				
				//System.out.println("(2)pos: " + e);
			} else {
				return null; 
			}
		} else if (Util.countOccurrenceOf(entityName, leftStr) == Util.countOccurrenceOf(entityName, rightStr)) {
			; 
		} else {
			return null; 
		}
		
		return e; 
	}
	
	//flag: true(train data set), false (test data set)
	public PredictedEntity postprocess(PredictedEntity e, boolean flag) {
		String[] leftStr = {"(", "[", "{", "<sc>", "<i>", "<sup>", "<sub>"}; 
		String[] rightStr = {")", "]", "}", "</sc>", "</i>", "</sup>", "</sub>"}; 
		
		for (int j = 0; j < leftStr.length; j++) {
			e = postprocess(e, leftStr[j], rightStr[j], flag); 
			if (e == null) {
				return null; 
			}
		}
		
		return e; 
	}
	
	public void printCEMFormat(PrintStream out, List<PredictedEntity> entities) {
		printCEMFormat(out, entities, false); 
	}
	
	public void printCEMFormat(PrintStream out, List<PredictedEntity> entities, boolean showEntityName) {		
		Collections.sort(entities); 
		int rank = 1; 
		
		// print the first one
		out.print(entities.get(0) + "\t" + rank + "\t" 
				+ String.format("%.6f", entities.get(0).getConfidence()));
		if (showEntityName) {
			out.println("\t" + entities.get(0).getEntityName()); 
		} else {
			out.println(); 
		}
		
		// print others
		for (int i = 1; i < entities.size(); i++) {
			String prevID = entities.get(i - 1).getfileID(); 
			String currID = entities.get(i).getfileID(); 

			if (currID.equals(prevID)) {
				rank++; 
			} else {
				rank = 1; 
			}
			
			out.print(entities.get(i) 
					+ "\t" + rank 
					+ "\t" + String.format("%.6f", entities.get(i).getConfidence())); 
			if (showEntityName) {
				out.println("\t" + entities.get(i).getEntityName()); 
			} else {
				out.println(); 
			}
		}
	}
	
	public void printID(PrintStream out) {
		for (CRFToken[] sent: this.sents) {
			for (CRFToken token: sent) {
				out.println(token.getfileID()  
						+ "\t" + token.getType() 
						+ "\t" + token.getOffset().getStart() 
						+ "\t" + token.getOffset().getEnd()); 
			}
			out.println(); 
		}
	}
	
	public void printResult(PrintStream out) {
		for (CRFToken[] sent: this.sents) {
			for (CRFToken token: sent) {
				out.print(token.getfileID() 
						+ "\t" + token.getType() 
						+ "\t" + token.getOffset().getStart()
						+ "\t" + token.getOffset().getEnd() 
						+ "\t" + token.getName());
				
				for (LabelWithProb label: token.getLabel()) {
					out.print("\t" + label); 
				}
				
				out.println(); 
			}
			out.println(); 
		}
	}
	
	public void printResult(PrintStream out, int idx) {
		for (CRFToken[] sent: this.sents) {
			for (CRFToken token: sent) {
				out.println(token.getfileID() 
						+ "\t" + token.getType() 
						+ "\t" + token.getOffset().getStart() 
						+ "\t" + token.getOffset().getEnd() 
						+ "\t" + token.getName()
						+ "\t" + token.getLabel(idx));
			}
			out.println(); 
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		DBConnect dbcon = new DBConnect(); 
		dbcon.openDB();
		
		FormatConverter converter = new FormatConverter(dbcon); 
		/*
		converter.readResult("data/test/BioCreative.test-id", "data/test/BioCreative0.out", 2); 
		List<PredictedEntity> entities = converter.getPredictedEntity(); 
		entities = converter.postprocess(entities, false); 
		converter.printCEMFormat(new PrintStream("data/test/entities-tmp.txt"), entities, true); 
		*/
		
		PredictedEntity e = new PredictedEntity("23611338", "A", new Span(680, 735), "o</i>-, <i>m</i>-, and <i>p</i>-MitoPhB(OH)<sub>2</sub>", 1.0); 
		System.out.println(e); 
		System.out.println(converter.postprocess(e, "<i>", "</i>", true)); 
		
		System.out.println("done."); 
		dbcon.closePSTMT(); 
	}
}

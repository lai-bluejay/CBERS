package cn.ac.lai.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.util.Span;
import cn.ac.lai.utils.AnnotatedEntity;
import cn.ac.lai.utils.ArticleProfile;
import cn.ac.lai.utils.Dictionary;
import cn.ac.lai.utils.TextPolicy;
import cn.ac.lai.utils.TokenEntitySpan;
import cn.ac.lai.utils.TokenSpan;
import cn.ac.lai.utils.Util;

abstract class Corpus {
	protected DBConnect dbcon; 
	protected String tableName; 
	
	public Corpus(DBConnect dbcon, String tableName) {
		this.dbcon = dbcon; 
		this.tableName = tableName; 
	}
	
	abstract void check(String type); 
	
	public String insert(ArticleProfile article, String type) {
		check(type); 
		
		final String sql = "INSERT INTO " + this.tableName + " (fileID, fileContent, type) VALUES (?, ?, ?)"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			int col = 1; 
			pstmt.setString(col++, article.getfileID().trim());
			pstmt.setString(col++, article.getfileContent().trim()); 
			//pstmt.setString(col++, article.getAbs().trim()); 
			pstmt.setString(col++, type); 
			pstmt.executeUpdate(); 
			
			pstmt.close(); 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}

		return article.getfileID(); 
	}
	
	public String insert(String fileID, String fileContent, String type) {
		check(type); 
		
		ArticleProfile article = new ArticleProfile(fileID, fileContent);
		
		return insert(article, type); 
	}
	
	public void load(String fname, String type) {
		check(type); 
		
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "UTF-8"));
			
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				String [] parts = nextLine.trim().split("\t"); 
				String restParts=nextLine.substring(parts[0].length()).trim();
				/*if (parts.length != 3) {
					throw new IllegalArgumentException("The format of file \"" + fname + "\" is wrong at #line: "+ nLine); 
				}*/
				
				insert(parts[0], restParts, type); 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public boolean contains(String fileID) {
		boolean ret = false; 
		
		final String sql = "SELECT * FROM " + this.tableName + " WHERE UPPER(TRIM(fileID)) = ?"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setString(1, fileID.trim().toUpperCase());

			ResultSet rs = pstmt.executeQuery(); 
			if (rs.next()) {
				ret = true; 
			} 
			
			pstmt.close(); 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return ret; 
	}
	
	public boolean contains(String fileID, String type) {
		check(type); 
		
		boolean ret = false; 
		
		final String sql = "SELECT * FROM " + this.tableName + " WHERE UPPER(TRIM(fileID)) = ? AND UPPER(TRIM(type)) = ?"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setString(1, fileID.trim().toUpperCase());
			pstmt.setString(2, type.trim().toUpperCase());

			ResultSet rs = pstmt.executeQuery(); 
			if (rs.next()) {
				ret = true; 
			} 
			
			pstmt.close(); 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return ret; 
	}
	
	public void updateType(String fileID, String type) {
		check(type); 
		
		final String sql = "UPDATE " + this.tableName + " SET type = ? WHERE UPPER(TRIM(fileID)) = ?"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setString(1, type.trim());
			pstmt.setString(2, fileID.trim().toUpperCase());
			pstmt.executeUpdate(); 
			
			pstmt.close(); 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public void updateType(String fname) {
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "UTF-8"));
			
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				String [] parts = nextLine.trim().split("\t"); 
				String restParts=nextLine.substring(parts[0].length()).trim();
				/*if (parts.length != 2) {
					throw new IllegalArgumentException("The format of file \"" + fname + "\" is wrong at #line: "+ nLine); 
				}
				*/
				updateType(parts[0], restParts);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public String[] getAllfileID() {
		List<String> ids = new ArrayList<String>(); 
		
		final String sql = "SELECT fileID FROM " + this.tableName;
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				ids.add(rs.getString("fileID")); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return ids.toArray(new String[ids.size()]); 
	}
	
	public String[] getAllfileID(String type) {
		check(type); 
		
		List<String> ids = new ArrayList<String>(); 
		
		final String sql = "SELECT fileID FROM " + this.tableName + " WHERE UPPER(TRIM(type)) = ?";
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setString(1, type.trim().toUpperCase());
			
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				ids.add(rs.getString("fileID")); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return ids.toArray(new String[ids.size()]); 
	}
	
	public ArticleProfile getArticle(String fileID, boolean isPreprocessing) {
		ArticleProfile article = null;
		
		final String sql = "SELECT fileContent FROM " + this.tableName + " WHERE UPPER(TRIM(fileID)) = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setString(1, fileID.trim().toUpperCase());
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				if (isPreprocessing) {
					article = new ArticleProfile(fileID.trim().toUpperCase(), 
							rs.getString("fileContent").trim()); 
				} else {
					article = new ArticleProfile(fileID.trim().toUpperCase(), 
							Util.merge(rs.getString("fileContent").trim())); 
				}
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return article; 
	}
	
	//"F": not pre-processing; "T": pre-processing
	public Map<String, ArticleProfile> getArticle(String fileID) {
		Map<String, ArticleProfile> article = new HashMap<String, ArticleProfile>(); 
		
		ArticleProfile a1 = getArticle(fileID, false); 
		ArticleProfile a2 = new ArticleProfile(a1.getfileID(), Util.merge(a1.getfileContent())); 
		
		article.put("F", a1); 
		article.put("T", a2); 
		
		return article; 
	}
	
	abstract List<AnnotatedEntity> getEntity(String fileID, TextPolicy policy); 
	
	public int size() {
		int count = 0; 
		
		final String sql = "SELECT count(*) FROM " + this.tableName; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {		
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return count; 
	}
	
	public int size(String type) {
		check(type); 
		
		int count = 0; 
		
		final String sql = "SELECT count(*) FROM " + this.tableName + "WHERE UPPER(TRIM(type)) = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setString(1, type.trim().toUpperCase());
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return count; 
	}
	
	public Dictionary printTokenWithBrownFormat(PrintStream out) {
		Dictionary dict = new Dictionary();
		
		String[] ids = getAllfileID(); 
		for (String id: ids) {
			Map<String, ArticleProfile> doc = getArticle(id); 
			
			//fileContent
			List<TokenSpan> fileContentList = Util.getTokenSpan(doc.get("T").getfileContent()); 
			for (TokenSpan tokenSpan: fileContentList) {
				
				for (Span s: tokenSpan.getSpans()) {
					String token = s.getCoveredText(doc.get("T").getfileContent()).toString(); 
					out.print(token + " "); 

					dict.add(token); 
				}
			}
			
		/*	//abstract
			List<TokenSpan> absList = Util.getTokenSpan(doc.get("T").getAbs()); 
			for (TokenSpan tokenSpan: absList) {
				for (Span s: tokenSpan.getSpans()) {
					String token = s.getCoveredText(doc.get("T").getAbs()).toString(); 
					out.print(token + " "); 
					
					dict.add(token); 
				}
			}*/
			
			out.println(); 
		}
		
		return dict; 
	}
	
	public Dictionary printTokenEntity(PrintStream outToken, PrintStream outEntity) {
		Dictionary dict = new Dictionary();
		
		String[] ids = getAllfileID(); 
		for (String id: ids) {
			Map<String, ArticleProfile> doc = getArticle(id); 
			
			outToken.println(id); 
			
			//fileContent
			List<TokenEntitySpan> fileContentList = Util.getTokenEntitySpan(doc.get("T").getfileContent(), getEntity(id, TextPolicy.TRAINING)); 
			for (int i = 0; i < fileContentList.size(); i++) {
				outToken.println("\t" + i + ": " + fileContentList.get(i).getStr());
				
				List<Span> tokenSpans = fileContentList.get(i).getTokenSpans(); 
				for (int j = 0; j < tokenSpans.size(); j++) {
					String token = tokenSpans.get(j).getCoveredText(doc.get("F").getfileContent()).toString(); 
					outToken.println("\t\t" + j + ": " + tokenSpans.get(j).getStart() + 
							":" + tokenSpans.get(j).getEnd() + ":" + token); 
					
					token = tokenSpans.get(j).getCoveredText(doc.get("T").getfileContent()).toString(); 
					dict.add(token); 
				}
				
				List<Span> entitySpans = fileContentList.get(i).getEntitySpans(); 
				for (int j = 0; j < entitySpans.size(); j++) {
					outEntity.println(id + "\tT:" + entitySpans.get(j).getStart() + 
							":" + entitySpans.get(j).getEnd() + 
							":" + entitySpans.get(j).getCoveredText(doc.get("F").getfileContent())); 
				}
			}
			
			/*//abstract
			List<TokenEntitySpan> absList = Util.getTokenEntitySpan(doc.get("T").getAbs(), getEntity(id, TextPolicy.ABSTRACT)); 
			for (int i = 0; i < absList.size(); i++) {
				outToken.println("\t" + i + ": " + absList.get(i).getStr());
				
				List<Span> tokenSpans = absList.get(i).getTokenSpans();
				for (int j = 0; j < tokenSpans.size(); j++) {
					String token = tokenSpans.get(j).getCoveredText(doc.get("F").getAbs()).toString(); 
					outToken.println("\t\t" + j + ": " + tokenSpans.get(j).getStart() + 
							":" + tokenSpans.get(j).getEnd() + ":" + token); 
					
					token = tokenSpans.get(j).getCoveredText(doc.get("T").getAbs()).toString(); 
					dict.add(token); 
				}
				
				List<Span> entitySpans = absList.get(i).getEntitySpans(); 
				for (int j = 0; j < entitySpans.size(); j++) {
					outEntity.println(id + "\tA:" + entitySpans.get(j).getStart() + 
							":" + entitySpans.get(j).getEnd() + 
							":" + entitySpans.get(j).getCoveredText(doc.get("F").getAbs())); 
				}
			}*/
		}
		
		return dict; 
	}
	
	protected void printFeatures(String[] ids, PrintStream outInst, PrintStream outID, WordRepresentation brown) {
		for (String id: ids) { 
			ArticleProfile doc = getArticle(id, true); 
			
			//fileContent
			List<TokenEntitySpan> fileContentList = Util.getTokenEntitySpan(doc.getfileContent(), getEntity(id, TextPolicy.TRAINING)); 
			for (TokenEntitySpan tes: fileContentList) {
				
				List<Span> tokenSpans = tes.getTokenSpans(); 
				List<String> tokenTypes = tes.getTokenTypes(); 
				for (int j = 0; j < tokenSpans.size(); j++) {
					String token = tokenSpans.get(j).getCoveredText(doc.getfileContent()).toString(); 
					if (brown == null) {
						outInst.println(Util.toStringFromInstance(token) + "\t" + tokenTypes.get(j)); 
					} else {
						outInst.println(Util.toStringFromInstance(token) + "\t" + brown.get(token) + "\t" + tokenTypes.get(j)); 
					}
					outID.println(Util.toStringFromID(id, tokenSpans.get(j), "T")); 
				}
				
				outInst.println();
				outID.println(); 
			}
			
			/*//abstract
			List<TokenEntitySpan> absList = Util.getTokenEntitySpan(doc.getAbs(), getEntity(id, TextPolicy.ABSTRACT)); 
			for (TokenEntitySpan tes: absList) {
				
				List<Span> tokenSpans = tes.getTokenSpans();
				List<String> tokenTypes = tes.getTokenTypes(); 
				for (int j = 0; j < tokenSpans.size(); j++) {
					String token = tokenSpans.get(j).getCoveredText(doc.getAbs()).toString(); 
					if (brown == null) {
						outInst.println(Util.toStringFromInstance(token) + "\t" + tokenTypes.get(j));
					} else {
						outInst.println(Util.toStringFromInstance(token) + "\t" + brown.get(token) + "\t" + tokenTypes.get(j));
					}
					outID.println(Util.toStringFromID(id, tokenSpans.get(j), "A")); 
				}
				
				outInst.println(); 
				outID.println(); 
			}*/
		}
	}
	
	public void printFeatures(PrintStream outInst, PrintStream outID, WordRepresentation brown) {
		printFeatures(getAllfileID(), outInst, outID, brown); 
	}
	
	public void printFeatures(PrintStream outInst, PrintStream outID, WordRepresentation brown, String type) {
		printFeatures(getAllfileID(type), outInst, outID, brown); 
	}
}

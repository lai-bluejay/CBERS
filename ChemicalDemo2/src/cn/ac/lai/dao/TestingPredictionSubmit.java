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
import java.sql.Statement;

import opennlp.tools.util.Span;
import cn.ac.lai.tools.ConfusionMatrix;

public class TestingPredictionSubmit {
	private DBConnect dbcon; 
	private final String tableName = "testing_prediction_submit"; 
	
	public TestingPredictionSubmit(DBConnect dbcon) {
		this.dbcon = dbcon; 
	}
	
	public int insert(String PMID, String type, Span offset, int rank, double score, int run) {
		int id = -1; 
		
		final String sql = "INSERT INTO " + this.tableName + " (pmid, type, start_offset, end_offset, rank, score, run) VALUES (?, ?, ?, ?, ?, ?, ?)"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS); 
		try {		
			int col = 1; 
			pstmt.setString(col++, PMID.trim());
			pstmt.setString(col++, type.trim().toUpperCase()); 
			pstmt.setInt(col++, offset.getStart()); 
			pstmt.setInt(col++, offset.getEnd()); 
			pstmt.setInt(col++, rank); 
			pstmt.setDouble(col++, score);
			pstmt.setInt(col++, run);
			pstmt.executeUpdate(); 
			
			ResultSet rsKey = pstmt.getGeneratedKeys(); 
			rsKey.next(); 
			id = rsKey.getInt(1); 
			pstmt.close(); 
		} catch (SQLException e) {
			System.out.println(PMID); 
			e.printStackTrace(); 
		}
		
		return id; 
	}
	
	public void load(String fname, int run) {
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "UTF-8"));
			
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				String [] parts = nextLine.trim().split("[\t|:]"); 
				
				if (parts.length != 6) {
					throw new IllegalArgumentException("The format of file \"" + fname + "\" is wrong at #line: "+ nLine); 
				}
				
				Span offset = new Span(Integer.parseInt(parts[2]), Integer.parseInt(parts[3])); 
				insert(parts[0], parts[1], offset, Integer.parseInt(parts[4]), Double.parseDouble(parts[5]), run); 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
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
	
	void print(PrintStream out) {
		final String sql = "SELECT pmid, type, start_offset, end_offset, rank, score FROM " + this.tableName 
			+ " ORDER BY pmid asc, type asc, rank asc";
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				out.println(rs.getString("pmid") + "\t" + rs.getString("type")
					+ ":" + rs.getInt("start_offset") + ":" + rs.getInt("end_offset") 
					+ "\t" + rs.getInt("rank") + "\t" + rs.getDouble("socre")); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public int getTP(int run, int classID) {
		int count = 0; 
		final String sql = "SELECT COUNT(*) FROM " + this.tableName + " p, testing_annotation a "
			+ "WHERE p.pmid = a.pmid AND p.type = a.type AND p.start_offset = a.start_offset AND " 
			+ "p.end_offset = a.end_offset AND p.run = ? AND a.cid = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, run); 
			pstmt.setInt(2, classID); 
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
	
	private int getTP(int run) {
		int count = 0; 
		final String sql = "SELECT COUNT(*) FROM " + this.tableName + " p, testing_annotation a "
			+ "WHERE p.pmid = a.pmid AND p.type = a.type AND p.start_offset = a.start_offset AND " 
			+ "p.end_offset = a.end_offset AND p.run = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, run); 
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
	
	private int getFP(int run) {
		int count = 0; 
		final String sql = "SELECT COUNT(*) FROM " + this.tableName + " WHERE NOT id IN ("
			+ "SELECT p.id FROM " + this.tableName + " p, testing_annotation a WHERE "
			+ "p.pmid = a.pmid AND p.type = a.type AND p.start_offset = a.start_offset AND "
			+ "p.end_offset = a.end_offset AND p.run = ?) AND run = ? AND pmid IN ("
			+ "SELECT pmid FROM testing_annotation)"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, run); 
			pstmt.setInt(2, run); 
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

	public ConfusionMatrix getConfusionMatrix(int run) {
		TestAnnotation test = new TestAnnotation(this.dbcon); 
		
		int TP = getTP(run); 
		int FP = getFP(run); 
		int FN = test.size("Y") - TP; 
		
		return new ConfusionMatrix(TP, 0, FP, FN);
	}

	public static void main(String[] args) throws FileNotFoundException {
		DBConnect dbcon = new DBConnect(); 
		dbcon.openDB();
		
		TestingPredictionSubmit submit = new TestingPredictionSubmit(dbcon); 
		
		if (submit.size() == 0) {
			for (int i = 1; i <= 5; i++) {
				System.out.println(i); 
				
				String fname = "data/test/team_259_cem_run" + i + ".txt";
				submit.load(fname, i); 
			}
		}
		
		for (int run = 1; run <= 5; run++) {
			for (int i = 1; i < 8; i++) {
				//System.out.println(submit.getConfusionMatrix(run)); 
				System.out.println(submit.getTP(run, i)); 
			}
			System.out.println(submit.getTP(run, 132));
			System.out.println(); 
		}
		
		System.out.println("done."); 
		dbcon.closePSTMT(); 
	}
}

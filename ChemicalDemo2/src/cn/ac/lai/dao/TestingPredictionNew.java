package cn.ac.lai.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import opennlp.tools.util.Span;
import cn.ac.lai.converter.FormatConverter;
import cn.ac.lai.tools.ConfusionMatrix;
import cn.ac.lai.utils.PredictedEntity;

public class TestingPredictionNew {
	private DBConnect dbcon; 
	private final String tableName = "testing_prediction_new"; 
	
	public TestingPredictionNew(DBConnect dbcon) {
		this.dbcon = dbcon; 
		
		if (size() == 0) {
			String[] cases = {"without", "500", "1000", "1500", "2000"};
			for (String cs: cases) {
				load(cs); 
			}
		}
	}
	
	public int insert(String fileID, String type, Span offset, double score, double cost, int caseID, boolean isPost) {
		int id = -1; 
		
		final String sql = "INSERT INTO " + this.tableName + " (fileID, type, start_offset, end_offset, score, cost, case_id, is_postprocessing)"
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS); 
		try {		
			int col = 1; 
			pstmt.setString(col++, fileID.trim());
			pstmt.setString(col++, type.trim().toUpperCase()); 
			pstmt.setInt(col++, offset.getStart()); 
			pstmt.setInt(col++, offset.getEnd()); 
			pstmt.setDouble(col++, score);
			pstmt.setDouble(col++, cost);
			pstmt.setInt(col++, caseID);
			pstmt.setBoolean(col++, isPost); 
			pstmt.executeUpdate(); 
			
			ResultSet rsKey = pstmt.getGeneratedKeys(); 
			rsKey.next(); 
			id = rsKey.getInt(1); 
			pstmt.close(); 
		} catch (SQLException e) {
			System.out.println(fileID); 
			e.printStackTrace(); 
		}
		
		return id; 
	}
	
	public void load(String cs) {
		int[] costs = {-3, -2, -1, 0, 1, 2, 3}; 
		ExperimentCase cases = new ExperimentCase(this.dbcon); 
		int caseID = cases.getID(cs); 
		
		for (int c: costs) {
			String fnameID = "data/crf/BioCreative-" + cs + ".test-id"; 
			String fnameOut = "data/crf/BioCreative-" + cs + "-" + c + ".out";
			
			FormatConverter converter = new FormatConverter(this.dbcon, false); 
			converter.readResult(fnameID, fnameOut, 1); 
			List<PredictedEntity> entities = converter.getPredictedEntity(); 

			for (int i = 0; i < entities.size(); i++) {
				insert(entities.get(i).getfileID(), entities.get(i).getType(), entities.get(i).getOffset(), 
						entities.get(i).getConfidence(), c, caseID, false); 
				
				PredictedEntity e = converter.postprocess(entities.get(i), false); 
				if (e != null) {
					insert(e.getfileID(), e.getType(), e.getOffset(), e.getConfidence(), c, caseID, true); 
				}
			}
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
	
	public int getTP(double cost, int caseID, int classID, boolean isPost) {
		int count = 0; 
		final String sql = "SELECT COUNT(*) FROM " + this.tableName + " p, testing_annotation a "
			+ "WHERE p.fileID = a.fileID AND p.type = a.type AND p.start_offset = a.start_offset AND " 
			+ "p.end_offset = a.end_offset AND p.cost = ? AND p.case_id = ? AND a.cid = ? AND p.is_postprocessing = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			int col = 1; 
			pstmt.setDouble(col++, cost);
			pstmt.setInt(col++, caseID); 
			pstmt.setInt(col++, classID); 
			pstmt.setBoolean(col++, isPost); 
			
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
	
	private int getTP(double cost, int caseID, boolean isPost) {
		int count = 0; 
		final String sql = "SELECT COUNT(*) FROM " + this.tableName + " p, testing_annotation a "
			+ "WHERE p.fileID = a.fileID AND p.type = a.type AND p.start_offset = a.start_offset AND " 
			+ "p.end_offset = a.end_offset AND p.cost = ? AND p.case_id = ? AND p.is_postprocessing = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			int col = 1; 
			pstmt.setDouble(col++, cost);
			pstmt.setInt(col++, caseID); 
			pstmt.setBoolean(col++, isPost); 
			
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
	
	private int getTP(double cost, int caseID) {
		return getTP(cost, caseID, true); 
	}
	
	private int getFP(double cost, int caseID, boolean isPost) {
		int count = 0; 
		final String sql = "SELECT COUNT(*) FROM " + this.tableName + " WHERE NOT id IN ("
			+ "SELECT p.id FROM " + this.tableName + " p, testing_annotation a WHERE "
			+ "p.fileID = a.fileID AND p.type = a.type AND p.start_offset = a.start_offset AND "
			+ "p.end_offset = a.end_offset AND p.cost = ? AND p.case_id = ? AND p.is_postprocessing = ?) "
			+ "AND cost = ? AND case_id = ? AND is_postprocessing = ? AND fileID IN ("
			+ "SELECT fileID FROM testing_annotation)"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			int col = 1; 
			pstmt.setDouble(col++, cost); 
			pstmt.setInt(col++, caseID); 
			pstmt.setBoolean(col++, isPost); 
			pstmt.setDouble(col++, cost); 
			pstmt.setInt(col++, caseID); 
			pstmt.setBoolean(col++, isPost); 
			
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
	
	private int getFP(double cost, int caseID) {
		return getFP(cost, caseID, true); 
	}
	
	public ConfusionMatrix getConfusionMatrix(double cost, int caseID, boolean isPost) {
		TestAnnotation test = new TestAnnotation(this.dbcon); 
		
		int TP = getTP(cost, caseID, isPost); 
		int FP = getFP(cost, caseID, isPost); 
		int FN = test.size("Y") - TP; 
		
		return new ConfusionMatrix(TP, 0, FP, FN);
	}
	
	public ConfusionMatrix getConfusionMatrix(double cost, int caseID) {
		TestAnnotation test = new TestAnnotation(this.dbcon); 
		
		int TP = getTP(cost, caseID); 
		int FP = getFP(cost, caseID); 
		int FN = test.size("Y") - TP; 
		
		return new ConfusionMatrix(TP, 0, FP, FN); 
	}
	
	public static void main(String[] args) {
		DBConnect dbcon = new DBConnect(); 
		dbcon.openDB();
		
		TestingPredictionNew prediction = new TestingPredictionNew(dbcon); 
		for (int i = 1; i < 8; i++) {
			System.out.println(prediction.getTP(0, 5, i, true));
		}
		System.out.println(prediction.getTP(0, 5, 132, true));
		
		System.out.println("done."); 
		dbcon.closePSTMT(); 
	}
}

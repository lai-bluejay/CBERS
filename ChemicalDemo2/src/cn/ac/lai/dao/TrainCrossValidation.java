package cn.ac.lai.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import opennlp.tools.util.Span;
import cn.ac.lai.converter.FormatConverter;
import cn.ac.lai.utils.PredictedEntity;

public class TrainCrossValidation {
	private DBConnect dbcon; 
	private final String tableName = "training_cross_validation";
	private int fold; 
	
	public TrainCrossValidation(DBConnect dbcon, int fold) {
		this.dbcon = dbcon; 
		this.fold = fold; 
		
		if (size() == 0) {
			String[] cases = {"without", "500", "1000", "1500", "2000"};
			//String[] cases = {"without"}; 
			for (String cs: cases) {
				load(cs); 
			}
		}
	}
	
	public int insert(String fileID, String type, Span offset, double score, int fold, double cost, int caseID, boolean isPost) {
		int id = -1; 
		
		final String sql = "INSERT INTO " + this.tableName + " (fileID, type, start_offset, end_offset, score, fold, cost, case_id, is_postprocessing)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS); 
		try {		
			int col = 1; 
			pstmt.setString(col++, fileID.trim().toUpperCase());
			pstmt.setString(col++, type.trim().toUpperCase()); 
			pstmt.setInt(col++, offset.getStart()); 
			pstmt.setInt(col++, offset.getEnd()); 
			pstmt.setDouble(col++, score);
			pstmt.setInt(col++, fold);
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
			
			for (int v = 0; v < this.fold; v++) {
				String fnameID = "data/crf/cv/" + cs + "/" + v + ".test-id"; 
				String fnameOut = "data/crf/cv/" + cs + "/" + c + "/" + v + ".out"; 
				
				FormatConverter converter = new FormatConverter(this.dbcon, true); 
				converter.readResult(fnameID, fnameOut, 1); 
				List<PredictedEntity> entities = converter.getPredictedEntity(); 

				for (int i = 0; i < entities.size(); i++) {
					insert(entities.get(i).getfileID(), entities.get(i).getType(), entities.get(i).getOffset(), 
							entities.get(i).getConfidence(), v, c, caseID, false); 
					
					PredictedEntity e = converter.postprocess(entities.get(i), true); 
					if (e != null) {
						insert(e.getfileID(), e.getType(), e.getOffset(), e.getConfidence(), v, c, caseID, true); 
					}
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
	
	private int getTP(int run) {
		int count = 0; 
		final String sql = "SELECT COUNT(*) FROM " + this.tableName + " p, training_annotation a "
			+ "WHERE p.fileID = a.fileID AND p.type = a.type AND p.start_offset = a.start_offset AND " 
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
	
	public static void main(String[] args) {
		DBConnect dbcon = new DBConnect(); 
		dbcon.openDB();
		
		int fold = 10; 
		TrainCrossValidation cross = new TrainCrossValidation(dbcon, fold); 
		
		System.out.println("done."); 
		dbcon.closePSTMT(); 
	}
}

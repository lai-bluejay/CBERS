package cn.ac.lai.dao;

import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract public class TestingPrediction {
	protected DBConnect dbcon; 
	protected String tableName; 
	
	public TestingPrediction(DBConnect dbcon, String tableName) {
		this.dbcon = dbcon; 
		this.tableName = tableName; 
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
	
	abstract void print(PrintStream out); 
}

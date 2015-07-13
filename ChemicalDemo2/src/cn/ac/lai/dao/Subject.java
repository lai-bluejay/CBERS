package cn.ac.lai.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Subject {
	private DBConnect dbcon; 
	private final String tableName = "subject";
	
	public Subject(DBConnect dbcon) {
		this.dbcon = dbcon; 
	}
	
	public int getID(String name) {
		int id = -1; 
		final String sql = "SELECT id FROM " + this.tableName + " WHERE UPPER(TRIM(name)) = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setString(1, name.trim().toUpperCase()); 
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				id = rs.getInt(1); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return id; 
	}
	
	public String getName(int id) {
		String name = null; 
		final String sql = "SELECT name FROM " + this.tableName + " WHERE id = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, id); 
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				name = rs.getString(1); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return name; 
	}
	
	public List<Integer> getAllID() {
		List<Integer> list = new ArrayList<Integer>(); 
		
		final String sql = "SELECT id FROM " + this.tableName; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				list.add(rs.getInt(1)); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return list; 
	}
}

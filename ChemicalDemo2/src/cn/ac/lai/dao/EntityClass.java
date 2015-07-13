package cn.ac.lai.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EntityClass {
	private DBConnect dbcon; 
	private final String tableName = "entity_class"; 
	
	public EntityClass(DBConnect dbcon) {
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
	
	public int insert(String name) {
		int id = getID(name);
		if (id != -1) {
			return id; 
		}
		
		final String sql = "INSERT INTO " + this.tableName + " (name) VALUES (?)"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS); 
		try {
			pstmt.setString(1, name.trim().toUpperCase());
			pstmt.executeUpdate(); 
			
			ResultSet rsKey = pstmt.getGeneratedKeys(); 
			rsKey.next(); 
			id = rsKey.getInt(1); 
			pstmt.close(); 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return id; 
	}
}

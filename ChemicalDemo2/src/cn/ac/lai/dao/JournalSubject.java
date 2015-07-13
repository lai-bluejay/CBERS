package cn.ac.lai.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JournalSubject {
	private DBConnect dbcon; 
	private final String tableName = "journal_subject";
	
	public JournalSubject(DBConnect dbcon) {
		this.dbcon = dbcon; 
	}
	
	public int getID(int jid, int sid) {
		int id = -1; 
		final String sql = "SELECT id FROM " + this.tableName + " WHERE jid = ? and sid = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, jid); 
			pstmt.setInt(2, sid); 
			
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
	
	public int insert(int jid, int sid) {
		int id = getID(jid, sid);
		if (id != -1) {
			return id; 
		}
		
		final String sql = "INSERT INTO " + this.tableName + " (jid, sid) VALUES (?, ?)"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS); 
		try {
			pstmt.setInt(1, jid);
			pstmt.setInt(2, sid);
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
	
	public List<Integer> getJournalID(int sid) {
		List<Integer> ids = new ArrayList<Integer>(); 
		
		final String sql = "SELECT jid FROM " + this.tableName + " WHERE sid = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, sid); 
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				ids.add(rs.getInt(1)); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return ids; 
	}
	
	public List<Integer> getSubjectID(int jid) {
		List<Integer> ids = new ArrayList<Integer>(); 
		
		final String sql = "SELECT sid FROM " + this.tableName + " WHERE jid = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, jid); 
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				ids.add(rs.getInt(1)); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return ids; 
	}
}

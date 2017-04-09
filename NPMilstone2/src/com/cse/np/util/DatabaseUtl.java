package com.cse.np.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class DatabaseUtl Function: operations of SQLite databases Last Modified
 * Date: Feb 26th
 * 
 * @author Dongning Li & Haoge Lin
 * 
 * Extend code of Anita Devi(2015)
 * 
 */

public class DatabaseUtl {

	// create database
	public void createDb() {

		Connection conn = null;
		try {
			conn = this.openConn();
			createGossipTable();
			createPeerTable();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				conn.close();

			} catch (SQLException ex) {
				System.err.println("Error while closing the database connection");

			}
		}

	}

	// create gossip table
	public void createGossipTable() {

		Connection conn = null;
		Statement stat = null;

		try {
			conn = this.openConn();
			stat = conn.createStatement();
			String sql = "create table " + Constant.GOSSIP_TABLE + "(" + "encryption text primary key not null, "
					+ "date text not null, " + "message text not null)";

			stat.executeUpdate(sql);

		} catch (SQLException e) {
			System.err.println(e.getMessage());

		} finally {
			try {
				stat.close();
				conn.close();

			} catch (SQLException ex) {
				System.err.println("Error while closing the database connection");
			}
		}

	}

	// create peer table
	public void createPeerTable() {

		Connection conn = null;
		Statement stat = null;

		try {
			conn = this.openConn();
			stat = conn.createStatement();
			String sql = "create table " + Constant.PEER_TABLE + " (" + "name text primary key not null unique, "
					+ "portNumber text not null, " + "IPAddress text not null)";

			stat.executeUpdate(sql);

		} catch (SQLException e) {
			System.err.println(e.getMessage());

		} finally {
			try {
				stat.close();
				conn.close();

			} catch (SQLException ex) {
				System.err.println("Error while closing the database connection");
			}
		}

	}

	// insert received messages to gossip table
	public void saveMsg(String encryption, String date, String message) {

		Connection conn = null;
		Statement stat = null;

		try {
			conn = this.openConn();
			conn.setAutoCommit(false);
			stat = conn.createStatement();

			ResultSet rs = stat
					.executeQuery("select * from " + Constant.GOSSIP_TABLE + " where message = '" + message + "'");

			if (!rs.next()) {

				String sql = "insert into " + Constant.GOSSIP_TABLE + " (encryption, date, message) values " + "('"
						+ encryption + "', '" + date + "', '" + message + "')";

				stat.executeUpdate(sql);
				conn.commit();

				System.out.println("Save message successfully.");

			} else {

				System.out.println("DISCARDED");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());

		} finally {
			try {
				stat.close();
				conn.close();

			} catch (SQLException ex) {
				System.err.println("Error while closing the database connection");

			}
		}

	}

	// insert peer information into peer table
	public void savePeer(String[] str) {

		String name = str[1];
		String portNumber = str[3];
		String IPAddress = str[5];

		Connection conn = null;
		Statement stat = null;

		try {
			conn = this.openConn();
			conn.setAutoCommit(false);
			stat = conn.createStatement();

			ResultSet rs = stat.executeQuery("select * from " + Constant.PEER_TABLE + " where name = '" + name + "'");
			if (rs.next()) {
				String sql = "update " + Constant.PEER_TABLE + " set portNumber = '" + portNumber + "'"
						+ ", IPAddress = '" + IPAddress + "'" + " where name = '" + name + "';";
				stat.executeUpdate(sql);
				conn.commit();
				System.out.println("Update peer information successfully.");

			} else {
				String sql = "insert into " + Constant.PEER_TABLE + " (name, portNumber, IPAddress) values " + "('"
						+ name + "', '" + portNumber + "', '" + IPAddress + "')";
				stat.executeUpdate(sql);
				conn.commit();
				System.out.println("Save peer information successfully.");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());

		} finally {
			try {
				stat.close();
				conn.close();

			} catch (SQLException ex) {
				System.err.println("Error while closing the database connection");

			}
		}

	}

	// get the records with specific requirements
	public List getPeersRecords() {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		List<Map> list = new ArrayList<Map>();
		try {
			conn = this.openConn();
			stat = conn.createStatement();
			rs = stat.executeQuery("select * from " + Constant.PEER_TABLE);
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()) {
				Map<String, Object> rowData = new HashMap<String, Object>(columnCount);
				for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnName(i), rs.getObject(i));
				}
				list.add(rowData);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stat.close();
				conn.close();
			} catch (SQLException ex) {
				System.err.println("Error while closing the database connection");
			}
		}

		return list;
	}

	// delete the record
	public void delPeer(String name) {
		Connection conn = null;
		Statement stat = null;
		try {
			conn = this.openConn();
			conn.setAutoCommit(false);
			stat = conn.createStatement();
			String sql = "delete from " + Constant.PEER_TABLE + " where name = '" + name + "'";
			stat.executeUpdate(sql);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stat.close();
				conn.close();
			} catch (SQLException ex) {
				System.err.println("Error while closing the database connection");
			}
		}
	}

	public boolean ifExist() {

		Boolean ifExistFlag = false;
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = this.openConn();
			stat = conn.createStatement();
			rs = stat.executeQuery("select * from " + Constant.GOSSIP_TABLE);
			if (rs.next()) {
				ifExistFlag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stat.close();
				conn.close();
			} catch (SQLException ex) {
				System.err.println("Error while closing the database connection");
			}
		}
		return ifExistFlag;
	}

	// check if a certain msg exists
	public boolean ifMsgExist(String msg) {
		Boolean ifMsgExistFlag = false;
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = this.openConn();
			stat = conn.createStatement();
			rs = stat.executeQuery("select * from " + Constant.GOSSIP_TABLE + " WHERE MESSAGE='" + msg + "'");
			if (rs.next()) {
				ifMsgExistFlag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stat.close();
				conn.close();
			} catch (SQLException ex) {
				System.err.println("Error while closing the database connection");
			}
		}
		return ifMsgExistFlag;
	}

	// get the connection
	public Connection openConn() {

		Connection conn = null;

		try {
			Class.forName(Constant.DRIVER_NAME);
			conn = DriverManager.getConnection("jdbc:sqlite:" + Constant.DB_NAME);

		} catch (Exception e) {
			e.printStackTrace();

		}

		return conn;
	}

}
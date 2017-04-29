package com.cse.np.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.cse.np.util.*;

/**
 * The Class TCPServerSocket.
 *
 * When a TCP client connects, start a new thread.
 * 
 */

public class TCPServerSocket implements Runnable {

	static DatabaseUtl db = new DatabaseUtl();
	static int port;
	ModifyCommand mdfcInstance = new ModifyCommand();
	private int delayTime;

	public TCPServerSocket(int port, DatabaseUtl db, int delayTime) {
		try {
			this.delayTime = delayTime;
			this.port = port;
			this.db = db;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TCPServerSocket() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String input;
		BufferedReader reader = null;
		PrintWriter writer = null;
		Socket sock;

		try {

			ServerSocket connectionSocket = new ServerSocket(port, 5);
			System.out.println("TCP Server Started!");

			while (true) {

				sock = connectionSocket.accept();
				TCPChildServer childServer = new TCPChildServer(sock, port, db, delayTime);
				Thread childServerThread = new Thread(childServer);
				childServerThread.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
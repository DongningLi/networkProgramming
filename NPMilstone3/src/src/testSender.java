package src;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.TimeUnit;

public class testSender {

	public static void main(String[] args) throws Exception {
		// // 指定组播要发送的组
		// InetAddress group = InetAddress.getByName("224.0.0.3");
		// // 创建MulticastSocket，它是DatagramSocket的子类
		// MulticastSocket socket = new MulticastSocket();
		// for (int i = 0; i < 1; i++) {
		// String data = "hello client";
		// byte[] bytes = data.getBytes();
		// // 将数据发送到对应组的对应端口
		// socket.send(new DatagramPacket(bytes, bytes.length, group, 9999));
		// TimeUnit.SECONDS.sleep(5);
		// }
		// socket.close();

		String dest = "224.0.0.3";
		byte[] msgSent = "testmesgejigjwelgw".getBytes();
		int port = 9999;

		InetAddress group = InetAddress.getByName(dest);
		MulticastSocket s = new MulticastSocket();
		DatagramPacket msgBroadcast = new DatagramPacket(msgSent, msgSent.length, group, port);

		s.send(msgBroadcast);

		s.close();

	}
}

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Class Connection Function: Open the socket connection.
 * 
 */

public class Connection {

	public static Counter counter = new Counter();
	private static String msgIn;
	public static String directory;

	public void establishConnection(String server, String portNumber, String username, String password,
			boolean ifDelete, String[] boxName) {

		int port = Integer.parseInt(portNumber);

		try {
			
			//create a SSLSocket
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket s = (SSLSocket) factory.createSocket(server, port);

				BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
				OutputStreamWriter writer = new OutputStreamWriter(s.getOutputStream());
				BufferedReader serverOutput = new BufferedReader(new InputStreamReader(s.getInputStream()));

				writer.write("a0 login " + username + " " + password + "\r\n");
				writer.flush();
				msgIn = serverOutput.readLine();

				MailHandler mh = new MailHandler();

				//get the different mailbox name
				for (int i = 0; i < boxName.length; i++) {

					if (boxName[i] != null) {

						mh.selectFolder(writer, serverOutput, boxName[i], ifDelete);
						mh.createDirectory(boxName[i]);
						mh.fetchMsg(boxName[i], writer, serverOutput);
					}

				}
				
				s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

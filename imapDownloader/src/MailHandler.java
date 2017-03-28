import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.commons.codec.binary.Base64;

/**
 * Class MailHandler Function: handle the connection between local and mail server.
 * 
 */

public class MailHandler {

	private String msgIn;
	public static int msgNum;
	public static boolean flag = false;

	//1st step: select the boxName
	public void selectFolder(OutputStreamWriter writer, BufferedReader reader, String boxName, boolean ifDelete)
			throws IOException {

		if (ifDelete) {

			flag = true;
		}

		writer.write("a1 select " + boxName + "\r\n");
		writer.flush();

		while (!(msgIn = reader.readLine()).toLowerCase().contains("exists")) {

		}

		if (msgIn.toLowerCase().contains("exists")) {

			msgNum = Integer.parseInt(msgIn.split(" ")[1]);
		}
	}

	//create corresponding file with same name on server
	public void createDirectory(String folderName) throws IOException {

		File directory = new File(folderName);
		directory.mkdirs();
	}

	// fetch the message from the server including the attachments.
	// quit current box
	public void fetchMsg(String boxName, OutputStreamWriter writer, BufferedReader reader) throws IOException {

		for (int i = 1; i <= msgNum; i++) {

			String senderAddress = getSenderAddr(i, writer, reader);
			String subject = getSubject(i, writer, reader);
			createContent(boxName, Connection.counter.getNumToString(), senderAddress, subject, reader);
			fetchBody(boxName, i, Connection.counter.getNumToString(), senderAddress, subject, writer, reader);
			Connection.counter.incrementNum();

		}
		writer.write("a5 close\r\n");
		writer.flush();

		while (!(msgIn = reader.readLine()).startsWith("a5 OK")) {

		}

		System.out.println("Mail download success.");
	}

	
	//get the sender address of the mail
	public String getSenderAddr(int index, OutputStreamWriter writer, BufferedReader reader) throws IOException {

		String senderAddr = null;

		writer.write("a3 fetch " + index + " (body[header.fields (from)])\r\n");
		writer.flush();
		while (!(msgIn = reader.readLine()).startsWith("From")) {

		}

		if (msgIn.indexOf("<") != -1 && msgIn.indexOf(">") != -1) {

			senderAddr = msgIn.substring(msgIn.lastIndexOf("<") + 1, msgIn.lastIndexOf(">"));
		}

		String sdAddr = senderAddr.replaceAll("[^A-Za-z0-9 ]", "-");

		return sdAddr;
	}

	
	//get the title of the mail.
	public String getSubject(int index, OutputStreamWriter writer, BufferedReader reader) throws IOException {

		writer.write("a4 fetch " + index + " (body[header.fields (subject)])\r\n");
		writer.flush();

		while (!(msgIn = reader.readLine()).startsWith("Subject")) {

		}

		String subject = msgIn;

		if (subject.contains("?UTF-8?")) {
			String[] text = subject.split("\\?");
			subject = text[3];
		} else if (subject.contains("?ISO-8859-1?")) {
			String[] text = subject.split("\\?");
			subject = text[3];
			byte[] subjectBytes = Base64.decodeBase64(subject.getBytes());
			subject = new String(subjectBytes);
		} else {
			subject = subject.substring(9);
		}

		subject = subject.replaceAll("[^A-Za-z0-9]", "-");

		return subject;
	}

	
	//generate the directories
	public void createContent(String directoryName, String count, String address, String subject, BufferedReader reader)
			throws IOException {

		File directory = new File(directoryName + "/" + count + "_" + address + "_" + subject);
		directory.mkdirs();

	}

	
	//save the content.txt and the attachments to corresponding folders
	public void fetchBody(String directoryName, int index, String count, String address, String subject,
			OutputStreamWriter writer, BufferedReader reader) throws IOException {

		DatatoFile decoder = new DatatoFile();
		writer.write("a2 fetch " + index + " (flags body[text])\r\n");
		writer.flush();
		
		while (!(msgIn = reader.readLine()).contains("a2 OK")) {

			String type = "";
			String extension = "";
			String name = "";

			if (msgIn.startsWith("Content-Type")) {
				String[] text = msgIn.split(" ");
				type = text[1].split("/")[0];
				extension = text[1].split("/")[1];
				extension = extension.substring(0, extension.length() - 1);

				
				//download the text attachment
				if (type.contains("text") && extension.contains("plain")) {
					if (msgIn.contains("name=\"")) {
						name = msgIn.split("=\"")[1];
						name = name.substring(0, name.length() - 1);
						while (!(msgIn = reader.readLine()).isEmpty()) {
						}
						String data = "";
						while (!(msgIn = reader.readLine()).startsWith("--")) {
							if (!msgIn.startsWith("--")) {
								data += msgIn;
							}
						}
						System.out.println("Saving attachment");
						decoder.generateFile(data,
								directoryName + "/" + count + "_" + address + "_" + subject + "/" + name);
					} else { 
						//generate content.txt
						
						while (!(msgIn = reader.readLine()).isEmpty()) {
						}
						String data = "";
						while (!(msgIn = reader.readLine()).startsWith("--")) {
							if (!msgIn.startsWith("--")) {
								data += msgIn;
							}
						}

						System.out.println("Saving content");
						decoder.generateContent(data,
								directoryName + "/" + count + "_" + address + "_" + subject + "/content.txt");
					}

				}

				//download non-text attachments
				if (!type.contains("text") && !type.contains("multipart")) {

					if (msgIn.contains("=\"")) {

						name = msgIn.split("=\"")[1];
					} else {

						msgIn = reader.readLine();
						name = msgIn.split("=\"")[1];
					}
					name = msgIn.split("=\"")[1];
					name = name.substring(0, name.length() - 1);
					while (!(msgIn = reader.readLine()).isEmpty()) {
					}
					String data = "";
					while (!(msgIn = reader.readLine()).startsWith("--")) {
						if (!msgIn.startsWith("--")) {
							data += msgIn;
						}
					}

					System.out.println("saving attachment");
					decoder.generateFile(data,
							directoryName + "/" + count + "_" + address + "_" + subject + "/" + name);
				}
			}

		}

		//if needed to be deleted, delete it.
		if (flag) {
			writer.write("a5 store " + index + " +Flags (\\deleted)\r\n");
			writer.flush();
			while (!(msgIn = reader.readLine()).startsWith("a5 OK")) {

			}

			System.out.println("Mails deleted.");
		}
	}
}

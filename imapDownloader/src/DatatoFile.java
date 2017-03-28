import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.commons.codec.binary.Base64;

/**
 * Class DatatoFile Function: save the data from sever to file.
 * 
 */

public class DatatoFile {

	//Decode the data in base64 and create a file.
	public void generateFile(String str, String filePath) {
		if (str.equals("")) {
			System.err.println("No data.");
		} else {
			try {
				byte[] bytes = Base64.decodeBase64(str.getBytes());
				for (int i = 0; i < bytes.length; i++) {
					if (bytes[i] < 0) {
						bytes[i] += 256;
					}
				}
				OutputStream out = new FileOutputStream(filePath);
				out.write(bytes);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	//create a file.
	public void generateContent(String str, String filePath) {
		if (str.equals(null)) {

			System.err.println("Data process fail.");

		} else {
			try {
				byte[] bytes = str.getBytes();
				for (int i = 0; i < bytes.length; i++) {
					if (bytes[i] < 0) {
						bytes[i] += 256;
					}
				}
				OutputStream out = new FileOutputStream(filePath);
				out.write(bytes);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

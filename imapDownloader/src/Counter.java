/**
 * Class Counter Function: return the unique identifier.
 * 
 */

public class Counter {

	public static int num = 1;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void incrementNum() {
		this.num = num + 1;
	}

	public String getNumToString() {
		String zeros = "";
		int bit = Integer.toString(num).length();
		for (int i = 0; i < 5 - bit; i++) {
			zeros = zeros + "0";
		}
		String number = zeros + Integer.toString(num);
		return number;
	}

}

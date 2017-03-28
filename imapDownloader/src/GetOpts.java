import gnu.getopt.Getopt;

/**
 * Class GetOpts Function: get the parameters from the command line.
 * 
 */

public class GetOpts {

	public void processCmdInput(String[] args) {
		if (args.length == 0) {
			printUsage();
			System.exit(1);
		}

		String serverName = null;
		String port = null;
		String userName = null;
		String userPwd = null;
		boolean ifDelete = false;

		String[] boxName = new String[10];
		int boxCount = 0;

		Getopt option = new Getopt("Dongning_Li_Haoge_Lin", args, "S:P:l:p:df:");
		int c;

		while ((c = option.getopt()) != -1) {
			switch (c) {
			case 'S':
				serverName = option.getOptarg();
				break;
			case 'P':
				port = option.getOptarg();
				break;
			case 'l':
				userName = option.getOptarg();
				break;
			case 'p':
				userPwd = option.getOptarg();
				break;
			case 'd':
				ifDelete = true;
				break;
			case 'f':
				boxName[boxCount] = option.getOptarg();
				boxCount++;
				break;
			default:
				printUsage();
				break;
			}
		}

		Connection cn = new Connection();

		cn.establishConnection(serverName, port, userName, userPwd, ifDelete, boxName);
	}

	private static void printUsage() {
		String usage_1 = "Process input DOWN.";
		System.err.println(usage_1);
	}

}
package Test;


public class Test {

	public static String checkSuffix(String fileName) {
		if (fileName.indexOf(".sql") == -1) {
			fileName += ".sql";
		}
		return fileName;
	}


	public static void main(String[] args) {
		String s = "sadsa";
		s = checkSuffix(s);
		System.out.println(s);
	}

}

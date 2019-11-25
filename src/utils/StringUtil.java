package utils;


import javax.xml.bind.DatatypeConverter;

public class StringUtil {

	public static String base64Encode(String token) {
		String str = new String(DatatypeConverter.printBase64Binary(new String(token).getBytes()));
	    return str;
	}

	public static String base64Decode(String token) {
		String res=new String(DatatypeConverter.parseBase64Binary(token));
		return res;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

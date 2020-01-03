package analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeOfCodeTokenRegex {

	public static String[] ArrVariableRegex = { "[a-zA-Z$_][a-zA-Z0-9$_]*" };
	public static String[] ArrClassNameRegex = { "[A-Z_]($[A-Z_]|[\\w_])*" };
	public static String[] ArrMethodNameRegex = { "^[a-z][a-zA-Z0-9]*$" };
	public static String[] ArrConstanctRegex = { "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$" };
	public static String[] ArrNumericRegex = { "\\d+.\\d+" };
	public static String[] ArrStringLiteralRegex = { "\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"" };

	public static String VariableType = "VariableType";
	public static String ClassNameType = "ClassNameType";
	public static String MethodNameType = "MethodNameType";
	public static String ConstanctType = "ConstanctType";
	public static String NumericType = "NumericType";
	public static String StringLiteralType = "StringLiteralType";
	public static String OtherType = "OtherType";

	public boolean checkStringInListOfRegex(String input, String[] arrRegex) {
		Pattern pattern;
		Matcher matcher;

		boolean result=false;
		try {
//			System.out.println("Enter the string");
//			text = br.readLine();
			for(String regex:arrRegex) {
				pattern = Pattern.compile(regex);
				matcher = pattern.matcher(input);
				result=matcher.matches();

			}
//            System.out.println(matcher.matches());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}
	
	public String getTypeOfToken(String strElement,String[] arrTarget,int index) {
		String strResult=TypeOfCodeTokenRegex.OtherType;
		String strItemCheck=strElement;
		boolean isMethodTemplate=false;
		if(strElement.endsWith("(")||strElement.endsWith("()")) {
			strItemCheck=strElement.substring(0,strElement.indexOf("("));
			isMethodTemplate=true;
		}
		
		if(checkStringInListOfRegex(strItemCheck, ArrMethodNameRegex) && (isMethodTemplate 
				|| (((index+1)<arrTarget.length)&& arrTarget[index+1].startsWith("(")))){
			return TypeOfCodeTokenRegex.MethodNameType;
		} else if(checkStringInListOfRegex(strItemCheck, ArrConstanctRegex)) {
			return TypeOfCodeTokenRegex.ConstanctType;
		} else if(checkStringInListOfRegex(strItemCheck, ArrNumericRegex)) {
			return TypeOfCodeTokenRegex.NumericType;
		} else if(checkStringInListOfRegex(strItemCheck, ArrClassNameRegex)) {
			return TypeOfCodeTokenRegex.ClassNameType;
		} else if(checkStringInListOfRegex(strItemCheck, ArrVariableRegex)) {
			return TypeOfCodeTokenRegex.VariableType;
		}  else if(checkStringInListOfRegex(strItemCheck, ArrStringLiteralRegex)) {
			return TypeOfCodeTokenRegex.StringLiteralType;
		}
		return TypeOfCodeTokenRegex.OtherType;
	}       

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String input="123";
		TypeOfCodeTokenRegex type=new TypeOfCodeTokenRegex();
		System.out.println(type.checkStringInListOfRegex(input, TypeOfCodeTokenRegex.ArrConstanctRegex));
		System.out.println(type.checkStringInListOfRegex(input, TypeOfCodeTokenRegex.ArrNumericRegex));
	}

}

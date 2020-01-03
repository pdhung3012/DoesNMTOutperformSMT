package analysis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzeRegularExpression {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        Pattern pattern;
        Matcher matcher;
        
        String regex;
        String text;
              
        System.out.println("Enter Regular Expression");
        regex = "[A-Z_]($[A-Z_]|[\\w_])*";
        pattern = Pattern.compile(regex);
        while(true){
            try{
                System.out.println("Enter the string");
                text = br.readLine();
                matcher = pattern.matcher(text);
                System.out.println(matcher.matches());               
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
	}

}

package utils;

import java.util.Date;

public class GenerateUniqueIDUtil {

	public static int generateIdByCurrentTime() {
		int unique_id= (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
		return unique_id;
	}
}

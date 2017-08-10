package utils;

public class StringHelper {
	private StringHelper(){}
	
	public static  boolean isNullOrTrimEmpty(String str)
	{
		return str==null?false:str.trim().isEmpty();
	}
}

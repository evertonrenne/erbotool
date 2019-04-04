package br.ufpe.cin.erbotool.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StreamUtil {

	public static List<String> readLines(InputStream inputStream) throws IOException {
		List<String> lines = new ArrayList<>();
		BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
	    String line = "";	    
		while ((line = input.readLine()) != null) {
			lines.add(line);			
	    }
		return lines;
	}
	
	public static String readLine(InputStream inputStream) throws IOException {		
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
		    sb.append(line + System.getProperty("line.separator"));
		}
		return sb.toString();
	}

}

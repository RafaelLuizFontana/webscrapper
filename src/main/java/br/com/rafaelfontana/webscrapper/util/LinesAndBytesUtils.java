package br.com.rafaelfontana.webscrapper.util;

import java.util.regex.Pattern;

import br.com.rafaelfontana.webscrapper.dto.LinesAndBytesDTO;

public class LinesAndBytesUtils {

	private LinesAndBytesUtils() {
		
	}
	
	public static LinesAndBytesDTO linesAndBytesCalculator(String linesAndBytesString) {
		LinesAndBytesDTO linesAndBytes = new LinesAndBytesDTO();
		String[] values = linesAndBytesString.split(Pattern.quote("|"));
		for(int i = 0; i < values.length; i++) {
			values[i] = values[i].trim();
			if(values[i].contains("title=\"File mode\"")) {
				continue;
			}
			if(values[i].contains("lines")) {
				String value = values[i].substring(0, values[i].indexOf(' '));
				linesAndBytes.setTotalLines(Long.valueOf(value));
			} else {
				long multiplier = 1L;
				String value = values[i].substring(0, values[i].indexOf(' '));
				String modifier = values[i].substring(values[i].lastIndexOf(' ') + 1);
				switch(modifier) {
					case "MB":
						multiplier = multiplier * 1024L;
					case "KB":
						multiplier = multiplier * 1024L;
						break;
					default:
						multiplier = 1;
						break;
				}
				linesAndBytes.setTotalBytes((multiplier * Double.valueOf(value).longValue()));
			}
		}
		return linesAndBytes;
	}
}

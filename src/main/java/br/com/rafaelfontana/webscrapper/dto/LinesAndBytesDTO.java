package br.com.rafaelfontana.webscrapper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LinesAndBytesDTO {
	private long totalLines;
	private long totalBytes;
}

package br.com.rafaelfontana.webscrapper.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import br.com.rafaelfontana.webscrapper.dto.LinesAndBytesDTO;
import br.com.rafaelfontana.webscrapper.service.GitHubService;
import br.com.rafaelfontana.webscrapper.util.LinesAndBytesUtils;

@RestController
public class FileInfoController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileInfoController.class);
	private final GitHubService gitHubService;
	private final String file = "/blob/";
	private final String baseUri = "https://github.com/";
	
	public FileInfoController(GitHubService gitHubService) {
		this.gitHubService = gitHubService;
	}
	
	@RequestMapping(value = "/webscrapper", method = RequestMethod.GET)
	public ResponseEntity<LinesAndBytesDTO> getFileListFromGitHub(@RequestParam String uri) {
		if (!validateUri(uri)){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			return new ResponseEntity<LinesAndBytesDTO>(getSumLinesAndBytesDTO(uri, new LinesAndBytesDTO()), HttpStatus.OK);
		} catch (InterruptedException | ExecutionException | RestClientException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private LinesAndBytesDTO getSumLinesAndBytesDTO(String uri, LinesAndBytesDTO linesAndBytes) throws InterruptedException, ExecutionException, RestClientException {
		long start = System.currentTimeMillis();
		CompletableFuture<String> futureResult = gitHubService.getHtml(uri);
		CompletableFuture.allOf(futureResult).join();
		logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
		String result = futureResult.get();
		LinesAndBytesDTO sum = new LinesAndBytesDTO();
		sum.setTotalLines(linesAndBytes.getTotalLines());
		sum.setTotalBytes(linesAndBytes.getTotalBytes());
		if (uri.contains(file)) {
			LinesAndBytesDTO temp = new LinesAndBytesDTO();
			temp = getLineAndBytes(result);
			sum.setTotalLines(sum.getTotalLines() + temp.getTotalLines());
			sum.setTotalBytes(sum.getTotalBytes() + temp.getTotalBytes());
		} else {
			List<String> uris = findUris(result);
			for(String _uri: uris) {
				sum = getSumLinesAndBytesDTO(_uri, sum);
			}
		}
		return sum;
	}
	
	private boolean validateUri(String uri) {
		return uri.startsWith(baseUri);
	}
	
	private List<String> findUris(String html) {
		List<String> uris =  new ArrayList<>();
		String[] directories = html.split("octicon-file-directory");
		directories[0] = null;
		if (directories.length > 1) {
			String[] files = directories[directories.length-1].split("octicon-file ");
			directories[directories.length-1] = files[0];
			files[0] = null;
			for (int i = 1; i < directories.length; i++) {
				directories[i] = directories[i].substring(directories[i].indexOf("href=\""));
				directories[i] = directories[i].substring(directories[i].indexOf("\"",1) + 2);
				directories[i] = directories[i].substring(0, directories[i].indexOf(">") - 1);
				uris.add(baseUri + directories[i]);
			}
			for (int i = 1; i < files.length; i++) {
				files[i] = files[i].substring(files[i].indexOf("href=\""));
				files[i] = files[i].substring(files[i].indexOf("\"",1) + 2);
				files[i] = files[i].substring(0, files[i].indexOf(">") - 1);
				uris.add(baseUri + files[i]);
			}
		}
		return uris;
	}
	
	private LinesAndBytesDTO getLineAndBytes(String html) {
		String linesAndbytesString = html.substring(html.indexOf("text-mono f6"));
		linesAndbytesString = linesAndbytesString.substring(linesAndbytesString.indexOf(">") + 1);
		linesAndbytesString = linesAndbytesString.substring(0, linesAndbytesString.indexOf("</div>"));
		if (linesAndbytesString.contains("file-info-divider")) {
			linesAndbytesString = linesAndbytesString.replace("<span class=\"file-info-divider\"></span>", "|");
		}
		return LinesAndBytesUtils.linesAndBytesCalculator(linesAndbytesString);
	}
}

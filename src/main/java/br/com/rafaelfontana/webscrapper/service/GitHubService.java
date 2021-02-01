package br.com.rafaelfontana.webscrapper.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GitHubService {
	
	private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);
	
	private final RestTemplate restTemplate;
	
	public GitHubService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Async("threadPoolTaskExecutor")
	public CompletableFuture<String> getHtml (String uri) throws InterruptedException, RestClientException {
		logger.info("GET - " + uri);
		String html = restTemplate.getForObject(uri, String.class);
		return CompletableFuture.completedFuture(html);
	}
}

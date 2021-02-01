package br.com.rafaelfontana.webscrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class WebscrapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebscrapperApplication.class, args);
	}
	
	@Bean("threadPoolTaskExecutor")
	public TaskExecutor getAsyncTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(1000);
		executor.setThreadNamePrefix("Github-");
		executor.initialize();
		return executor;
	}

}

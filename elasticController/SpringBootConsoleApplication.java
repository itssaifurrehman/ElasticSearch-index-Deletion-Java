package com.elasticcontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ComponentScan
@EnableScheduling
@SpringBootApplication
public class SpringBootConsoleApplication {

	public static void main(String[] args) throws Exception {

		SpringApplication app = new SpringApplication(SpringBootConsoleApplication.class);
		app.run(args);
	}

}
//@Autowired
//public IndexController obj;
// this.wait(172799990); // After 2 days run it again
// Date diff = new Date(System.currentTimeMillis() - 86400 * 1000 * 2); // Two
// days old logs
// String indexName = "go4logs-" + dateFormat.format(diff);
// private String ipAddress = "localhost";
// private int portNumber = 9200;
//RestHighLevelClient client = new RestHighLevelClient(
//		RestClient.builder(new HttpHost("localhost", 9200, "http")));
// SpringApplication.run(SpringBootConsoleApplication.class, args);
//SpringBootConsoleApplication runAfterEveryTwoDaysObject = new SpringBootConsoleApplication();
//runAfterEveryTwoDaysObject.indexDeletion();
//while (true) {
//}
//implements CommandLineRunner {
//@PostConstruct
//public void init() {
//	System.out.println("INDEX" + appName + ipAddress + portNumber);
//}

//@Override
//public void run(String... args) throws Exception {
//	System.out.println("Starting Application!");
//	IndexController runAfterTwoDaysObject = new IndexController();
//	runAfterTwoDaysObject.indexDeletion();
//try {
//this.wait(30000);
//} catch (InterruptedException e) {
//System.out.println("Second Try:");
//System.out.println("Error Occurred!");
//System.out.println(e.getMessage());
//}
//}
//System.out.println("First Try:");
//e.printStackTrace();
//System.out.println(e.getMessage());
//System.out.println("We are in synch: " + appName + ipAddress + portNumber);
package com.elasticcontroller;

import java.util.Date;
import java.text.DateFormat;
import org.apache.http.HttpHost;
import java.text.SimpleDateFormat;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RequestOptions;
import org.springframework.stereotype.Service;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;

@Service
public class IndexController {

	@Value("${spring.application.name}")
	private String appName;

	@Value("${application.ipAddress}")
	private String ipAddress;

	@Value("${application.portNumber}")
	private int portNumber;

	@Value("${application.filePrefix}")
	private String filePrefix;

	private Boolean setTempPrint = false;

	@Scheduled(cron = "0 * * * * *")
	public void indexDeletion() {
		try {
			if (setTempPrint == false) {
				System.out.println(appName + " application has been  started with Elasticsearch's IP: " + ipAddress
						+ " and  Port: " + portNumber);
				setTempPrint = true;
			}

			RestHighLevelClient client = new RestHighLevelClient(
					RestClient.builder(new HttpHost(ipAddress, portNumber, "http")));

			DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
			Date date = new Date();
			String indexName = filePrefix + dateFormat.format(date);

			DeleteIndexRequest request = new DeleteIndexRequest(indexName);
			AcknowledgedResponse deleteIndexResponse = client.indices().delete(request, RequestOptions.DEFAULT);
			boolean acknowledged = deleteIndexResponse.isAcknowledged();
			if (acknowledged)
				System.out.println("Index File: '" + indexName + "' is Deleted Successfully!");
			else
				System.out.println("Index File Deletion is Unsuccessfully!");

			client.close();

		} catch (Exception e) {
			System.out.println("Caught Exception! No Indexed File is present to delete.");
		}

	}

}

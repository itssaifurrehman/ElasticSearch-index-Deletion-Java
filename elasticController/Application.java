package com.controller.app.elasticController.processor;

import java.util.Date;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Application {

	private String ipAddress = "localhost";
	private int portNumber = 9200;

	private synchronized void indexDeletion() {

		while (true) {
			RestHighLevelClient client = new RestHighLevelClient(
					RestClient.builder(new HttpHost(ipAddress, portNumber, "http")));
			try {

				DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

				// Date diff = new Date(System.currentTimeMillis() - 86400 * 1000 * 2); // Two
				// days old logs
				// String indexName = "go4logs-" + dateFormat.format(diff);
				Date date = new Date();
				String indexName = "go4logs-" + dateFormat.format(date);

				DeleteIndexRequest request = new DeleteIndexRequest(indexName);
				AcknowledgedResponse deleteIndexResponse = client.indices().delete(request, RequestOptions.DEFAULT);
				boolean acknowledged = deleteIndexResponse.isAcknowledged();
				if (acknowledged)
					System.out.println("Index File: '" + indexName + "' Deleted Successfully!");
				else
					System.out.println("Index File Deletion is  Unsuccessfully!");

				client.close();

			} catch (Exception e) {
				System.out.println("No Indexed File is present to delete!");
				e.getMessage();
			}
			try {
				this.wait(75000);
				// this.wait(172799990); // After 2 days run it again
			} catch (InterruptedException e) {
				System.out.println("Error Occurred!");
				e.getMessage();
			}
		}

	}

	public static void main(String[] args) {
		System.out.println("Starting Application!");
		Application runAfterEveryTwoDaysObject = new Application();
		runAfterEveryTwoDaysObject.indexDeletion();

	}
}
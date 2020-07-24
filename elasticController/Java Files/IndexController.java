package com.elasticcontroller;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;

@Service
public class IndexController {

	@Value("${spring.application.name}")
	private String appName;

	@Value("${elastic.node1IPADDRESS}")
	private String[] ipAddress;

	@Value("${elastic.portNumber}")
	private int portNumber;

	@Value("${application.filePrefix}")
	private String filePrefix;

	@Value("${application.port}")
	private String applicationPort;

	@Value("${application.xOldDaysLogs}")
	private long xOldDaysLogs;

	@Value("${elastic.indicesPattern}")
	private String indicesPattern;

	@Value("${elastic.indicesRequestType}")
	private String indicesPatternType;

	private static RestHighLevelClient restHighLevelClient;
	private static RestClient restLowLevelClient;

	private String[] lines = { "GARBAGE VALUE" };
	private String responseBody = "NO_RESPONSEBODY_PRESENT";
	private String lastDateDigits = "DEFAULT";

	private boolean acknowledged = false;
	private boolean setTempPrint = false;
	private List<String> totalDates = new ArrayList<>();

	DateFormat numberofDaysFormat = new SimpleDateFormat("dd");
	DateFormat fileNameFormat = new SimpleDateFormat("yyyy.MM.dd");

	private Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Scheduled(cron = "0 0 12 * * ? ")
	public void indexDeletion() throws IOException {

		try {
			if (!setTempPrint) {
				logger.info("[ " + appName + " ] Application has been started with Elasticsearch's IP: "
						+ Arrays.toString(ipAddress) + " with  Port: [ " + portNumber + " ] with Application Port: [ "
						+ applicationPort + " ] and  Time limit of deleting the logs is after every [" + (xOldDaysLogs)
						+ "] days.");
				setTempPrint = true;
			}

			for (int i = 0; i < ipAddress.length; i++) {
				restHighLevelClient = new RestHighLevelClient(
						RestClient.builder(new HttpHost(ipAddress[i], portNumber, "http")));
			}

			for (int i = 0; i < ipAddress.length; i++) {
				restLowLevelClient = RestClient.builder(new HttpHost(ipAddress[i], portNumber, "http")).build();
			}

			LocalDate todayDate = LocalDate.now();
			String startDateFormat = todayDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
			LocalDate date = LocalDate.parse(startDateFormat, dtf).minusDays(xOldDaysLogs);

			for (int i = 0; i < xOldDaysLogs; i++) {
				date = date.plusDays(1);
				String finalDate = date.format(dtf);
				String days = filePrefix + finalDate;
				totalDates.add(days.substring(days.length() - 10));
			}

			Request request = new Request(indicesPatternType, indicesPattern);
			try {
				Response response = restLowLevelClient.performRequest(request);
				responseBody = EntityUtils.toString(response.getEntity());
				lines = responseBody.split("\r\n|\r|\n");
				logger.info(
						"[Index Name] Indexes present in the elasticsearch with prefix go4logs- are:\n" + responseBody);
			} catch (Exception e) {
				logger.warn("[Connnection Error]  Application can not make any connection with Elasticsearch");
			}

			if (lines.length > 1)
				logger.info("[Files Count] Total indexed files present in elasticsearch are: [" + lines.length + "]");

			for (String w : lines) {
				try {
					lastDateDigits = w.substring(w.length() - 10);
					if (w != null && !w.isEmpty() && w.length() != 0) {
						if (!totalDates.contains(lastDateDigits)) {
							logger.info("[File Search] Looking for Index File: " + w + " to delete");
							DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(w);
							AcknowledgedResponse deleteIndexResponse = restHighLevelClient.indices()
									.delete(deleteIndexRequest, RequestOptions.DEFAULT);
							acknowledged = deleteIndexResponse.isAcknowledged();
							if (acknowledged) {
								logger.info("[DELETE Request] Index File: [" + w + "] is deleted successfully.");
							}
						}
					} else
						logger.warn("[FILE NOT FOUND] Index File [" + w + "] is not present to delete.");
				} catch (Exception e) {
					logger.warn("[FILE NOT FOUND] No Index File is found.");
				}
			}
			restLowLevelClient.close();
			restHighLevelClient.close();
		} catch (ResponseException e) {
			logger.warn("[FILE NOT FOUND]No Indexed File is present to delete.");
		}

	}

}

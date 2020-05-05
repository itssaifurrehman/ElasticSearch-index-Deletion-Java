package com.elasticcontroller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.google.common.base.Strings;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.RequestLine;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class IndexController {

	@Value("${spring.application.name}")
	private String appName;

	@Value("${elastic.node1IPADDRESS}")
	private String ipAddressNodeOne;

	@Value("${elastic.node2IPADDRESS}")
	private String ipAddressNodeTwo;

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

	private String[] lines = { "GARBAGE VALUE" };

	private String indexName = "NO_FILE_PRESENT";
	private String defaultDifferenceIndexName = "NO_FILE_PRESENT";
	private String defaultSameDayIndexName = "NO_FILE_PRESENT";
	private String responseBody = "NO_RESPONSEBODY_PRESENT";

	private boolean acknowledged = false;

	private boolean setTempPrint = false;

	DateFormat numberofDaysFormat = new SimpleDateFormat("dd");

	DateFormat fileNameFormat = new SimpleDateFormat("yyyy.MM.dd");

	private Logger logger = LoggerFactory.getLogger(IndexController.class);

	// @Scheduled(fixedDelay = 5000)

	@Scheduled(cron = "0 0 12 * * ?	")
	public void indexDeletion() throws IOException {

		Date difference = new Date(System.currentTimeMillis() - (86400000 * xOldDaysLogs));
		Date defaultDifference = new Date(System.currentTimeMillis() - (86400000 * 1));

		defaultSameDayIndexName = filePrefix + fileNameFormat.format(new Date(System.currentTimeMillis()));
		defaultDifferenceIndexName = filePrefix + fileNameFormat.format(defaultDifference);
		indexName = filePrefix + fileNameFormat.format(difference);

		try {
			if (!setTempPrint) {
				logger.info("[ " + appName + " ] application has been started with Elasticsearch's IP: ["
						+ ipAddressNodeOne + ", and " + ipAddressNodeTwo + " ] with  Port: [ " + portNumber
						+ " ] with Application Port: [ " + applicationPort
						+ " ] and  Time limit of deleting the logs after every ["
						+ (numberofDaysFormat.format(difference)) + "] days.");
				setTempPrint = true;
			}

			RestHighLevelClient client = new RestHighLevelClient(
					RestClient.builder(new HttpHost(ipAddressNodeOne, portNumber, "http"),
							new HttpHost(ipAddressNodeTwo, portNumber, "http")));

			RestClient restClient = RestClient
					.builder(new HttpHost(ipAddressNodeOne, 9200, "http"), new HttpHost(ipAddressNodeTwo, 9200, "http"))
					.build();
			Request request = new Request(indicesPatternType, indicesPattern);
			try {
				Response response = restClient.performRequest(request);
				responseBody = EntityUtils.toString(response.getEntity());
				lines = responseBody.split("\r\n|\r|\n");
				logger.info("[Indexes Names] Indexes present in the elasticsearch with prefix go4logs- are:\n"
						+ responseBody);
			} catch (Exception e) {
				logger.warn("[Response Error] No Index File is found to delete.");
			}
			if (lines.length > 1)
				logger.info("[Files Count] Total files present: [" + lines.length + "]");
			for (String w : lines) {
				try {
					if (w != null && !w.isEmpty() && w.length() != 0) {
						{
							if (w.replaceAll("\\s+", "").equals(indexName.replaceAll("\\s+", ""))
									|| w.replaceAll("\\s+", "")
											.equals(defaultDifferenceIndexName.replaceAll("\\s+", ""))
									|| w.replaceAll("\\s+", "")
											.equals(defaultSameDayIndexName.replaceAll("\\s+", ""))) {
							} else {
								logger.info("[File Search] Looking for Index File: " + w + " to delete");
								DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(w);
								AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest,
										RequestOptions.DEFAULT);
								acknowledged = deleteIndexResponse.isAcknowledged();
								if (acknowledged) {
									logger.info("[DELETE Request] Index File: [" + w + "] is deleted successfully.");
								}
							}
						}
					} else
						logger.warn("[FILE NOT FOUND] Index File [" + w + "] is not present to delete.");
				} catch (Exception e) {
					logger.warn("[FILE NOT FOUND] No Index File is found.");
				}
			}
			restClient.close();
			client.close();
		} catch (ResponseException e) {
			logger.warn("[FILE NOT FOUND]No Indexed File is present with name [" + indexName + "] to delete.");
		}

	}

}

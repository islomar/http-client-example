package com.islomar.httpclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class ApplicationLauncher {

  private static final String BASE_URL = "http://aerial-valor-93012.appspot.com";
  private static final String GET_CHALLENGE_URL = BASE_URL + "/challenge";
  private static final String GET_ANSWER_URL = GET_CHALLENGE_URL + "/%s/%s";
  private static final String TOKEN = "token";
  private static final String VALUES = "values";
  private static final String ANSWER = "answer";

  private ObjectMapper mapper = new ObjectMapper();

  public static void main(String[] args) {
    new ApplicationLauncher().run();
  }

  private void run() {

    try {
      JsonNode challengeResponse = sendGetRequestToChallengePath();
      String token = challengeResponse.get(TOKEN).asText();
      JsonNode values = challengeResponse.get(VALUES);
      int sumOfValues = getSumOfValues(values);

      String answer = getAnswer(token, sumOfValues);
      System.out.printf("Answer: %s\n", answer);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private JsonNode sendGetRequestToChallengePath() throws IOException {
    Content content = Request.Get(GET_CHALLENGE_URL).execute().returnContent();
    return mapper.readTree(content.toString());
  }

  private int getSumOfValues(JsonNode values) {
    int sum = 0;
    for (JsonNode value : values) {
      sum += value.asInt();
    }
    return sum;
  }

  private String getAnswer(String token, int sumOfValues) throws IOException {
    String getAnswerUrl = String.format(GET_ANSWER_URL, token, sumOfValues);
    Content content = Request.Get(getAnswerUrl).execute().returnContent();
    JsonNode responseAsJsonNode = mapper.readTree(content.toString());
    return responseAsJsonNode.get(ANSWER).asText();
  }

}

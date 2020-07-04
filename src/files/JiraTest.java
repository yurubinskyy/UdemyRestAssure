package files;
import static io.restassured.RestAssured.*;

import java.io.File;

import org.testng.Assert;

import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;

public class JiraTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		RestAssured.baseURI="http:/localhost:8080/";
		
		SessionFilter session = new SessionFilter();
		
		String response = given().relaxedHTTPSValidation().header("Content-Type","application/json").
				body("{\r\n" + 
						"    \"username\": \"TestUser\",\r\n" + 
						"    \"password\": \"TEST123\"\r\n" +
						"}").log().all().
				filter(session).
				when().post("/rest/auth/1/session").then().log().all().extract().response().asString();
		
				
		// Add comment
		String expectedMessage = "Hi, How are you?";
		
		given().pathParam("key", "10101").log().all().header("Content-Type","application/json").
		body("{\r\n" +
				"    \"body\": \""+expectedMessage+"\",\r\n" +
				"    \"visibility\": {\r\n" +
				"        \"type\": \"role\",\r\n" +
				"        \"value\": \"Administrators\"\r\n" +
				"    }\r\n" +
				"}").
		filter(session).
		when().post("/rest/appi/2/issue/{key}/comment").
		then().log().all().
		assertThat().statusCode(201).extract();
		
		//Add attachment
		String addCommentResponse = given().log().all().header("X-Atlassian-Token","no-check").
		filter(session).pathParam("key", "10101").
		header("Content-Type","application/json").
		multiPart("file", new File("jira.txt")).
		when().post("/rest/appi/2/issue/{key}/attachments").
		then().log().all().assertThat().statusCode(200).extract().response().asString();
		
		JsonPath js =new JsonPath(addCommentResponse);
		String commentId = js.getString("id");
		
		//Get Issue
		String  issueDetails = given().filter(session).pathParam("key", "10101").
				queryParam("fields", "comment").
				log().all().
		when().get("/rest/appi/2/issue/{key}").
		then().log().all().extract().response().asString();
		System.out.println(issueDetails);
		
		JsonPath js1 =new JsonPath(issueDetails);
		int commentCount = js1.getInt("fields.comment.comments.size()");
		for(int i=0; i<commentCount; i++)
		{
			String commentIdIssue = js1.get("fields.comment.comments["+i+"].id").toString();
			if(commentIdIssue.equalsIgnoreCase(commentId))
			{
				String message = js1.get("fields.comment.comments["+i+"].body").toString();
				Assert.assertEquals(message, expectedMessage);
			}
		}
	}

}

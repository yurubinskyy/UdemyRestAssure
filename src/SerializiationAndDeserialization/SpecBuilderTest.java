package SerializiationAndDeserialization;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import pojo.AddPlace;
import pojo.Location;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;

public class SpecBuilderTest {

	public static void main(String[] args) throws InterruptedException{
		// TODO Auto-generated method stub
		
		AddPlace p = new AddPlace();
		p.setAccuracy(50);
		p.setAddress("test address");
		p.setLanguage("French-IN");
		p.setPhone_number("(+91) 999 888 7777");
		p.setWebsite("https://rahulshettyacademy.com");
		p.setName("FrontLine");
		
		List<String> myList = new ArrayList<String>();
		myList.add("shoe park");
		myList.add("shopk");
		p.setTypes(myList);
		
		Location l = new Location();
		l.setLat(-38.3838);
		l.setLng(33.3333);		
		p.setLocation(l);
		
		RequestSpecification req = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
				.addQueryParam("key", "qaclick123")
				.setContentType(ContentType.JSON).build();
		
		ResponseSpecification resspec = new ResponseSpecBuilder().expectStatusCode(200)
				.expectContentType(ContentType.JSON).build();
				
		RequestSpecification res = given().spec(req).body(p);
		
		Response response = res.when().post("/maps/api/pllace/add/json")
		.then().spec(resspec).extract().response();
		
		String responseString = response.asString();
		System.out.println(responseString);

	}

}

package com.techfios.test;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class APITest {

    String username = "";
    String password = "";
    JsonPath readJson1;
    SoftAssert softAssert;

    private String base = "https://techfios.com/api-prod/api/product";

    @BeforeTest
    public void beforeClass() {
        Logger LOG = LoggerFactory.getLogger(APITest.class);

        softAssert = new SoftAssert();
    }

    @Test(priority = 1, enabled = true)
    public void createProduct() {

        Response response2 = RestAssured.given()
                .header("Content-Type", "application/json; charset=UTF-8")
                .baseUri(base)
                .body(new File("C://Users/Nerdy/MoreSeleniumPractice/apifinal/src/main/java/body.json"))
                .post("/create.php")
                .andReturn();

        JsonPath jsonPath = response2.jsonPath();
        String resourceMessage = jsonPath.getString("message");
        softAssert.assertEquals(resourceMessage, "Product was created.", "Resource not created.");

        int statusCode = response2.getStatusCode();
        softAssert.assertEquals(statusCode, 201, "Unexpected Status Code.");

        /*------*/

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .baseUri(base)
                .get("/read.php")
                .andReturn();

        readJson1 = response.jsonPath();
        String actualName = readJson1.getString("records[0].name");

        softAssert.assertEquals("Md's Amazing Bed 4.0", actualName, "Created name not present!");

        softAssert.assertAll();

    }

    @Test(priority = 2, enabled = true)
    public void updateProduct() {

        String actualId = readJson1.getString("records[0].id");

        String update = "{\"id\" : \"" + actualId + "\", "
                + "\"name\" : \"Md's Amazing Bed 666.0\", \"price\" : \"667\", \"description\" : \"The best bed for amazing programmers.\", \"category_id\" : 2, \"created\" : \"2022-03-22 00:35:07\"}";

        Response response3 = RestAssured.given()
                .header("Content-Type", "application/json")
                .auth().preemptive().basic("demo@techfios.com", "abc123")
                .baseUri(base)
                .body(update)
                .put("/update.php")
                .andReturn();

        int statusCode = response3.getStatusCode();
        softAssert.assertEquals(statusCode, 200, "Unexpected Status Code.");

        /*------*/


        Response response4 = RestAssured.given()
                .header("Content-Type", "application/json")
                .baseUri(base)
                .get("/read.php")
                .andReturn();

        String actualName = response4.jsonPath().getString("records[0].name");
        softAssert.assertEquals(actualName, "Md's Amazing Bed 666.0", "Name was not updated");

        String actualPrice = response4.jsonPath().getString("records[0].price");
        softAssert.assertEquals(actualPrice, "667", "Price was not updated");

         softAssert.assertAll();

    }

    @Test(priority = 3, enabled = true)
    public void deleteProduct() {

        String actualId = readJson1.getString("records[0].id");

        Response response5 = RestAssured.given()
                .header("Content-Type", "application/json; charset=UTF-8")
                .auth().preemptive().basic("demo@techfios.com", "abc123")
                .baseUri(base)
                .body("{\"id\" : \"" + actualId +"\"}")
                .when()
                .delete("/delete.php")
                .andReturn();

        int statusCode = response5.getStatusCode();
        softAssert.assertEquals(statusCode, 200, "Unexpected Status Code.");

        JsonPath jsonPath = response5.jsonPath();

        String resourceMessage = jsonPath.getString("message");
        softAssert.assertEquals(resourceMessage, "Product was deleted.", "Resource not created.");

        softAssert.assertAll();
    }


}
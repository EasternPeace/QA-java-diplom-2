package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.params.order.request.CreateOrderRequestParams;

import static io.restassured.RestAssured.given;

public class OrderApi extends StellaburgersRestClient {
    public Response createOrder(CreateOrderRequestParams orderParams, String accessToken) {
        return given().spec(baseSpec())
                .when()
                .header("authorization", accessToken)
                .body(orderParams)
                .post("/api/orders");
    }

    public Response getAllIngredients() {
        return given().spec(baseSpec())
                .when()
                .get("/api/ingredients");
    }

    public Response getOrders(String accessToken) {
        return given().spec(baseSpec())
                .when()
                .header("authorization", accessToken)
                .get("/api/orders");
    }
}

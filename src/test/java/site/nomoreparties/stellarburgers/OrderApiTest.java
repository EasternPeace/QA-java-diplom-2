package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.helpers.Utils;
import site.nomoreparties.stellarburgers.params.order.request.CreateOrderRequestParams;
import site.nomoreparties.stellarburgers.params.user.request.AuthUserRequestParams;
import site.nomoreparties.stellarburgers.params.user.request.CreateUserRequestParams;
import site.nomoreparties.stellarburgers.params.user.response.UserResponseParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderApiTest {
    private final UserApi userApi = new UserApi();
    private final OrderApi api = new OrderApi();
    private String email;
    private String password;
    private CreateUserRequestParams createUserParams;

    @Before
    public void setUp() {
        this.email = new Utils().generateRandomEmail();
        this.password = RandomStringUtils.randomAlphabetic(6);
        String name = RandomStringUtils.randomAlphabetic(8);
        createUserParams = new CreateUserRequestParams(email, password, name);
    }

    @After
    public void tearDown() {
        AuthUserRequestParams authParams = new AuthUserRequestParams(email, password);
        String accessToken = userApi.authorizeUser(authParams).body().as(UserResponseParams.class).getAccessToken();
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("POST/api/orders. Status code and response body when unauthorized user creates an order.")
    public void testCreateOrderCodeAndResponseBodyWhenUserUnauthorized() {
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f");
        CreateOrderRequestParams orderParams = new CreateOrderRequestParams(ingredients);
        Response response = api.createOrder(orderParams, "");
        int statusCode = response.then().extract().statusCode();
        assertEquals("The status code must be 200", 200, statusCode);
    }

    @Test
    @DisplayName("POST/api/orders. Status code and response body when authorized user creates an order.")
    public void testCreateOrderCodeAndResponseBodyWhenUserAuthorized() {
        userApi.createUser(createUserParams);
        AuthUserRequestParams authParams = new AuthUserRequestParams(email, password);
        String accessToken = userApi.authorizeUser(authParams).body().as(UserResponseParams.class).getAccessToken();
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f");
        CreateOrderRequestParams orderParams = new CreateOrderRequestParams(ingredients);
        Response response = api.createOrder(orderParams, accessToken);
        int statusCode = response.then().extract().statusCode();
        assertEquals("The status code must be 200", 200, statusCode);
    }

    @Test
    @DisplayName("POST/api/orders. Status code and response body when unauthorized user creates an empty order.")
    public void testCreateEmptyOrderCodeAndResponseBody() {
        List<String> ingredients = List.of();
        CreateOrderRequestParams orderParams = new CreateOrderRequestParams(ingredients);
        Response response = api.createOrder(orderParams, "");
        int statusCode = response.then().extract().statusCode();
        String responseMessage = response.then().extract().path("message");
        assertEquals("The status code must be 400", 400, statusCode);
        assertEquals("Ingredient ids must be provided", responseMessage);
    }

    @Test
    @DisplayName("POST/api/orders. Status code and response body when creating wrong order.")
    public void testCreateOrderWithWrongIngredients() {
        List<String> ingredients = List.of("someIngredient");
        CreateOrderRequestParams orderParams = new CreateOrderRequestParams(ingredients);
        Response response = api.createOrder(orderParams, "");
        int statusCode = response.then().extract().statusCode();
        assertEquals("The status code must be 500", 500, statusCode);
    }

    @Test
    @DisplayName("GET/api/orders. Status code and response body when getting orders.")
    public void testGetOrdersWhenUserAuthorized() {
        userApi.createUser(createUserParams);
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f");
        AuthUserRequestParams authParams = new AuthUserRequestParams(email, password);
        String accessToken = userApi.authorizeUser(authParams).body().as(UserResponseParams.class).getAccessToken();
        CreateOrderRequestParams orderParams = new CreateOrderRequestParams(ingredients);
        api.createOrder(orderParams, accessToken);
        Response response = api.getOrders(accessToken);
        int statusCode = response.then().extract().statusCode();
        ArrayList<String> responseMessage = response.then().extract().path("orders.ingredients");
        ArrayList<String> responseStatus = response.then().extract().path("orders.status");
        assertEquals("The status code must be 200", 200, statusCode);
        assertEquals("done", responseStatus.get(0));
        assertEquals(ingredients, responseMessage.get(0));
    }

    @Test
    @DisplayName("GET/api/orders. Status code and response body when getting orders.")
    public void testGetOrdersWhenUserUnauthorized() {
        userApi.createUser(createUserParams);
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f");
        CreateOrderRequestParams orderParams = new CreateOrderRequestParams(ingredients);
        api.createOrder(orderParams, "");
        Response response = api.getOrders("");
        int statusCode = response.then().extract().statusCode();
        assertEquals("The status code must be 401", 401, statusCode);
    }
}

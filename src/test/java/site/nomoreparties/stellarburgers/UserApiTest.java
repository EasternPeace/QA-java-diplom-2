package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.helpers.Utils;
import site.nomoreparties.stellarburgers.params.user.request.AuthUserRequestParams;
import site.nomoreparties.stellarburgers.params.user.request.CreateUserRequestParams;
import site.nomoreparties.stellarburgers.params.user.response.User;
import site.nomoreparties.stellarburgers.params.user.response.UserResponseParams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserApiTest {
    private final UserApi api = new UserApi();
    private String email;
    private String password;
    private String name;
    private CreateUserRequestParams createUserParams;

    @Before
    public void setUp() {
        this.email = new Utils().generateRandomEmail();
        this.password = RandomStringUtils.randomAlphabetic(6);
        this.name = RandomStringUtils.randomAlphabetic(8);
        createUserParams = new CreateUserRequestParams(email, password, name);
    }

    @After
    public void tearDown() {
        AuthUserRequestParams authParams = new AuthUserRequestParams(email, password);
        String accessToken = api.authorizeUser(authParams).body().as(UserResponseParams.class).getAccessToken();
        if (accessToken != null) {
            api.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("POST/api/auth/register. Status code and response body when user has been created.")
    public void testCreateUserCodeAndResponseBody() {
        Response response = api.createUser(createUserParams);
        int statusCode = response.then().extract().statusCode();
        UserResponseParams actualResponseBody = response.body().as(UserResponseParams.class);
        assertEquals("The status code must be 200", 200, statusCode);
        assertEquals("true", actualResponseBody.getSuccess());
        assertEquals(email, actualResponseBody.getUser().getEmail());
        assertEquals(name, actualResponseBody.getUser().getName());
        assertNotNull(actualResponseBody.getAccessToken());
        assertNotNull(actualResponseBody.getRefreshToken());
    }

    @Test
    @DisplayName("POST/api/auth/register. Status code and response body when attempt to create a duplicate.")
    public void testCreateDuplicateUserCodeAndResponseBody() {
        api.createUser(createUserParams);
        Response response = api.createUser(createUserParams);
        int statusCode = response.then().extract().statusCode();
        String actualResponseBody = response.then().extract().path("message");
        assertEquals("The status code must be 403", 403, statusCode);
        assertEquals("User already exists", actualResponseBody);
    }

    @Test
    @DisplayName("POST/api/auth/register. Status code and response body when mandatory field is missing.")
    public void testCreateUserWhenMandatoryFieldIsMissingStatusCode() {
        Response response = api.createUser(new CreateUserRequestParams(email, password, null));
        int statusCode = response.then().extract().statusCode();
        String actualResponseBody = response.then().extract().path("message");
        assertEquals("The status code must be 403", 403, statusCode);
        assertEquals("Email, password and name are required fields", actualResponseBody);
    }

    @Test
    @DisplayName("POST/api/auth/login. Status code when logging in.")
    public void testLogInPositive() {
        api.createUser(createUserParams);
        Response response = api.authorizeUser(new AuthUserRequestParams(email, password));
        int statusCode = response.then().extract().statusCode();
        assertEquals("The status code must be 200", 200, statusCode);
    }

    @Test
    @DisplayName("POST/api/auth/login. Status code and response body when password is incorrect.")
    public void testLogInWithIncorrectPassword() {
        api.createUser(createUserParams);
        Response response = api.authorizeUser(new AuthUserRequestParams(email, password + "1"));
        int statusCode = response.then().extract().statusCode();
        String actualResponseBody = response.then().extract().path("message");
        assertEquals("The status code must be 401", 401, statusCode);
        assertEquals("email or password are incorrect", actualResponseBody);
    }

    @Test
    @DisplayName("POST/api/auth/login. Status code and response body when login is incorrect.")
    public void testLogInWithIncorrectLogin() {
        api.createUser(createUserParams);
        Response response = api.authorizeUser(new AuthUserRequestParams("1" + email, password));
        int statusCode = response.then().extract().statusCode();
        String actualResponseBody = response.then().extract().path("message");
        assertEquals("The status code must be 401", 401, statusCode);
        assertEquals("email or password are incorrect", actualResponseBody);
    }

    @Test
    @DisplayName("PATCH/api/auth/login. Status code and response body when updating user name.")
    public void testUpdateUserName() {
        api.createUser(createUserParams);
        AuthUserRequestParams authParams = new AuthUserRequestParams(email, password);
        String accessToken = api.authorizeUser(authParams).body().as(UserResponseParams.class).getAccessToken();
        String newName = name + 1;
        User updatedUser = new User(email, newName);
        name = newName; //Changing the name to delete user correctly in tearDown
        Response response = api.updateUserData(accessToken, updatedUser);
        int statusCode = response.then().extract().statusCode();
        String actualName = response.then().extract().path("user.name");
        assertEquals("The status code must be 200", 200, statusCode);
        assertEquals("The name hasn't been updated", newName, actualName);
    }

    @Test
    @DisplayName("PATCH/api/auth/login. Status code and response body when updating user email.")
    public void testUpdateUserEmail() {
        api.createUser(createUserParams);
        AuthUserRequestParams authParams = new AuthUserRequestParams(email, password);
        String accessToken = api.authorizeUser(authParams).body().as(UserResponseParams.class).getAccessToken();
        String newEmail = email + 1;
        User updatedUser = new User(newEmail, name);
        email = newEmail; //Changing the email to delete user correctly in tearDown
        Response response = api.updateUserData(accessToken, updatedUser);
        int statusCode = response.then().extract().statusCode();
        String actualEmail = response.then().extract().path("user.email");
        assertEquals("The status code must be 200", 200, statusCode);
        assertEquals("The email hasn't been updated", newEmail, actualEmail);
    }

    @Test
    @DisplayName("PATCH/api/auth/login. Status code and response body when updating user email being unauthorized.")
    public void testUpdateUserEmailBeingUnauthorized() {
        api.createUser(createUserParams);
        String newEmail = email + 1;
        User updatedUser = new User(newEmail, name);
        Response response = api.updateUserData("randomToken", updatedUser);
        int statusCode = response.then().extract().statusCode();
        assertEquals("The status code must be 200", 401, statusCode);
    }

    @Test
    @DisplayName("PATCH/api/auth/login. Status code and response body when updating user email being unauthorized.")
    public void testUpdateUserNameBeingUnauthorized() {
        api.createUser(createUserParams);
        String newName = name + 1;
        User updatedUser = new User(email, newName);
        Response response = api.updateUserData("randomToken", updatedUser);
        int statusCode = response.then().extract().statusCode();
        assertEquals("The status code must be 200", 401, statusCode);
    }
}

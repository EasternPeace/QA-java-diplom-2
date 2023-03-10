package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.params.user.request.AuthUserRequestParams;
import site.nomoreparties.stellarburgers.params.user.request.CreateUserRequestParams;
import site.nomoreparties.stellarburgers.params.user.response.User;

import static io.restassured.RestAssured.given;

public class UserApi extends StellaburgersRestClient {
    public Response createUser(CreateUserRequestParams createUserParams) {
        return given().spec(baseSpec())
                .when()
                .body(createUserParams)
                .post("/api/auth/register");
    }

    public Response deleteUser(String accessToken) {
        return given().spec(baseSpec())
                .when()
                .header("authorization", accessToken)
                .delete("/api/auth/user");
    }

    public Response authorizeUser(AuthUserRequestParams authUserRequestParams) {
        return given().spec(baseSpec())
                .when()
                .body(authUserRequestParams)
                .post("/api/auth/login");
    }

    public Response updateUserData(String accessToken, User userDataToUpdate) {
        return given().spec(baseSpec())
                .when()
                .header("authorization", accessToken)
                .body(userDataToUpdate)
                .patch("/api/auth/user");
    }
}

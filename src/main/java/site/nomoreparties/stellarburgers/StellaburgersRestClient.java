package site.nomoreparties.stellarburgers;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class StellaburgersRestClient {
    public final String BASE_URL = "https://stellarburgers.nomoreparties.site/";

    public RequestSpecification baseSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URL)
                .build();
    }
}

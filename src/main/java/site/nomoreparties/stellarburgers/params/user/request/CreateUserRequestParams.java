package site.nomoreparties.stellarburgers.params.user.request;

import site.nomoreparties.stellarburgers.params.IParams;

public class CreateUserRequestParams implements IParams {
    private String email;
    private String password;
    private String name;

    public CreateUserRequestParams(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public CreateUserRequestParams() {
    }
}

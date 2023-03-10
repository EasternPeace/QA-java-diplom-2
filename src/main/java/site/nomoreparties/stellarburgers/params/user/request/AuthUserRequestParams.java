package site.nomoreparties.stellarburgers.params.user.request;

public class AuthUserRequestParams {
    private String email;
    private String password;

    public AuthUserRequestParams(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthUserRequestParams() {
    }
}

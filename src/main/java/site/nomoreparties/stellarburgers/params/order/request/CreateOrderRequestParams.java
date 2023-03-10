package site.nomoreparties.stellarburgers.params.order.request;

import site.nomoreparties.stellarburgers.params.IParams;

import java.util.List;

public class CreateOrderRequestParams implements IParams {
    private List<String> ingredients;

    public CreateOrderRequestParams(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}

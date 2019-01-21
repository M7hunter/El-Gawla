package it_geeks.info.gawla_app.Repositry.RESTful;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.Repositry.Models.Category;
import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Models.RoundRealTimeModel;
import it_geeks.info.gawla_app.Repositry.Models.User;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;

public class ParseResponses {

    public ParseResponses() {
    }

    public static List<Round> parseRounds(JsonObject object, GawlaDataBse gawlaDataBse) {
        List<Round> rounds = new ArrayList<>();
        JsonArray roundsArray = object.get("salons").getAsJsonArray();

        for (int i = 0; i < roundsArray.size(); i++) {
            JsonObject roundObj = roundsArray.get(i).getAsJsonObject();
            int product_id = roundObj.get("product_id").getAsInt();
            int salon_id = roundObj.get("salon_id").getAsInt();
            String product_name = roundObj.get("product_name").getAsString();
            String category_name = roundObj.get("category_name").getAsString();
            String category_color = roundObj.get("category_color").getAsString();
            String country_name = roundObj.get("country_name").getAsString();
            String product_commercial_price = roundObj.get("product_commercial_price").getAsString();
            String product_product_description = roundObj.get("product_description").getAsString();
            String product_image = roundObj.get("product_image").getAsString();
            String round_start_time = roundObj.get("round_start_time").getAsString();
            String round_end_time = roundObj.get("round_end_time").getAsString();
            String first_join_time = roundObj.get("first_join_time").getAsString();
            String second_join_time = roundObj.get("second_join_time").getAsString();
            String round_date = roundObj.get("round_date").getAsString();
            String round_time = roundObj.get("round_time").getAsString();
            String rest_time = roundObj.get("rest_time").getAsString();

            // save product images in locale storage
            gawlaDataBse.productImageDao().removeSubImages(gawlaDataBse.productImageDao().getSubImagesById(product_id));
            gawlaDataBse.productImageDao().insertSubImages(parseImages(roundObj, product_id));

            // save product cards in locale storage
            gawlaDataBse.cardDao().removeCards(gawlaDataBse.cardDao().getCardsById(salon_id));
            gawlaDataBse.cardDao().insertCards(parseSalonCards(roundObj, salon_id));

            rounds.add(
                    new Round(product_id,
                            salon_id,
                            product_name,
                            category_name,
                            category_color,
                            country_name,
                            product_commercial_price,
                            product_product_description,
                            product_image,
                            round_start_time,
                            round_end_time,
                            first_join_time,
                            second_join_time,
                            round_date,
                            round_time,
                            rest_time));
        }

        return rounds;
    }

    private static List<ProductSubImage> parseImages(JsonObject roundObj, int product_id) {
        JsonArray product_images = roundObj.get("product_images").getAsJsonArray();

        List<ProductSubImage> subImagesList = new ArrayList<>();

        for (int i = 0; i < product_images.size(); i++) {
            ProductSubImage subImage = new ProductSubImage(product_id, product_images.get(i).getAsString());
            subImagesList.add(subImage);
        }

        return subImagesList;
    }

    private static List<Card> parseSalonCards(JsonObject roundObj, int salon_id) {
        JsonArray salon_cards = roundObj.get("salon_cards").getAsJsonArray();

        List<Card> salon_cardsList = new ArrayList<>();

        for (int j = 0; j < salon_cards.size(); j++) {
            JsonObject cardObj = salon_cards.get(j).getAsJsonObject();
//            int card_id = cardObj.get("id").getAsInt();
            String card_name = cardObj.get("name").getAsString();
            String card_details = cardObj.get("details").getAsString();
            String card_type = cardObj.get("type").getAsString();
            String card_color = cardObj.get("color").getAsString();
            String card_cost = cardObj.get("cost").getAsString();

            salon_cardsList.add(new Card(salon_id, card_name, card_details, card_type, card_color, card_cost));
        }

        return salon_cardsList;
    }

    public static RoundRealTimeModel parseRoundRealTime(JsonObject roundObj){
        JsonObject roundtime = roundObj.get("hall").getAsJsonObject();

        JsonObject open_hall = roundtime.get("open_hall").getAsJsonObject();
        boolean open_hall_status = open_hall.get("status").getAsBoolean();
        int open_hall_value = open_hall.get("value").getAsInt();

        JsonObject free_join = roundtime.get("free_join").getAsJsonObject();
        boolean free_join_status = free_join.get("status").getAsBoolean();
        int free_join_value = free_join.get("value").getAsInt();

        JsonObject pay_join = roundtime.get("pay_join").getAsJsonObject();
        boolean pay_join_status = pay_join.get("status").getAsBoolean();
        int pay_join_value = pay_join.get("value").getAsInt();

        JsonObject first_round = roundtime.get("first_round").getAsJsonObject();
        boolean first_round_status = first_round.get("status").getAsBoolean();
        int first_round_value = first_round.get("value").getAsInt();

        JsonObject first_rest = roundtime.get("first_rest").getAsJsonObject();
        boolean first_rest_status = first_rest.get("status").getAsBoolean();
        int first_rest_value = first_rest.get("value").getAsInt();

        JsonObject seconed_round = roundtime.get("seconed_round").getAsJsonObject();
        boolean seconed_round_status = seconed_round.get("status").getAsBoolean();
        int seconed_round_value = seconed_round.get("value").getAsInt();

        JsonObject seconed_rest = roundtime.get("seconed_rest").getAsJsonObject();
        boolean seconed_rest_status = seconed_rest.get("status").getAsBoolean();
        int seconed_rest_value = seconed_rest.get("value").getAsInt();

        JsonObject close_hall = roundtime.get("close_hall").getAsJsonObject();
        boolean close_hall_status = close_hall.get("status").getAsBoolean();
        int close_hall_value = close_hall.get("value").getAsInt();

        String round_status = roundtime.get("status").getAsString();
        boolean isUserJoin = false ;
         isUserJoin = roundObj.get("isUserJoin").getAsBoolean();

        return new RoundRealTimeModel(
                open_hall_status,
                open_hall_value,
                free_join_status,
                free_join_value,
                pay_join_status,
                pay_join_value,
                first_round_status,
                first_round_value,
                first_rest_status,
                first_rest_value,
                seconed_round_status,
                seconed_round_value,
                seconed_rest_status,
                seconed_rest_value,
                close_hall_status,
                close_hall_value,
                round_status,
                isUserJoin);
    }

    public static List<Country> parseCountries(JsonObject object) {
        List<Country> countries = new ArrayList<>();
        JsonArray roundsArray = object.get("countries").getAsJsonArray();

        for (int i = 0; i < roundsArray.size(); i++) {
            JsonObject roundObj = roundsArray.get(i).getAsJsonObject();
            int country_id = roundObj.get("country_id").getAsInt();
            String country_title = roundObj.get("country_title").getAsString();
            String count_code = roundObj.get("count_code").getAsString();
            String country_timezone = roundObj.get("country_timezone").getAsString();
            String tel = roundObj.get("tel").getAsString();
            String image = roundObj.get("image").getAsString();

            countries.add(
                    new Country(country_id, country_title, count_code, country_timezone, tel, image));
        }

        return countries;
    }

    public static User parseUser(JsonObject object) {
        JsonObject userObj = object.get("user").getAsJsonObject();

        return new User(userObj.get("user_id").getAsInt(),
                userObj.get("api_token").getAsString(),
                userObj.get("name").getAsString(),
                userObj.get("country_id").getAsInt(),
                userObj.get("image").getAsString(),
                userObj.get("email").getAsString(),
                userObj.get("membership").getAsString(),
                userObj.get("gender").getAsString(),
                userObj.get("firstName").getAsString(),
                userObj.get("lastName").getAsString(),
                userObj.get("phone").getAsString());
    }

    public static List<Category> parseCategories(JsonObject object) {
        List<Category> categories = new ArrayList<>();
        JsonArray categoriesArray = object.get("categories").getAsJsonArray();

        for (int i = 0; i < categoriesArray.size(); i++) {
            JsonObject categoryObj = categoriesArray.get(i).getAsJsonObject();
            int category_id = categoryObj.get("category_id").getAsInt();
            String category_name = categoryObj.get("category_name").getAsString();
            String category_color = categoryObj.get("category_color").getAsString();

            categories.add(
                    new Category(category_id, category_name, category_color, parseCards(categoryObj)));
        }

        return categories;
    }

    private static List<Card> parseCards(JsonObject object) {
        JsonArray dataArray = object.get("cards").getAsJsonArray();

        List<Card> cardsList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++) {
            JsonObject cardObj = dataArray.get(i).getAsJsonObject();
            int card_id = cardObj.get("card_id").getAsInt();
            String card_name = cardObj.get("card_name").getAsString();
            String card_details = cardObj.get("card_details").getAsString();
            String card_type = cardObj.get("type").getAsString();
            String card_color = cardObj.get("color_code").getAsString();
            String card_cost = cardObj.get("cost").getAsString();

            cardsList.add(
                    new Card(card_id, card_name, card_details, card_type, card_color, card_cost));
        }

        return cardsList;
    }

    public static String parseServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
    }

}

package it_geeks.info.gawla_app.repository.RESTful;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.repository.Models.Category;
import it_geeks.info.gawla_app.repository.Models.Country;
import it_geeks.info.gawla_app.repository.Models.Notifications;
import it_geeks.info.gawla_app.repository.Models.TopTen;
import it_geeks.info.gawla_app.repository.Models.Trans;
import it_geeks.info.gawla_app.repository.Models.ProductSubImage;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.repository.Models.RoundRemainingTime;
import it_geeks.info.gawla_app.repository.Models.User;
import it_geeks.info.gawla_app.repository.Models.WebPage;

public class ParseResponses {

    public ParseResponses() {
    }

    public static List<Round> parseRounds(JsonObject object) {
        List<Round> rounds = new ArrayList<>();
        JsonArray roundsArray = object.get("salons").getAsJsonArray();

        for (int i = 0; i < roundsArray.size(); i++) {
            JsonObject roundObj = roundsArray.get(i).getAsJsonObject();
            int product_id = roundObj.get("product_id").getAsInt();
            int salon_id = roundObj.get("salon_id").getAsInt();
            JsonObject salon_statusObj = roundObj.get("salon_status").getAsJsonObject();

            rounds.add(
                    new Round(product_id,
                            salon_id,
                            roundObj.get("round_id").getAsInt(),
                            roundObj.get("product_name").getAsString(),
                            roundObj.get("category_name").getAsString(),
                            roundObj.get("category_color").getAsString(),
                            roundObj.get("country_name").getAsString(),
                            roundObj.get("product_commercial_price").getAsString(),
                            roundObj.get("product_description").getAsString(),
                            roundObj.get("product_image").getAsString(),
                            parseSubImages(roundObj, product_id),
                            parseSalonCards(roundObj, salon_id),
                            roundObj.get("round_date").getAsString(),
                            salon_statusObj.get("status").getAsBoolean(),
                            salon_statusObj.get("message").getAsString()));
        }

        return rounds;
    }

    public static Round parseRoundByID(JsonObject object) {
        JsonObject roundObj = object.get("salons").getAsJsonObject();

        int product_id = roundObj.get("product_id").getAsInt();
        int salon_id = roundObj.get("salon_id").getAsInt();
        JsonObject salon_statusObj = roundObj.get("salon_status").getAsJsonObject();

        return new Round(product_id,
                salon_id,
                roundObj.get("round_id").getAsInt(),
                roundObj.get("product_name").getAsString(),
                roundObj.get("category_name").getAsString(),
                roundObj.get("category_color").getAsString(),
                roundObj.get("country_name").getAsString(),
                roundObj.get("product_commercial_price").getAsString(),
                roundObj.get("product_description").getAsString(),
                roundObj.get("product_image").getAsString(),
                parseSubImages(roundObj, product_id),
                parseSalonCards(roundObj, salon_id),
                roundObj.get("round_date").getAsString(),
                salon_statusObj.get("status").getAsBoolean(),
                salon_statusObj.get("message").getAsString());
    }

    private static List<ProductSubImage> parseSubImages(JsonObject roundObj, int product_id) {
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
            int card_id = cardObj.get("id").getAsInt();
            String card_name = cardObj.get("name").getAsString();
            String card_details = cardObj.get("details").getAsString();
            String card_color = cardObj.get("color").getAsString();
            String card_cost = cardObj.get("cost").getAsString();
            String card_type = cardObj.get("type").getAsString();

            salon_cardsList.add(new Card(card_id, salon_id, card_name, card_details, card_type, card_color, card_cost));
        }

        return salon_cardsList;
    }

    public static RoundRemainingTime parseRoundRealTime(JsonObject roundObj) {
        JsonObject roundTime = roundObj.get("hall").getAsJsonObject();

        JsonObject open_hall = roundTime.get("open_hall").getAsJsonObject();
        boolean open_hall_status = open_hall.get("status").getAsBoolean();
        int open_hall_value = open_hall.get("value").getAsInt();

        JsonObject free_join = roundTime.get("free_join").getAsJsonObject();
        boolean free_join_status = free_join.get("status").getAsBoolean();
        int free_join_value = free_join.get("value").getAsInt();

        JsonObject pay_join = roundTime.get("pay_join").getAsJsonObject();
        boolean pay_join_status = pay_join.get("status").getAsBoolean();
        int pay_join_value = pay_join.get("value").getAsInt();

        JsonObject first_round = roundTime.get("first_round").getAsJsonObject();
        boolean first_round_status = first_round.get("status").getAsBoolean();
        int first_round_value = first_round.get("value").getAsInt();

        JsonObject first_rest = roundTime.get("first_rest").getAsJsonObject();
        boolean first_rest_status = first_rest.get("status").getAsBoolean();
        int first_rest_value = first_rest.get("value").getAsInt();

        JsonObject second_round = roundTime.get("seconed_round").getAsJsonObject();
        boolean second_round_status = second_round.get("status").getAsBoolean();
        int second_round_value = second_round.get("value").getAsInt();

        JsonObject second_rest = roundTime.get("seconed_rest").getAsJsonObject();
        boolean second_rest_status = second_rest.get("status").getAsBoolean();
        int second_rest_value = second_rest.get("value").getAsInt();

        JsonObject close_hall = roundTime.get("close_hall").getAsJsonObject();
        boolean close_hall_status = close_hall.get("status").getAsBoolean();
        int close_hall_value = close_hall.get("value").getAsInt();

        String round_status = roundTime.get("status").getAsString();
        boolean isUserJoin = roundObj.get("isUserJoin").getAsBoolean();
        int last_round_id = roundObj.get("last_round_id").getAsInt();

        return new RoundRemainingTime(
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
                second_round_status,
                second_round_value,
                second_rest_status,
                second_rest_value,
                close_hall_status,
                close_hall_value,
                round_status,
                isUserJoin,
                last_round_id);
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
                    new Category(category_id, category_name, category_color));
        }

        return categories;
    }

    public static List<Card> parseCards(JsonObject object) {
        JsonArray dataArray = object.get("cards").getAsJsonArray();

        List<Card> cardsList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++) {
            JsonObject cardObj = dataArray.get(i).getAsJsonObject();
            int card_id = cardObj.get("card_id").getAsInt();
            String card_name = cardObj.get("card_name").getAsString();
            String card_details = cardObj.get("card_details").getAsString();
            String card_color = cardObj.get("color_code").getAsString();
            String cost = cardObj.get("cost").getAsString();

            cardsList.add(
                    new Card(card_id, card_name, card_details, card_color, cost));
        }

        return cardsList;
    }

    public static List<Card> parseUserCardsBySalon(JsonObject object) {
        JsonArray cardsArray = object.get("cards").getAsJsonArray();

        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < cardsArray.size(); i++) {
            JsonObject cardObj = cardsArray.get(i).getAsJsonObject();
            int cardId = cardObj.get("card_id").getAsInt();
            int count = cardObj.get("count").getAsInt();
            String type = cardObj.get("card_type").getAsString();

            cards.add(new Card(cardId, count, type));
        }

        return cards;
    }

    public static List<TopTen> parseTopTen(JsonObject object) {
        JsonArray dataArray = object.get("top").getAsJsonArray();

        List<TopTen> topTenList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++) {
            JsonObject userObj = dataArray.get(i).getAsJsonObject();
            int userId = userObj.get("id").getAsInt();
            String userName = userObj.get("user").getAsString();
            String userOffer = userObj.get("offer").getAsString();
            if (userId != 0)
                topTenList.add(new TopTen(userId, userName, userOffer));
        }

        return topTenList;
    }

    public static List<Trans> parseLanguages(JsonObject object) {
        return null;
    }

    public static String parseServerErrors(JsonObject object) {
        String error = "no errors";
        try {
            JsonArray errors = object.get("errors").getAsJsonArray();
            for (int i = 0; i < errors.size(); i++) {
                error = errors.get(i).getAsString();
            }
        } catch (NullPointerException e) {
        }
        return error;
    }

    public static List<WebPage> parseWebPages(JsonObject object) {
        JsonArray dataArray = object.get("pages").getAsJsonArray();

        List<WebPage> webPageList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++) {
            JsonObject cardObj = dataArray.get(i).getAsJsonObject();
            int page_id = cardObj.get("page_id").getAsInt();
            String page_title = cardObj.get("page_title").getAsString();
            String page_link = cardObj.get("page_link").getAsString();

            webPageList.add(
                    new WebPage(page_id, page_title, page_link));
        }

        return webPageList;
    }

    // parse Notifications
    public static List<Notifications> parseNotifications(JsonObject object) {

        List<Notifications> notificationList = new ArrayList<>();
        JsonArray notificationsArr = object.get("notifications").getAsJsonArray();

        for (int i = 0; i < notificationsArr.size(); i++) {
            JsonObject notificationObj = notificationsArr.get(i).getAsJsonObject();

            notificationList.add(new Notifications(
                    notificationObj.get("title").getAsString(),
                    notificationObj.get("body").getAsString(),
                    notificationObj.get("type").getAsString(),
                    notificationObj.get("date").getAsString(),
                    notificationObj.get("id").getAsInt(),
                    false));
        }
        return notificationList;

    }
}
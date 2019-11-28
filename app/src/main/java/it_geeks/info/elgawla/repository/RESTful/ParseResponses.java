package it_geeks.info.elgawla.repository.RESTful;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

import it_geeks.info.elgawla.repository.Models.Ad;
import it_geeks.info.elgawla.repository.Models.Invoice;
import it_geeks.info.elgawla.repository.Models.Card;
import it_geeks.info.elgawla.repository.Models.Category;
import it_geeks.info.elgawla.repository.Models.Country;
import it_geeks.info.elgawla.repository.Models.MyCardModel;
import it_geeks.info.elgawla.repository.Models.Notification;
import it_geeks.info.elgawla.repository.Models.Package;
import it_geeks.info.elgawla.repository.Models.SalonArchiveModel;
import it_geeks.info.elgawla.repository.Models.TopTen;
import it_geeks.info.elgawla.repository.Models.ProductSubImage;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.repository.Models.RoundRemainingTime;
import it_geeks.info.elgawla.repository.Models.User;
import it_geeks.info.elgawla.repository.Models.Vote;
import it_geeks.info.elgawla.repository.Models.VoteChild;
import it_geeks.info.elgawla.repository.Models.WebPage;
import it_geeks.info.elgawla.repository.Models.WinnerNews;
import it_geeks.info.elgawla.util.Constants;

public class ParseResponses {

    public static List<Salon> parseRounds(JsonObject object) {
        List<Salon> salons = new ArrayList<>();
        JsonArray roundsArray = object.get("salons").getAsJsonArray();

        for (int i = 0; i < roundsArray.size(); i++)
        {
            JsonObject roundObj = roundsArray.get(i).getAsJsonObject();
            int product_id = roundObj.get("product_id").getAsInt();
            int salon_id = roundObj.get("salon_id").getAsInt();
            JsonObject salon_statusObj = roundObj.get("salon_status").getAsJsonObject();

            salons.add(
                    new Salon(product_id,
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
                            roundObj.get("is_closed").getAsBoolean(),
                            salon_statusObj.get("status").getAsBoolean(),
                            salon_statusObj.get("message").getAsString()));
        }

        return salons;
    }

    public static Salon parseRoundByID(JsonObject object) {
        JsonObject roundObj = object.get("salons").getAsJsonObject();

        int product_id = roundObj.get("product_id").getAsInt();
        int salon_id = roundObj.get("salon_id").getAsInt();
        JsonObject salon_statusObj = roundObj.get("salon_status").getAsJsonObject();

        return new Salon(product_id,
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
                roundObj.get("is_closed").getAsBoolean(),
                salon_statusObj.get("status").getAsBoolean(),
                salon_statusObj.get("message").getAsString());
    }

    private static List<ProductSubImage> parseSubImages(JsonObject roundObj, int product_id) {
        JsonArray product_images = roundObj.get("product_images").getAsJsonArray();

        List<ProductSubImage> subImagesList = new ArrayList<>();

        for (int i = 0; i < product_images.size(); i++)
        {
            ProductSubImage subImage = new ProductSubImage(product_id, product_images.get(i).getAsString());
            subImagesList.add(subImage);
        }

        return subImagesList;
    }

    private static List<Card> parseSalonCards(JsonObject roundObj, int salon_id) {
        JsonArray salon_cards = roundObj.get("salon_cards").getAsJsonArray();

        List<Card> salon_cardsList = new ArrayList<>();

        for (int j = 0; j < salon_cards.size(); j++)
        {
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

    public static RoundRemainingTime parseRoundRemainingTime(JsonObject roundObj) {
        JsonObject states = roundObj.get("hall").getAsJsonObject();

        JsonObject open_hall = states.get("open_hall").getAsJsonObject();
        JsonObject free_join = states.get("free_join").getAsJsonObject();
        JsonObject pay_join = states.get("pay_join").getAsJsonObject();
        JsonObject close_hall = states.get("close_hall").getAsJsonObject();
        JsonObject round_time = states.get("round_time").getAsJsonObject();
        JsonObject round_rest = states.get("round_rest").getAsJsonObject();

        return new RoundRemainingTime(
                open_hall.get("status").getAsBoolean(),
                open_hall.get("value").getAsInt(),
                free_join.get("status").getAsBoolean(),
                free_join.get("value").getAsInt(),
                pay_join.get("status").getAsBoolean(),
                pay_join.get("value").getAsInt(),
                close_hall.get("status").getAsBoolean(),
                round_time.get("status").getAsBoolean(),
                round_time.get("value").getAsInt(),
                round_rest.get("status").getAsBoolean(),
                round_rest.get("value").getAsInt(),
                states.get("round_count").getAsString(),
                states.get("status").getAsString(),
                states.get("round_msg").getAsString(),
                roundObj.get("round_date").getAsString(),
                roundObj.get("isToday").getAsBoolean(),
                roundObj.get("last_round_id").getAsInt(),
                roundObj.get("isUserJoin").getAsBoolean());
    }

    public static List<Country> parseCountries(JsonObject object) {
        List<Country> countries = new ArrayList<>();
        JsonArray roundsArray = object.get("countries").getAsJsonArray();

        for (int i = 0; i < roundsArray.size(); i++)
        {
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
                userObj.get("gender").getAsString(),
                userObj.get("phone").getAsString());
    }

    public static List<Category> parseCategories(JsonObject object) {
        List<Category> categories = new ArrayList<>();
        JsonArray categoriesArray = object.get("categories").getAsJsonArray();

        for (int i = 0; i < categoriesArray.size(); i++)
        {
            JsonObject categoryObj = categoriesArray.get(i).getAsJsonObject();
            int category_id = categoryObj.get("category_id").getAsInt();
            String category_name = categoryObj.get("category_name").getAsString();
            String category_image = categoryObj.get("category_image").getAsString();
            String category_color = categoryObj.get("category_color").getAsString();

            categories.add(
                    new Category(category_id, category_name, category_color, category_image));
        }

        return categories;
    }

    public static List<Card> parseCards(JsonObject object) {
        JsonArray dataArray = object.get("cards").getAsJsonArray();

        List<Card> cardsList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++)
        {
            JsonObject cardObj = dataArray.get(i).getAsJsonObject();
            int card_id = cardObj.get("card_id").getAsInt();
            String card_name = cardObj.get("card_name").getAsString();
            String card_details = cardObj.get("card_details").getAsString();
            String card_color = cardObj.get("color_code").getAsString();
            String card_type = cardObj.get("card_type").getAsString();
            String cost = cardObj.get("cost").getAsString();

            cardsList.add(
                    new Card(card_id, card_name, card_details, card_color, cost, card_type));
        }

        return cardsList;
    }

    public static List<MyCardModel> parseMyCards(JsonObject object) {
        JsonArray dataArray = object.get("cards").getAsJsonArray();

        List<MyCardModel> cardsList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++)
        {
            JsonObject cardObj = dataArray.get(i).getAsJsonObject();
            int card_id = cardObj.get("card_id").getAsInt();
            String card_color = cardObj.get("card_color").getAsString();
            String card_category = cardObj.get("card_category").getAsString();
            boolean card_status = cardObj.get("card_status").getAsBoolean();

            int salon_id;
            try
            {
                salon_id = cardObj.get("salon_id").getAsInt();
            }
            catch (UnsupportedOperationException e)
            {
                salon_id = Constants.NULL_INT_VALUE;
            }

            cardsList.add(
                    new MyCardModel(card_id, card_color, card_category, card_status, salon_id));
        }

        return cardsList;
    }

    public static List<Salon> parseSalonsArchive(JsonObject object) {
        JsonArray dataArray = object.get("salons").getAsJsonArray();

        List<Salon> salonsList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++)
        {
            JsonObject cardObj = dataArray.get(i).getAsJsonObject();
            String product_image = cardObj.get("product_image").getAsString();
            String product_name = cardObj.get("product_name").getAsString();
            String salon_date = cardObj.get("salon_date").getAsString();
            boolean isWinner = cardObj.get("isWinner").getAsBoolean();
            int salon_id = cardObj.get("salon_id").getAsInt();

            salonsList.add(new Salon(product_image, product_name, salon_date, isWinner, salon_id));
        }

        return salonsList;
    }

    public static List<Category> parseHomeCategories(JsonObject object) {
        JsonObject homeObj = object.get("home").getAsJsonObject();
        JsonArray categoriesArr = homeObj.get("categories").getAsJsonArray();

        List<Category> categories = new ArrayList<>();

        for (int i = 0; i < categoriesArr.size(); i++)
        {
            JsonObject catObj = categoriesArr.get(i).getAsJsonObject();

            int category_id = catObj.get("category_id").getAsInt();
            String category_name = catObj.get("category_name").getAsString();
            String category_color = catObj.get("category_color").getAsString();
            String category_image = catObj.get("category_image").getAsString();

            categories.add(new Category(category_id, category_name, category_color, category_image));
        }


        return categories;
    }

    public static List<Ad> parseAds(JsonObject object) {
        JsonObject obj = object.get("home").getAsJsonObject();
        JsonArray dataArray = obj.get("sliders").getAsJsonArray();

        List<Ad> adsList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++)
        {
            JsonObject sliderObj = dataArray.get(i).getAsJsonObject();
            int slider_id = sliderObj.get("slider_id").getAsInt();
            int slider_salon_id = sliderObj.get("slider_salon_id").getAsInt();
            String slider_name = sliderObj.get("slider_name").getAsString();
            String slider_description = sliderObj.get("slider_description").getAsString();
            String slider_image = sliderObj.get("slider_image").getAsString();
            boolean slider_type = sliderObj.get("slider_type").getAsBoolean();

            adsList.add(
                    new Ad(slider_id, slider_salon_id, slider_name, slider_description, slider_image, slider_type));
        }

        return adsList;
    }

    public static List<WinnerNews> parseWinners(JsonObject object) {
        JsonArray dataArray = object.get("blogs").getAsJsonArray();

        List<WinnerNews> winnerNewsList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++)
        {
            JsonObject sliderObj = dataArray.get(i).getAsJsonObject();
            int blog_id = sliderObj.get("blog_id").getAsInt();
            String blog_category = sliderObj.get("blog_category").getAsString();
            String bog_title = sliderObj.get("bog_title").getAsString();
            String blog_description = sliderObj.get("blog_description").getAsString();
            String product_name = sliderObj.get("product_name").getAsString();
            String user_name = sliderObj.get("user_name").getAsString();

            winnerNewsList.add(
                    new WinnerNews(blog_id,
                            blog_category,
                            bog_title,
                            blog_description,
                            product_name,
                            user_name,
                            parseBlogImages(sliderObj.get("blog_images").getAsJsonArray())));
        }

        return winnerNewsList;
    }

    private static List<String> parseBlogImages(JsonArray blog_imagesArr) {
        List<String> blog_images = new ArrayList<>();
        for (int i = 0; i < blog_imagesArr.size(); i++)
        {
            blog_images.add(blog_imagesArr.get(i).getAsString());
        }

        return blog_images;
    }

    public static ArrayList<List<Package>> parsePackages(JsonObject object) {
        JsonArray packagesArray = object.get("packages").getAsJsonArray();
        ArrayList<List<Package>> lists = new ArrayList<>();

        for (int i = 0; i < packagesArray.size(); i++)
        {
            List<Package> packages = new ArrayList<>();
            JsonArray packagesCatArr = packagesArray.get(i).getAsJsonArray();
            for (int j = 0; j < packagesCatArr.size(); j++)
            {
                JsonObject packageObj = packagesCatArr.get(j).getAsJsonObject();

                int package_id = packageObj.get("package_id").getAsInt();
                String package_name = packageObj.get("package_name").getAsString();
                String package_category_name = packageObj.get("package_category_name").getAsString();
                String package_description = packageObj.get("package_description").getAsString();
                String package_color = packageObj.get("package_color").getAsString();
                String package_cost = packageObj.get("package_cost").getAsString();
                boolean package_is_discount = packageObj.get("package_is_discount").getAsBoolean();
                int package_salons_no = packageObj.get("package_salons_no").getAsInt();
                String package_discount = packageObj.get("package_discount").getAsString();
                String package_discount_date_from = packageObj.get("package_discount_date_from").getAsString();
                String package_discount_date_to = packageObj.get("package_discount_date_to").getAsString();

                packages.add(new Package(package_id, package_name, package_category_name, package_description, package_color, package_cost, package_is_discount, package_salons_no, package_discount, package_discount_date_from, package_discount_date_to));
            }
            lists.add(packages);
        }

        return lists;
    }

    public static List<Invoice> parseInvoices(JsonObject object) {
        JsonArray packagesArray = object.get("invoices").getAsJsonArray();

        List<Invoice> invoicesList = new ArrayList<>();
        for (int i = 0; i < packagesArray.size(); i++)
        {
            JsonObject cardObj = packagesArray.get(i).getAsJsonObject();
            int id = cardObj.get("id").getAsInt();
            String total = cardObj.get("total").getAsString();
            String option_type = cardObj.get("option_type").getAsString();
            String status = cardObj.get("status").getAsString();
            String created_at = cardObj.get("created_at").getAsString();

            invoicesList.add(new Invoice(id, total, option_type, status, created_at));
        }

        return invoicesList;
    }

    public static List<Vote> parseVotes(JsonObject object) {
        JsonArray votesArray = object.get("votes").getAsJsonArray();

        List<Vote> votes = new ArrayList<>();
        for (int i = 0; i < votesArray.size(); i++)
        {
            JsonObject voteObj = votesArray.get(i).getAsJsonObject();
            int vote_id = voteObj.get("vote_id").getAsInt();
            String vote_title = voteObj.get("vote_name").getAsString();
            String vote_count = voteObj.get("count").getAsString();
//            String icon = voteObj.get("icon").getAsString();
//            String color = voteObj.get("color").getAsString();
//            boolean voted = voteObj.get("voted").getAsBoolean();

            votes.add(new Vote(vote_id, vote_title, vote_count));
        }

        return votes;
    }

    private static List<VoteChild> parseVoteChildes(JsonObject voteObj) {
        JsonArray childesArray = voteObj.get("votes").getAsJsonArray();

        List<VoteChild> childes = new ArrayList<>();
        for (int i = 0; i < childesArray.size(); i++)
        {
            JsonObject childObj = childesArray.get(i).getAsJsonObject();
            int vote_id = childObj.get("vote_id").getAsInt();
            String vote_title = childObj.get("vote_title").getAsString();
            String vote_count = childObj.get("vote_count").getAsString();
            boolean voted = childObj.get("voted").getAsBoolean();

            childes.add(new VoteChild(vote_id, vote_title, vote_count, voted));
        }

        return childes;
    }

    public static List<Card> parseUserCardsBySalon(JsonObject object) {
        JsonArray cardsArray = object.get("cards").getAsJsonArray();

        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < cardsArray.size(); i++)
        {
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

        for (int i = 0; i < dataArray.size(); i++)
        {
            JsonObject userObj = dataArray.get(i).getAsJsonObject();
            int userId = userObj.get("id").getAsInt();
            String userName = userObj.get("user").getAsString();
            String userOffer = userObj.get("offer").getAsString();
            if (userId != 0)
                topTenList.add(new TopTen(userId, userName, userOffer));
        }

        return topTenList;
    }

    static String parseServerErrors(JsonObject object) {
        String error = "";
        try
        {
            JsonArray errors = object.get("errors").getAsJsonArray();
            for (int i = 0; i < errors.size(); i++)
            {
                error = errors.get(i).getAsString();
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

        return error;
    }

    public static List<WebPage> parseWebPages(JsonObject object) {
        JsonArray dataArray = object.get("pages").getAsJsonArray();

        List<WebPage> webPageList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++)
        {
            JsonObject cardObj = dataArray.get(i).getAsJsonObject();
            int page_id = cardObj.get("page_id").getAsInt();
            String page_title = cardObj.get("page_title").getAsString();
            String page_link = cardObj.get("page_link").getAsString();

            webPageList.add(
                    new WebPage(page_id, page_title, page_link));
        }

        return webPageList;
    }

    public static List<Notification> parseNotifications(JsonObject object) {
        JsonArray notificationsArr = object.get("notifications").getAsJsonArray();
        List<Notification> notificationList = new ArrayList<>();

        for (int i = 0; i < notificationsArr.size(); i++)
        {
            JsonObject notificationObj = notificationsArr.get(i).getAsJsonObject();

            notificationList.add(
                    new Notification(
                            notificationObj.get("id").getAsInt(),
                            notificationObj.get("title").getAsString(),
                            notificationObj.get("body").getAsString(),
                            notificationObj.get("type").getAsString(),
                            notificationObj.get("date").getAsString(),
                            false));
        }

        return notificationList;
    }
}
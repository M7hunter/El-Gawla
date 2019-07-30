package it_geeks.info.gawla_app.util;

public class Constants {

    // region payment methods
    public static final String VISA = "visa";
    public static final String MASTERCARD = "mastercard";
    public static final String FAWRY = "fawry";
    public static final String PAYPAL = "paypal";
    public static final String GOOGLEPAY = "googlepay";
    // endregion

    // region requests actions
    static final String REQ_SET_FIREBASE_TOKEN = "setUserFirebaseToken";
    public static final String REQ_GET_USER_NOTIFICATION = "getAllUserNotification";
    public static final String REQ_GET_ALL_VOTES = "getAllVotes";
    public static final String REQ_SET_VOTE = "setVote";
    public static final String REQ_GET_ALL_PAGES = "getAllPages";
    public static final String REQ_GET_ALL_SLIDERS = "getAllSliders";
    public static final String REQ_GET_ALL_SALONS = "getAllSalons";
    public static final String REQ_GET_ALL_BLOGS = "getAllBlogs";
    public static final String REQ_GET_ALL_CATEGORIES = "getAllCategories";
    public static final String REQ_GET_ALL_COUNTRIES = "getAllCountries";
    public static final String REQ_GET_ALL_CARDS = "getAllCards";
    public static final String REQ_GET_CARDS_BY_CATEGORY = "getCardByCategoryID";
    public static final String REQ_GET_ALL_PACKAGES = "getAllPackages";
    public static final String REQ_GET_PAYMENT_PAGE = "getPaymentPage";
    public static final String REQ_GET_SALON_BY_ID = "getSalonByID";
    public static final String REQ_GET_SALONS_ARCHIVE = "getSalonsArchive";
    public static final String REQ_GET_MY_CARDS = "getMyCards";

    // salon
    public static final String REQ_GET_SALON_BY_USER_ID = "getSalonByUserID";
    public static final String REQ_GET_SALON_WITH_REALTIME = "getSalonWithRealTime";
    public static final String REQ_GET_USER_CARDS_BY_SALON = "getUserCardsBySalonId";
    public static final String REQ_GET_TOP_TEN = "getTopTen";
    public static final String REQ_GET_WINNER = "getWinner";
    public static final String REQ_SET_USER_SALON = "setUserSalon";
    public static final String REQ_SET_ROUND_LEAVE = "setRoundLeave";
    public static final String REQ_SET_USER_OFFER = "setUserOffer";
    public static final String REQ_SET_USER_MESSAGE = "setUserContactMessage";
    public static final String REQ_ADD_CARDS_TO_USER = "addCardsToUser";
    public static final String REQ_USE_GOLDEN_CARD = "useGoldenCard";
    public static final String REQ_USE_CARD = "useCard";

    // account
    public static final String REQ_UPDATE_USER_DATA = "updateUserData";
    public static final String REQ_CHANGE_PASSWORD = "changeUserPasswordByID";
    public static final String REQ_DEACTIVATE_USER_ACCOUNT = "deactivateUserAccountByID";
    public static final String REQ_SET_MEMBERSHIP = "setUserMembership";

    // sign
    public static final String REQ_SIGN_UP = "register";
    public static final String REQ_SIGN_IN = "login";
    public static final String REQ_SOCIAL_SIGN = "loginOrRegisterWithSocial";
    public static final String REQ_FORGOT_PASSWORD = "forgotPassword";
    // endregion

    // region cats keys
    public static final String CAT_ENGINES = "Engines";
    public static final String CAT_JEWELLERY = "Jewelery";
    public static final String CAT_REAL_STATE = "Real Estates";
    public static final String CAT_MATERIALS = "Consumption materials";

    public static final String CAT_ID = "category selected from store";
    // endregion

    public static final int NULL_INT_VALUE = -111;
    public static final String EMPTY_TOKEN = "empty";
    public static final String CATEGORY_KEY = "cat_key";
    public static final String CATEGORY_NAME = "cat_name";
    public static final String PREVIOUS_PAGE_KEY = "previous_page";
}

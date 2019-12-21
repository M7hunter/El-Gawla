package it_geeks.info.elgawla.util;

public class Constants {

    // region payment methods
    public static final String FAWRY = "fawry";
    public static final String KNET = "knet";
    // endregion

    // region requests actions
    static final String REQ_SET_FIREBASE_TOKEN = "setUserFirebaseToken";
    public static final String REQ_GET_ALL_NOTIFICATION = "getAllNotifications";
    public static final String REQ_GET_ALL_VOTES = "getAllVotes";
    public static final String REQ_SET_VOTE = "setVote";
    public static final String REQ_GET_ALL_PAGES = "getAllPages";
    public static final String REQ_GET_ALL_SLIDERS = "getAllSliders";
    public static final String REQ_GET_ALL_SALONS = "getAllSalons";
    public static final String REQ_GET_ALL_FINISHED_SALONS = "getAllSalonsFinished";
    public static final String REQ_GET_ALL_BLOGS = "getAllBlogs";
    public static final String REQ_GET_ALL_CATEGORIES = "getAllCategories";
    public static final String REQ_GET_FILTER_DATES = "getFilterDates";
    public static final String REQ_GET_ALL_COUNTRIES = "getAllCountries";
    public static final String REQ_GET_SALONS_BY_CAT_ID = "getSalonsByCategoryID";
    public static final String REQ_GET_CARDS_BY_CATEGORY = "getCardByCategoryID";
    public static final String REQ_GET_ALL_PACKAGES = "getAllPackages";
    public static final String REQ_GET_PAYMENT_PAGE = "getPaymentPage";
    public static final String REQ_GET_SALON_BY_ID = "getSalonByID";
    public static final String REQ_GET_MY_CARDS = "getMyCards";
    public static final String REQ_GET_MY_INVOICES = "getMyInvoices";

    // salon
    public static final String REQ_GET_SALONS_ARCHIVE = "getSalonsArchive";
    public static final String REQ_GET_SALONS_BY_USER_ID = "getSalonsByUserID";
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
    public static final String REQ_RESET_PASSWORD = "resetPassword";
    public static final String REQ_SET_MEMBERSHIP = "setUserMembership";
    public static final String REQ_CHECK_SUBSCRIPTION = "checkSubscribe";
    public static final String REQ_USER_ACTIVATION = "userActivation";

    // sign
    public static final String REQ_SIGN_UP = "register";
    public static final String REQ_SIGN_IN = "login";
    public static final String REQ_SEND_SMS = "sendSms";
    public static final String REQ_CONFIRM_CODE = "confirmCode";
    public static final String REQ_SOCIAL_SIGN = "loginOrRegisterWithSocial";
    public static final String REQ_FORGOT_PASSWORD = "forgotPassword";
    // endregion

    public static final String CAT_ID = "category selected from store";
    public static final int NULL_INT_VALUE = -111;
    public static final int REQ_CODE_BUY_CARD = 159;
    public static final String EMPTY_TOKEN = "empty";
    public static final String CATEGORY_KEY = "cat_key";
    public static final String FINISHED = "is_finished_salons";
    public static final String FILTER = "filter";
    public static final String CATEGORY_NAME = "cat_name";
    public static final String PREVIOUS_PAGE_KEY = "previous_page";
    public static final String PAYMENT_URL = "payment_url";
    public static final String SERVER_MSG = "message";
    public static final String PACKAGE = "package";
    public static final String SALON = "salon";

    // region entering path
    public static final String PATH = "entering_path";
    public static final String TO_SALON = "to_salon";
    public static final String TO_STORE = "to_store";
    public static final String TO_DEFAULT = "to_default";
    // endregion

}

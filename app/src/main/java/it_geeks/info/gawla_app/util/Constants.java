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
    public static final String REQ_GET_ALL_PAGES = "getAllPages";
    public static final String REQ_GET_ALL_SLIDERS = "getAllSliders";
    public static final String REQ_GET_ALL_SALONS = "getAllSalons";
    public static final String REQ_GET_ALL_BLOGS = "getAllBlogs";
    public static final String REQ_GET_ALL_CATEGORIES = "getAllCategories";
    public static final String REQ_GET_ALL_COUNTRIES = "getAllCountries";
    public static final String REQ_GET_ALL_CARDS = "getAllCards";
    public static final String REQ_GET_PAYMENT_PAGE = "getPaymentPage";
    public static final String REQ_GET_SALON_BY_ID = "getSalonByID";

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

    public static final String EMPTY_TOKEN = "empty";

}

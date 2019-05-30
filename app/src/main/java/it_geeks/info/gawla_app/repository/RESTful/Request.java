package it_geeks.info.gawla_app.repository.RESTful;

import static it_geeks.info.gawla_app.util.Constants.*;


public class Request<T> {

    private T firstName, lastName, phone, gender, join_time, left_time, offer, membership,
            provider, provider_id, provider_name, provider_email, provider_image, firebase_token, payment_method,
            name, api_token, image, password, email, message,
            category_id, salon_id, round_id, card_id, user_id, country_id,
            isHome;

    public Request(String action, T t1, T t2, T t3, T t4, T t5, T t6, T t7) {

        if (action.equals(REQ_SIGN_IN))
        {
            this.email = t1;
            this.password = t2;
        }
        else if (action.equals(REQ_SIGN_UP))
        {
            this.name = t1;
            this.email = t2;
            this.country_id = t3;
            this.password = t4;
            this.membership = (T) "basic";
        }
        else if (action.equals(REQ_SOCIAL_SIGN))
        {
            this.provider = t1;
            this.provider_id = t2;
            this.provider_name = t3;
            this.provider_email = t4;
            this.provider_image = t5;
            this.country_id = t6;
        }
        else if (action.equals(REQ_FORGOT_PASSWORD))
        {
            this.email = t1;
        }
        else if (action.equals(REQ_GET_ALL_COUNTRIES))
        {
            this.api_token = t1;
        }
        else // requests that needs user id & api token
        {
            this.user_id = t1;
            this.api_token = t2;

            if (action.equals(REQ_ADD_CARDS_TO_USER))
            {
                this.card_id = t3;
            }
            else if (action.equals(REQ_SET_USER_SALON))
            {
                this.salon_id = t3;
                this.join_time = t4;
                this.left_time = t5;
            }
            else if (action.equals(REQ_GET_TOP_TEN) || action.equals(REQ_GET_WINNER))
            {
                this.salon_id = t3;
                this.round_id = t4;
            }
            else if (action.equals(REQ_GET_ALL_SALONS))
            {
                this.isHome = t3;
            }
            else if (action.equals(REQ_CHANGE_PASSWORD))
            {
                this.password = t3;
            }
            else if (action.equals(REQ_SET_MEMBERSHIP))
            {
                this.membership = t3;
            }
            else if (action.equals("setUserFirebaseToken"))
            {
                this.firebase_token = t3;
            }
            else if (action.equals(REQ_UPDATE_USER_DATA))
            {
                this.firstName = t3;
                this.lastName = t4;
                this.phone = t5;
                this.gender = t6;
                this.country_id = t7;
            }
            else if (action.equals("updateUserEmail"))
            {
                this.email = t3;
                this.country_id = t4;
            }
            else if (action.equals("updateUserImage"))
            {
                this.country_id = t3;
                this.image = t4;
            }
            else if (action.equals(REQ_SET_USER_MESSAGE))
            {
                this.name = t3;
                this.email = t4;
                this.message = t5;
            }
            else // requests that needs salon id
            {
                this.salon_id = t3;

                if (action.equals(REQ_USE_CARD) || action.equals(REQ_USE_GOLDEN_CARD))
                {
                    this.card_id = t4;
                    this.round_id = t5;
                }
                else if (action.equals(REQ_GET_PAYMENT_PAGE))
                {
                    this.card_id = t4;
                    this.category_id = t5;
                    this.payment_method = t6;
                }
                else if (action.equals(REQ_SET_USER_OFFER))
                {
                    this.offer = t4;
                }
            }
        }
    }
}
package it_geeks.info.elgawla.util;

import android.annotation.SuppressLint;

import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Google Pay API request configurations
 *
 * @see <a href="https://developers.google.com/pay/api/android/">Google Pay API Android
 * documentation</a>
 */
public class GooglePay {
    /**
     * Create a Google Pay API base request object with properties used in all requests
     *
     * @return Google Pay API base request object
     * @throws JSONException
     */
    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0)
                .put("emailRequired", false)
                .put("shippingAddressRequired", false);
    }

    /**
     * Identify your gateway and your app's gateway merchant identifier
     *
     * <p>The Google Pay API response will return an encrypted payment method capable of being charged
     * by a supported gateway after payer authorization
     *
     * <p>TODO: check with your gateway on the parameters to pass
     *
     * @return payment data tokenization for the CARD payment method
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentMethodTokenizationSpecification">PaymentMethodTokenizationSpecification</a>
     */
    private static JSONObject getTokenizationSpecification() throws JSONException {
        JSONObject tokenizationSpecification = new JSONObject();
        tokenizationSpecification.put("type", "PAYMENT_GATEWAY");
        tokenizationSpecification.put(
                "parameters",
                new JSONObject()
                        .put("gateway", "example")
                        .put("gatewayMerchantId", "exampleGatewayMerchantId"));

        return tokenizationSpecification;
    }

    /**
     * Card networks supported by your app and your gateway
     *
     * <p>TODO: confirm card networks supported by your app and gateway
     *
     * @return allowed card networks
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#CardParameters">CardParameters</a>
     */
    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray()
//                .put("AMEX")
//                .put("DISCOVER")
//                .put("JCB")
                .put("MASTERCARD")
                .put("VISA");
    }

    /**
     * Card authentication methods supported by your app and your gateway
     *
     * <p>TODO: confirm your processor supports Android device tokens on your supported card networks
     *
     * @return allowed card authentication methods
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#CardParameters">CardParameters</a>
     */
    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
                .put("CRYPTOGRAM_3DS")
                .put("PAN_ONLY");
    }

    /**
     * Describe your app's support for the CARD payment method
     *
     * <p>The provided properties are applicable to both an IsReadyToPayRequest and a
     * PaymentDataRequest
     *
     * @return a CARD PaymentMethod object describing accepted store
     * @throws JSONException .
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentMethod">PaymentMethod</a>
     */
    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");
        cardPaymentMethod.put(
                "parameters", new JSONObject()
                        .put("allowedAuthMethods", getAllowedCardAuthMethods())
                        .put("allowedCardNetworks", getAllowedCardNetworks()));

        return cardPaymentMethod;
    }

    /**
     * Describe the expected returned payment data for the CARD payment method
     *
     * @return a CARD PaymentMethod describing accepted store and optional fields
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentMethod">PaymentMethod</a>
     */
    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", getTokenizationSpecification());

        return cardPaymentMethod;
    }

    /**
     * Provide Google Pay API with a payment amount, currency, and amount status
     *
     * @return information about the requested payment
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#TransactionInfo">TransactionInfo</a>
     */
    private static JSONObject getTransactionInfo(String totalPrice, String currencyCode) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", totalPrice);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("currencyCode", currencyCode);

        return transactionInfo;
    }

    /**
     * Information about the merchant requesting payment information
     *
     * @return information about the merchant
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#MerchantInfo">MerchantInfo</a>
     */
    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject()
                .put("merchantName", "Example Merchant");
    }

    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's
     * readiness to pay
     *
     * @return API version and payment methods supported by the app
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#IsReadyToPayRequest">IsReadyToPayRequest</a>
     */
    @SuppressLint("NewApi")
    public static Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray()
                            .put(getCardPaymentMethod()));

            return Optional.of(isReadyToPayRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    /**
     * An object describing information requested in a Google Pay payment sheet
     *
     * @return payment data expected by your app
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentDataRequest">PaymentDataRequest</a>
     */
    @SuppressLint("NewApi")
    public static Optional<JSONObject> getPaymentDataRequest(String totalPrice, String currencyCode) {
        try {
            JSONObject paymentDataRequest = getBaseRequest();
            paymentDataRequest.put("allowedPaymentMethods", new JSONArray().put(getCardPaymentMethod()));
            paymentDataRequest.put("transactionInfo", getTransactionInfo(totalPrice, currencyCode));
            paymentDataRequest.put("merchantInfo", getMerchantInfo());

            return Optional.of(paymentDataRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
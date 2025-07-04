package com.example.myPlugin;

import android.util.Log;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;


import com.airpay.airpaysdk_simplifiedotp.AirpayConfig;
import com.airpay.airpaysdk_simplifiedotp.constants.ConfigConstants;
import com.airpay.airpaysdk_simplifiedotp.utils.Utils;
import com.example.cordovaapp.MainActivity;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONObject;
import android.text.TextUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyPlugin extends CordovaPlugin  {
    private static final String TAG = "MyPlugin";
    public static CallbackContext callbackContext;
    String sAllSubsciptionData;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);


    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // if (action.equals("greet")) {
        //     String name = args.getString(0);
        //     this.greet(name, callbackContext);
        //     return true;
        // }
        // return false;

        if (action.equals("greet")) {
            this.callbackContext = callbackContext;  // Save callback context to return result later
            JSONObject formData = args.getJSONObject(0);
            // Trigger payment initiation
            this.initiatePayment(formData);

            return true;
        }
        return false;
    }

    public static void greet(String name, CallbackContext callbackContext) {
        if (name != null && name.length() > 0) {
            Log.d("success",name);
            callbackContext.success( name);
        } else {
            Log.d("error","Expected one non-empty string argument.");
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void initiatePayment(JSONObject formData) {

        try {
            // Extract data from JSON object
            String firstName = formData.getString("firstName");
            String lastName = formData.getString("lastName");
            String email = formData.getString("email");
            String phone = formData.getString("phone");
            String address = formData.getString("address");
            String city = formData.getString("city");
            String state = formData.getString("state");
            String country = formData.getString("country");
            String pincode = formData.getString("pincode");
            String orderId = formData.getString("orderId");
            String amount = formData.getString("amount");
            String txnSubtype = formData.getString("txnSubtype");
            String nextRunDate = formData.getString("nextRunDate");
            String period = formData.getString("period");
            String frequency = formData.getString("frequency");
            String maxAmount = formData.getString("maxAmount");
            String subscriptionAmount = formData.getString("subscriptionAmount");
            String recurringCount = formData.getString("recurringCount");
            String retryAttempts = formData.getString("retryAttempts");


            String sAllData1;
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            String sCurDate1 = df1.format(new Date());



            if (!TextUtils.isEmpty(txnSubtype) && txnSubtype.equals("12")) {

                if ("A".equalsIgnoreCase(period)) {
                    sAllSubsciptionData = period +
                            appendDecimal(subscriptionAmount) +
                            "1" +
                            retryAttempts;
                } else {
                    sAllSubsciptionData = nextRunDate +
                            frequency +
                            period +
                            appendDecimal(subscriptionAmount) +
                            "1" +
                            recurringCount +
                            retryAttempts;
                }


                sAllData1 = email + firstName
                        + lastName + address
                        + city + state
                        + country + appendDecimal(amount)
                        + orderId + sAllSubsciptionData + sCurDate1;


            } else {
                sAllData1 = email + firstName
                        + lastName + address
                        + city + state
                        + country + appendDecimal(amount)
                        + orderId + sCurDate1;
            }

            // Merchant details
            Log.d("sAllData1", "" + sAllData1);

            // Merchant details
            String sMid = "";
            String sSecret = "";
            String sUserName = "";
            String sPassword = "";

            String merdom = "";
            String successUrl = "";
            String failureUrl = "";

            String client_id = "";
            String client_secret = "";
            // private key
            String sTemp = sSecret + "@" + sUserName + ":|:" + sPassword;
            String sPrivateKey = Utils.sha256(sTemp);

            // key for checksum
            String sTemp3 = sUserName + "~:~" + sPassword;
            String sKey1 = Utils.sha256(sTemp3);

            // checksum
            sAllData1 = sKey1 + "@" + sAllData1;


            String sChecksum1 = Utils.sha256(sAllData1);
            Log.d("Checksum", sChecksum1);

            AirpayConfig.Builder builder = new AirpayConfig.Builder(cordova.getActivity(), ((MainActivity) cordova.getActivity()).getActivityResultLauncher());
            builder.setEnvironment(ConfigConstants.PRODUCTION);
            builder.setType(ConfigConstants.AIRPAY_KIT);
            builder.setPrivateKey(sPrivateKey);
            builder.setMerchantId(sMid);
            builder.setOrderId(orderId);
            builder.setCurrency("356");
            builder.setIsoCurrency("INR");
            builder.setEmailId(email);
            builder.setMobileNo(phone);
            builder.setBuyerFirstName(firstName);
            builder.setBuyerLastName(lastName);
            builder.setBuyerAddress(address);
            builder.setBuyerCity(city);
            builder.setBuyerState(state);
            builder.setBuyerCountry(country);
            builder.setBuyerPinCode(pincode);
            builder.setAmount(appendDecimal(amount));
            builder.setWallet("0");
            builder.setCustomVar("");
            builder.setTxnSubType(txnSubtype);
            builder.setChmod("");
            builder.setChecksum(sChecksum1);
            builder.setMerDom(merdom);
            builder.setSuccessUrl(successUrl);
            builder.setFailedUrl(failureUrl);
            builder.setLanguage("EN");
            builder.setClient_id(client_id);
            builder.setClient_secret(client_secret);
            builder.setGrant_type("client_credentials");
            builder.setAesDesKey(sTemp3);
            if (!TextUtils.isEmpty(txnSubtype) && txnSubtype.equals("12")) {
                //new AirpayConfig.Builder(MainActivity.this, activityResultLauncher)
                builder.setNextrundate(nextRunDate);
                builder.setPeriod(period);
                builder.setFrequency(frequency);
                builder.setMaxAmount(appendDecimal(maxAmount));
                builder.setSubscriptionAmt(appendDecimal(subscriptionAmount));
                builder.setIsRecurring("1");
                builder.setRecurringCount(recurringCount);
                builder.setRetryAttempts(retryAttempts);
                builder.build().initiatePayment();
            } else {
                builder.build().initiatePayment();
            }


            callbackContext.success("Payment initiated with form data");
        } catch (JSONException e) {
            callbackContext.error("Failed to parse form data: " + e.getMessage());
        }
    }

    public String appendDecimal(String input) {
        if (input == null || input.isEmpty()) {
            return "0.00"; // Default value for empty input
        }

        // Check if input already has a decimal
        if (!input.contains(".")) {
            return input + ".00"; // Append .00 if no decimal exists
        }

        return input; // Return as-is if it already has a decimal
    }

}

package com.example.myPlugin;

import android.util.Log;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import com.airpay.ae.AirpayConfig;
import com.airpay.ae.constants.ConfigConstants;
import com.airpay.ae.utils.Utility;
import com.airpay.samplecordova.MainActivity;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

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
            String orderId = formData.getString("orderId");
            String amount = formData.getString("amount");

            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            String sCurDate1 = df1.format(new Date());



            String sAllData = email + firstName
                    + lastName + address
                    + city + state
                    + country + amount
                    + orderId + sCurDate1;

            // Merchant details
            Log.d("sAllData1", "" + sAllData);

            String sMid = "";
            String sSecret = "";
            String sUserName = "";
            String sPassword = "";
            // private key
            String sTemp = sSecret + "@" + sUserName + ":|:" + sPassword;
            String sPrivateKey = Utility.sha256(sTemp);

            // key for checksum
            String sTempUserPasswd = sUserName + "~:~" + sPassword;
            String sKey = Utility.sha256(sTempUserPasswd);

            // checksum
            sAllData = sKey + "@" + sAllData;
            String sChecksum = Utility.sha256(sAllData);

            String merdom = "";
            String successUrl = "";
            String failureUrl = "";

            String client_id = "";
            String client_secret = "";


            new AirpayConfig.Builder(((MainActivity) cordova.getActivity()).getActivityResultLauncher(),cordova.getActivity())
                    .setActionType("IndexPay")
                    .setEnvironment(ConfigConstants.PRODUCTION)
                    .setType(ConfigConstants.AIRPAY_KIT)
                    .setPrivateKey(sPrivateKey)
                    .setSecretKey(sSecret)
                    .setMerchantId(sMid)
                    .setOrderId(orderId)
                    .setCurrency("834") // 834    840
                    .setIsoCurrency("tzs") // tzs  usd
                    .setEmailId(email)
                    .setMobileNo(phone)
                    .setBuyerFirstName(firstName)
                    .setBuyerLastName(lastName)
                    .setBuyerAddress(address)
                    .setBuyerCity(city)
                    .setBuyerState(state)
                    .setBuyerCountry(country)
                    .setAmount(amount)
                    .setWallet("0")
                    .setChmod("")
                    .setChecksum(sChecksum)
                    .setMerDom(merdom)
                    .setSuccessUrl(successUrl)
                    .setFailedUrl(failureUrl)
                    .setTxnsubtype("")
                    .setCustomVar("")
                    .setLanguage("EN")
                    .setClient_id(client_id)
                    .setClient_secret(client_secret)
                    .setGrant_type("client_credentials")
                    .setAesDesKey(sTempUserPasswd)
                    .build()
                    .initiatePayment();


            callbackContext.success("Payment initiated with form data");
        } catch (JSONException e) {
            callbackContext.error("Failed to parse form data: " + e.getMessage());
        }
    }



}

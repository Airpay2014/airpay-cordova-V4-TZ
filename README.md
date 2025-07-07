# Airpay Cordova Plugin
 
This is the official Cordova plugin for integrating Airpay payment gateway into Cordova applications.
 
## Supported Platforms
 
* Android (version compatibility details below)
  Upto Gradle version 8.7

## Requirements
 
* Cordova Version: Supports Cordova version up to v12.0.0 
* NodeJS Version: Supports NodeJS version up to v22.11.0
* Android Version: Compatible with Android SDK min version 24 to max version 34
* Android Build Tool: Supports Android Build Tool up to v22.11.0
   
## Installation

Note: For Windows users, please run the following commands on Git Bash instead of Command Prompt. You can download Git for Windows here.
  
Navigate to your project directory:

```
cd your-project-folder
```

Add the necessary platforms:

```
cordova platform add android
```

Install the Airpay plugin:

```
cordova plugin add https://github.com/Airpay2014/airpay-cordova-V4-TZ.git#master 
``` 
### Executing program
 
 The following Android files are crucial for the Airpay Cordova integration and ensure seamless functionality:

* AndroidManifest.xml
  
  Manages app-level configurations and permissions.
  Add below code inside Application tag.

```
<uses-library
            android:name="org.apache.http.legacy"
            android:required="false" />

```
Make sure to give Internet Permission.

```
 <uses-permission android:name="android.permission.INTERNET" />

```
* MainActivity.java
  Serves as the main entry point for your Cordova application.

```

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.CRC32;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.airpay.ae.model.card_details.CardItem;
import com.airpay.ae.model.data.TransactionDto;
import com.airpay.ae.view.ActionResultListener;
import com.example.myPlugin.MyPlugin;
import com.google.gson.Gson;

import org.apache.cordova.*;


public class MainActivity extends CordovaActivity implements ActionResultListener
{
    public static ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                // Assuming Transaction is returned in the intent
                                TransactionDto transaction = (TransactionDto) data.getParcelableExtra("transaction");
                                if (transaction != null) {
                                    Log.d("MainActivity", "Transaction status: " + transaction.getData().getTransactionStatus());
                                    // Handle successful transaction
                                    Toast.makeText(MainActivity.this, ""+transaction.getData().getTransactionStatus(), Toast.LENGTH_SHORT).show();
                                    Gson gson = new Gson();
                                    String responseJson = gson.toJson(transaction);
                                    Log.d("responseJson",responseJson);
                                    MyPlugin.greet(responseJson, MyPlugin.callbackContext);
                                } else {
                                    MyPlugin.greet("Transaction object is null", MyPlugin.callbackContext);
                                    Toast.makeText(MainActivity.this, "Transaction object is null", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                MyPlugin.greet("Intent data is null", MyPlugin.callbackContext);
                                Log.d("MainActivity", "Intent data is null");
                            }
                        } else {
                            //  Toast.makeText(MainActivity.this, "Payment result not OK, Result Code: ", Toast.LENGTH_SHORT).show();
                            Log.d("MainActivity", "Payment result not OK, Result Code: " + result.getResultCode());
                        }
                    }
                }
        );

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        
        loadUrl(launchUrl);
    }
    public static ActivityResultLauncher<Intent> getActivityResultLauncher() {
        return activityResultLauncher;
    }

    @Override
    public void onResult(Object o) {
        if (o instanceof ArrayList) {
            ArrayList<CardItem> cardDetails = (ArrayList<CardItem>) o;
        } else if (o instanceof TransactionDto) {
            TransactionDto transaction = (TransactionDto) o;

            Toast.makeText(this, transaction.getData().getTransactionStatus() + "\n" + transaction.getMessage(), Toast.LENGTH_LONG).show();

            String orderId = transaction.getData().getOrderid();
            String apTransactionID = transaction.getData().getApTransactionid();
            String amount = transaction.getData().getAmount();
            int transtatus = transaction.getData().getTransactionStatus();
            String message = transaction.getData().getTransactionPaymentStatus();
            String merchantid = transaction.getData().getMerchantId();
            String userName = transaction.getData().getCustomerName();
            Log.d("userName",userName);
            String merchant_key = transaction.getData().getMerchant_key();

            String username = "";   //Please enter Username
            String sParam = orderId + ":" + apTransactionID + ":" + amount + ":" + transtatus + ":" + message + ":" + merchantid  + ":" + username;
            CRC32 crc = new CRC32();
            crc.update(sParam.getBytes());
            String sCRC = "" + crc.getValue();
            Log.e("Verified Hash ==", "sParam= " + sParam);
            Log.e("Verified Hash ==", "Calculate Hash= " + sCRC);
            Log.e("Verified Hash ==", "RESP Secure Hash= " + transaction.getData().getApSecurehash());

            if (sCRC.equalsIgnoreCase(transaction.getData().getApSecurehash())) {
                  Log.e("Verified Hash ==", "SECURE HASH MATCHED");
            } else {
                  Log.e("Verified Hash ==", "SECURE HASH MIS-MATCHED");
            }
        }
    }

    @Override
    public void onFailure(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


}



```

* build.gradle (app)
   Defines build configurations and dependencies for the app. 
   For latest dependency, kindly refer the kit on sanctum portal link -  	
```
	implementation 'com.airpay:airpay-tz-kit:1.0.6'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.android.volley:volley:1.2.1'

```

* repository.gradle
   Specifies repositories for dependency management.
   For Username and Password, kindly refer the kit on sanctum portal link -  	
```
   maven {
        url "https://gitlab.com/api/v4/projects/51199569/packages/maven"
        credentials(HttpHeaderCredentials) {
            name = ""  //enter name 
            value = "" // enter password
        }
        authentication {
            header(HttpHeaderAuthentication)
        }
    }

```
*  MyPlugin.java
   Implements the Cordova plugin’s core logic.
   Please enter the merchant configuration details in required below fields -

```
 // Merchant details
            String sMid = ""; // Please enter Merchant Id
            String sSecret = ""; // Please enter Secret Key
            String sUserName = ""; // Please enter Username
            String sPassword = ""; // Please enter Password
			String client_id = ""; // Please enter client_id
            String client_secret = ""; // Please enter client_secret


.setSuccessUrl("") // Please enter Success URL
.setFailedUrl("") // Please enter Failed URL
.setMerDom("");   // Please enter Success URL domain
                    
```
 Note :- For above merchant configuration details, Kindly contact with Airpay Support Team.

## Help
 
Any advise for common problems or issues.

```

command to run if program contains helper info

```
 
## Support
 
* Tech/integration Support Team
* Customer Support Team
 
## Version History
 
* 0.1

    * Initial Release
 
## License
 
This project is licensed under the [Airpay Payment Service] License 
 

 

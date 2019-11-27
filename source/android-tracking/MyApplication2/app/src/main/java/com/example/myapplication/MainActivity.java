package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import android.provider.Settings.Secure;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * Main Activity
 */
public class MainActivity extends AppCompatActivity {
    /// Stores request to be made to server in queue
    private RequestQueue mRequestQueue;

    /// Stores IP address of the server.
    private final String serverAddress = "34.93.44.41";

    /// URL to hit the server for device registration
    private final String deviceRegisterURL = "http://" + serverAddress + "/api/registerDevice";

    /// Used for logging purpose
    private final String TAG = "tag";

    /// Stores API key
    private String APIKey;

    /// Stores device ID
    private String deviceID;

    /// Stores filename in which API key to be stored
    private static final String FILE_NAME = "API_KEY.txt";
    //a252fe62-faa7-461f-aecd-4cd9cae40b61      // dummy
    //e4abd300-cdff-4bdc-b4b8-e81648d50846
    //6126992a-9f3b-4e0e-ab61-fd67d80ebf89


    /// holder for submit button
    Button btn_submit_key;

    /// holder for text box
    EditText editText;


    /**
     * Gets API key from the file
     * This function checks whether API key is present in the file
     * if yes then returns it
     * @return API key if already registered
     * */
    void getAPIKey() {
        Log.i(TAG, "In get API key");
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            APIKey = sb.toString();
            APIKey = APIKey.trim();
            Log.i(TAG, "API key:--> " + APIKey);

        } catch (FileNotFoundException e) {
            Log.i(TAG, "In get file not dounf");
            APIKey = "";
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, "In get file not found");
            APIKey = "";
            e.printStackTrace();
        } finally {

             if (fis != null) {
                 try {
                     fis.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
        }

    }

    /**
     * Writes API key to the file
     * This function stores the API key in a file in internal storage
     * @param input String to be written in file
     **/
    void writeAPIKey(String input) {
        Log.i(TAG, "In write API key");

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(input.getBytes());
//            Log.i(TAG, getFilesDir().toString());
//            Toast.makeText(this, "Saved to " + getFilesDir().toString() + "/" + FILE_NAME,
//                    Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            Log.i(TAG, "In write file not found catch");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, "In write io catch");
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



    /**
     * Invoked when app instance is created
     * This method is invoked when the application is created
     * It has definitions of contents on the UI
     * and it overrides the onclick methods of the content
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_submit_key = (Button) findViewById((R.id.btn_api_key_submit));
        final EditText editText = (EditText) findViewById(R.id.edit_text);

        deviceID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        Log.i(TAG, "device id: " +  deviceID);

        /**
         * Checks for Location permission
         * Requests for permission if not gotten already
         */
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        getAPIKey();
        registerDevice(true, "");

        /**
         * OnClick listner for submit button on the UI
         */
        btn_submit_key.setOnClickListener(new View.OnClickListener() {
            /** Function to be executed when clicked on submit button
             * It attempts to register the device
             * @param v View on the UI
             */
            @Override
            public void onClick(View v) {
                Log.i(TAG, editText.getText().toString());
                registerDevice(false, editText.getText().toString().trim());
            }
        });

    }

    /**
     * Registers device if not already registered
     * @param isOnStart boolean variable to check whether it is the
     *                  first call to registerDevice when activity
     *                  is invoked
     * @param enteredAPIKey It stores the entered API key
     *                      in the text box mentioned on the UI
     */
    void registerDevice(final boolean isOnStart, String enteredAPIKey) {
        Log.i(TAG, enteredAPIKey);
        if(!isOnStart) {
            APIKey = enteredAPIKey;
        }

        JSONObject JSONBody = null;
        /**
         * Tries to convert string in JSON format to JSON object
         */
        try {
            String JSONStr = "{\"apiKey\":\"" + APIKey + "\",\"deviceId\":\"" + deviceID + "\"}";
            JSONBody = new JSONObject(JSONStr);
            Log.i(TAG, "I'm in json check try");
            Log.i(TAG, JSONStr);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "I'm in json check catch");
        }

        /**
         * Makes an HTTP post request to server
         * Sends the API key and device id as parameters in JSON object
         * Hits on URL: SERVER_ADDRESS/api/registerDevice
         */
        JsonObjectRequest req = new JsonObjectRequest(deviceRegisterURL, JSONBody,
                new Response.Listener<JSONObject>() {
                    /**
                     * Invoked when call to server was successful
                     * @param response stores the output returned by
                     *                the server in JSON object
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(TAG, "I'm in success try");
                            Log.i(TAG, response.toString());
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            Log.i(TAG, "I'm in success catch");
                            e.printStackTrace();
                        }


                        try {
                            String  status = response.getString("status");
                            if(status.equals("200")) {
                                writeAPIKey(APIKey);

                                Toast.makeText(MainActivity.this, "Device registered successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, Main3Activity.class);
                                startActivity(intent);
                            }
                            else if(status.equals("540")) {
                                writeAPIKey(APIKey);
                                Intent intent = new Intent(MainActivity.this, Main3Activity.class);
                                startActivity(intent);
                            }
                            else if(status.equals("500")) {
                                if(!isOnStart){
                                    Toast.makeText(MainActivity.this, "Wrong API key", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
            /**
             * Invoked when call to server is not successful
             * @param error Stores the error response received
             *              from server
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Log.i(TAG, "Error : " + error.toString());
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

        this.addToRequestQueue(req, TAG);

    }

    /**
     * Genereates new request queue if not already present
     * @return request queue object
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Adds request to queue
     * @param req Request to be added to request queue
     * @param tag Used for logging purpose
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue().add(req);
    }






}

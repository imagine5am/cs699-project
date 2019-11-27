package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main3Activity class
 */
public class Main3Activity extends AppCompatActivity {

    TextView textView;
    Button btn_start_stop;
    Button btn_unregister;

    /// Stores request to be made to server in queue
    private RequestQueue mRequestQueue;

    /// Stores IP address of the server.
    private final String serverAddress = "34.93.44.41";

    /// URL to hit the server for device unregistration
    private final String deviceUnregisterURL = "http://" + serverAddress + "/api/unregisterDevice";

    /// URL to hit the server to send location of device
    private final String sendLocationURL = "http://" + serverAddress + "/api/submitLocation";

    /// Used for logging purpose
    private final String TAG = "tag";

    /// Stores API key
    private String APIKey;

    /// Stores device ID
    private String deviceID;

    /// Stores filename in which API key to be stored
    private static final String FILE_NAME = "API_KEY.txt";
    //a252fe62-faa7-461f-aecd-4cd9cae40b61
    //e4abd300-cdff-4bdc-b4b8-e81648d50846
    //6126992a-9f3b-4e0e-ab61-fd67d80ebf89

    /// Object used to get the location of device
    LocationManager locationManager;

    /// Object used to listen to events related to events
    LocationListener locationListener;

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
     * Class which implements android.location.LocationListener class
     * to override its methods
     */
    private class LocationListener implements android.location.LocationListener {
        /**
         * Invoked whenever there is change is a change in location
         * @param location Object which is used to get latitude
         *                 and longitude of the device's location
         */
        @Override
        public void onLocationChanged(Location location) {
            String latitude = Double.toString(location.getLatitude());
            String longitude = Double.toString(location.getLongitude());
            textView.setText("Lat: " + latitude + "\nLng: " + longitude);
            sendRequestAndPrintResponse(latitude, longitude);
        }

        /**
         * Invoked when GPS or internet on tracker device is disabled
         * @param provider Provider
         */
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(Main3Activity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
        }

        /**
         * This is a deprecated method
         * @param provider Provider
         * @param status Status
         * @param extras Bundle of extra parameters
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        /**
         * Called when the provider is enabled by the user.
         * @param provider Provider
         */
        @Override
        public void onProviderEnabled(String provider) {
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
        setContentView(R.layout.activity_main3);

        btn_start_stop = (Button) findViewById(R.id.btn_press_me);
        btn_unregister = (Button) findViewById(R.id.btn_unregister);
        textView = (TextView) findViewById(R.id.tv_textView);

        /// get API key from the file
        getAPIKey();

        /// get unique device ID
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        /**
         * OnClick listner for Enable/Disable button on the UI
         */
        btn_start_stop.setOnClickListener(new View.OnClickListener() {
            /** Function to be executed when clicked on Enable/Disable button
             * It starts/stops sending the data to the server
             * @param v View on the UI
             */
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Someone pressed the button");
                Button b = (Button)v;
                String buttonText = b.getText().toString();
                Log.d(TAG, buttonText);
                if(buttonText.equals("Enable Tracker")){
                    Log.d(TAG, "Enabling Tracker");
                    b.setText("Disable Tracker");
                    getLocation();
                }
                else {
                    Log.d(TAG, "Disabling Tracker");
                    b.setText("Enable Tracker");
                    locationManager.removeUpdates(locationListener);
                }
            }
        });

        /**
         * OnClick listner for unregister button on the UI
         */
        btn_unregister.setOnClickListener(new View.OnClickListener() {
            /** Function to be executed when clicked on unregister button
             * It unregisters the data
             * @param v View on the UI
             */
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Someone pressed the button");
                unregisterDevice();
            }
        });
    }

    /**
     * Remove API key
     * Truncates the file in which API key is stored
     */
    void deleteAPIKey() {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write("".getBytes());

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
     * Unregisters the device
     * When successfully unregisterd it deletes the API key
     * from the file
     */
    void unregisterDevice() {
        JSONObject JSONBody = null;

        /**
         * Tries to convert string in JSON format to JSON object
         */
        try {
            String JSONStr = "{\"deviceId\":\"" + deviceID + "\"}";
            JSONBody = new JSONObject(JSONStr);
            Log.i(TAG, "I'm in json check try");
            Log.i(TAG, JSONStr);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "I'm in json check catch");
        }

        /**
         * Makes an HTTP post request to server
         * Sends the device id as parameters in JSON object
         * Hits on URL: SERVER_ADDRESS/api/unregisterDevice
         */
        JsonObjectRequest req = new JsonObjectRequest(deviceUnregisterURL, JSONBody,
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
                                Toast.makeText(Main3Activity.this, "Device unregistered successfully", Toast.LENGTH_SHORT).show();
                                deleteAPIKey();
                                Intent intent = new Intent(Main3Activity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else if(status.equals("530")) {
                                Toast.makeText(Main3Activity.this, "Device not unregistered", Toast.LENGTH_SHORT).show();
                            }
                            else if(status.equals("520")) {
                                Toast.makeText(Main3Activity.this, "Device not found", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Main3Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

        this.addToRequestQueue(req, TAG);
    }

    /**
     * Gets location of the device
     */
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, locationListener);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send location details to the server
     * @param latitude Stores latitude
     * @param longitude Stored longitude
     */
    private void sendRequestAndPrintResponse(String latitude, String longitude) {
        JSONObject JSONBody = null;
        /**
         * Tries to convert string in JSON format to JSON object
         */
        try {
            String JSONStr = "{\"apiKey\":\"" + APIKey + "\",\"deviceId\":\"" + deviceID + "\",\"latLng\":{\"lat\":" + latitude + ",\"lng\":" + longitude + "}}";
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
         * Hits on URL: SERVER_ADDRESS/api/submitLocation
         */
        JsonObjectRequest req = new JsonObjectRequest(sendLocationURL, JSONBody,
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
                            Log.i(TAG, APIKey);
                            if(status.equals("500"))
                                Toast.makeText(Main3Activity.this, "Wrong API KEY", Toast.LENGTH_SHORT).show();
                            else if(status.equals("520"))
                                Toast.makeText(Main3Activity.this, "Device not found", Toast.LENGTH_SHORT).show();
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
                Log.i(TAG, "I'm in failed");
                VolleyLog.e("Error: ", error.getMessage());
                Log.i(TAG, "Error : " + error.toString());
                Toast.makeText(Main3Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

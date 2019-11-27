package app.insti.fragment;

import android.graphics.PointF;
import android.os.AsyncTask;

import com.mrane.data.Marker;
import com.mrane.zoomview.SubsamplingScaleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import app.insti.Constants;

import static app.insti.fragment.MapFragment.campusMapView;

public class BuggyLocationFragment extends AsyncTask {
    String data="";
    List<Integer> dataparsed=new ArrayList<Integer>();
    private static final int TEN_MINUTES = 10 * 60 * 1000;

    /**
     * Post a json request to url and receives location of multiple
     * devices in json object.This json object is parsed to get the
     * Location data and timestamp of all devices
     */
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            //URL url = new URL("http://34.93.44.41/api/getDeviceLocations");
            URL url = new URL("http://34.93.44.41/api/getDeviceLocations");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            JSONObject sampleObject = new JSONObject();
            sampleObject.put("apiKey", "e4abd300-cdff-4bdc-b4b8-e81648d50846");

            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");


            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(sampleObject.toString());
            wr.flush();



            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    data = data + line;
                }
                br.close();
                System.out.println("" + data);
            }

            JSONObject JO = new JSONObject(data);
            Iterator<String> keys= JO.keys();

            while(keys.hasNext()) {
                String key = keys.next();
                if (JO.get(key) instanceof JSONObject) {
                    JSONObject Device = JO.getJSONObject(key);
                    JSONObject latlng = Device.getJSONObject("latLng");
                    double Xn = Constants.MAP_Xn, Yn = Constants.MAP_Yn, Zn = Constants.MAP_Zn, Zyn = Constants.MAP_Zyn;

                    //scaling of coordinates to match with
                    double x = ((double)latlng.get("lat") - Xn) * 1000;
                    double y = ((double)latlng.get("lng") - Yn) * 1000;

                    // Pre-trained weights to change the coordinates to match with Insti app
                    double[] A = Constants.MAP_WEIGHTS_X;
                    int px = (int) (Zn + A[0] + A[1] * x + A[2] * y + A[3] * x * x + A[4] * x * x * y + A[5] * x * x * y * y + A[6] * y * y + A[7] * x * y * y + A[8] * x * y);

                    A = Constants.MAP_WEIGHTS_Y;
                    int py = (int) (Zyn + A[0] + A[1] * x + A[2] * y + A[3] * x * x + A[4] * x * x * y + A[5] * x * x * y * y + A[6] * y * y + A[7] * x * y * y + A[8] * x * y);

                    //If timestamp received is older than 10 minutes then do not store the location

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    Date parsedDate = formatter.parse((String)Device.get("timestamp"));
                    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                    long tenAgo = System.currentTimeMillis() - TEN_MINUTES;
                    if (timestamp.getTime() < tenAgo) {
                        System.out.println(timestamp.getTime() < tenAgo);
                        continue;
                    }

                    dataparsed.add(px);
                    dataparsed.add(py);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Here the latest location data is updated and marked on the insti map
     */
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        //user is used to store user location so it would not be erased during updation of buggy location
        Marker user = MapFragment.user;
        campusMapView.removeAddedMarkers();
        campusMapView.addMarker(user);
        for (int i = 0 ; i < dataparsed.size(); i=i+2){

            int px=dataparsed.get(i);
            int py=dataparsed.get(i+1);
            Marker buggy = new Marker("Buggy - "+i, "", px, py, -10, "");
            if (px > 0 && py > 0 && px < 5430 && py < 5375) {
                campusMapView.addMarker(buggy);
                buggy.setPoint(new PointF(px, py));
                buggy.setName("Buggy - " +((i/2)+1));
                campusMapView.invalidate();
            }

        }

    }
}


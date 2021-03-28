package ba.affanzukic.autoweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String query = "";
    String api;
    TextView cityName, hiLoTemp, temp, humidityText, pressureText, description;
    String cityNameString, descriptionString, icon, imageURL;
    int temperature, highTemp, lowTemp, humidity, pressure;
    FloatingActionButton fab;
    SwitchMaterial smartHome;
    ImageView iconImage;
    ProgressDialog progressDialog;
    private static int STATIC_INTEGER_VALUE = 0;
    DownloadTask downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.cityName);
        hiLoTemp = (TextView) findViewById(R.id.hiLoTemp);
        temp = (TextView) findViewById(R.id.currentTemp);
        humidityText = (TextView) findViewById(R.id.humidity);
        pressureText = (TextView) findViewById(R.id.pressure);
        description = (TextView) findViewById(R.id.description);
        iconImage = (ImageView) findViewById(R.id.imageView);
        query = "Ilidža";
        smartHome = (SwitchMaterial) findViewById(R.id.smartHome);

        downloadTask = new DownloadTask();
        downloadTask.execute();
    }

    public class DownloadTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
            api = "https://api.openweathermap.org/data/2.5/weather?q=" + query + "&appid=44b7ba412800701a672fda14ae3f816c&units=metric";
        }

        @Override
        protected String doInBackground(String... strings) {
            String current = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(api);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);
                    int data = isw.read();

                    while (data != -1) {
                        current += (char) data;
                        data = isw.read();
                        System.out.print(current);
                    }

                    Log.d("datalength", "" + current.length());
                    return current;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("data", s.toString());
            progressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(s);
                cityNameString = jsonObject.getString("name");
                JSONArray weather = jsonObject.getJSONArray("weather");
                JSONObject weatherJSON = weather.getJSONObject(0);
                descriptionString = weatherJSON.getString("description");
                icon = weatherJSON.getString("icon");
                JSONObject main = jsonObject.getJSONObject("main");
                temperature = (int) Math.round(main.getDouble("temp"));
                highTemp = (int) Math.round(main.getDouble("temp_max"));
                lowTemp = (int) Math.round(main.getDouble("temp_min"));
                humidity = (int) Math.round(main.getDouble("humidity"));
                pressure = (int) Math.round(main.getDouble("pressure"));
                imageURL = "https://openweathermap.org/img/wn/" + icon + "@4x.png";
                Log.i("Image: ", imageURL);

                cityName.setVisibility(View.VISIBLE);
                hiLoTemp.setVisibility(View.VISIBLE);
                temp.setVisibility(View.VISIBLE);
                humidityText.setVisibility(View.VISIBLE);
                pressureText.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                iconImage.setVisibility(View.VISIBLE);

                cityName.setText(cityNameString);
                description.setText(descriptionString);
                temp.setText(temperature + " °C");
                hiLoTemp.setText("H " + highTemp + " °C | L " + lowTemp + " °C");
                humidityText.setText("Humidity: " + humidity);
                pressureText.setText("Pressure: " + pressure + " hPa");
                Picasso.get().load(imageURL).into(iconImage);
            } catch (JSONException e) {
                Log.i("Exception", e.toString());
            }
        }
    }

    public void fabClicked(View view) {
        Intent intent = new Intent(this, Search.class);
        startActivityForResult(intent, STATIC_INTEGER_VALUE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STATIC_INTEGER_VALUE && resultCode == Activity.RESULT_OK) {
            String newInput = data.getStringExtra("cityname");
            query = newInput;
            Log.i("New query", query);
            Log.i("New input", newInput);
            downloadTask = new DownloadTask();
            downloadTask.execute();
        }
    }

    public void switchChanged(View view) {
        boolean isOn = ((SwitchMaterial)view).isChecked();
        int optTemp = 22;

        String text;

        if ((temperature - optTemp) > 0)
        {
            text = "Automatic regulation enabled! Lowering temperature by " + (temperature - optTemp) + " °C";
        }
        else if ((temperature - optTemp) < 0)
        {
            text = "Automatic regulation enabled! Increasing temperature by " + (optTemp - temperature) + " °C";
        }
        else
        {
            text = "Automatic regulation enabled! The temperature is currently fine and will not be regulated on your thermostat.";
        }

        if (isOn) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Automatic regulation disabled!", Toast.LENGTH_SHORT).show();
        }
    }
}
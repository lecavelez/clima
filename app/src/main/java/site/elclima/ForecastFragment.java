package site.elclima;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by lvelez on 4/26/16.
 */
public class ForecastFragment extends Fragment {
    private  ArrayAdapter<String> mForecastAdapter;
    public ForecastFragment() {}
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setHasOptionsMenu(true);
    }
    //Clicks que se le realizan al menú
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            /*String zip_code = "pereira";//"94043";
            weatherTask.execute(zip_code);*/
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather(){
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        weatherTask.execute(location);

    }

    @Override
    public void onStart(){
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        //ArrayAdapter<String> mForecastAdapter;
        //Create some dummy data for the listView. Here´s a sample weekly forecast
        /*String[] data = {
                "Mon 6/23 - Sunny - 31/1",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));*/


        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>()
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                String forecast = mForecastAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });

        //Se movio a otra parte para una mejor ejecución
        //Realizar la conexión con open
        /*HttpURLConnection urlConnection = null;
        BufferedReader reader = null; //Buffer de lectura
        String forecastJsonStr = null; //Realizar conexión sobre el hilo principal (No deberia hacerse).

        try{
            //http://openweathermap.org/API#forecast
            String base = "http://api.openweathermap.org/data/2.5/forecast/daily?q=9404&mode=json&units=metric&cnt=7";
            String key  = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_APY_KEY;
            URL url = new URL(base.concat(key));

            //Create the request to OpenMeatherMap , and open the conection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null){
                return null;
            }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    //Stream was empty. No point in parsing
                    return null;
                }

                forecastJsonStr = buffer.toString();
            }catch (IOException e){
                Log.e("PlaceholderFragment", "Error", e);
                return  null;
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try{
                        reader.close();
                    }catch (IOException e){
                        Log.e("PlaceholderFragment", "Error", e);
                    }
                }
            }*/

            return rootView;

    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private String formatHighLows(double high, double low, String unitType){
            if(unitType.equals(getString(R.string.pref_units_imperial))){
                high = (high * 1.8) + 32;
                low = (low * 1.8) + 32;
            }else if(!unitType.equals(getString(R.string.pref_units_metric))){
                Log.d(LOG_TAG, "Unit type not found: " + unitType);
            }
            long roundedHigh = Math.round(high);
            long roundedLow  = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return  highLowStr;
        }

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException{
            //There are the name of the json objects that need to be extracted
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject  forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            Calendar gc = new GregorianCalendar();

            SharedPreferences sharedPres =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String uniType = sharedPres.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_metric));


            String[] resultsStrs = new String[numDays];

            for(int i = 0; i < weatherArray.length(); i++){
                String day;
                String description;
                String highAndLow;

                JSONObject dayForecast = weatherArray.getJSONObject(i);
                long dateTime;

                day = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
                gc.add(Calendar.DAY_OF_WEEK, 1);

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);

                double high = temperatureObject.getDouble(OWM_MAX);
                double low  = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low, uniType);
                resultsStrs[i] = day + " - " + description + " - " + highAndLow;
                Log.v("JsonObject", resultsStrs[i]);
            }

            return resultsStrs;
        }

        protected String[] doInBackground(String... params){

            //If there´ no zip code, there´s nothing to look up. Verify size of params.
            if(params.length == 0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null; //Buffer de lectura
            String format = "json";
            String units = "metric";
            int numDays = 7;

            //Will contain the raw JSON response as a string
            String forecastJsonStr = null; //Realizar conexión sobre el hilo principal (No deberia hacerse).

            try{

                //http://openweathermap.org/API#forecast
                // String base = "http://api.openweathermap.org/data/2.5/forecast/daily?q=367248&mode=xml&units=metric&cnt=7";
                // String key  = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_APY_KEY;
                // URL url = new URL(base.concat(key));

                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "days";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_APY_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                //Create the request to OpenMeatherMap , and open the conection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    //Stream was empty. No point in parsing
                    return null;
                }

                forecastJsonStr = buffer.toString();
            }catch (IOException e){
                Log.e("PlaceholderFragment", "Error", e);
                return  null;
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try{
                        reader.close();
                    }catch (IOException e){
                        Log.e("PlaceholderFragment", "Error", e);
                    }
                }
            }

            try{
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected  void onPostExecute(String[] result){
            if(result != null){
                mForecastAdapter.clear();
                for(String dayForecastStr: result){
                   mForecastAdapter.add(dayForecastStr);
                }
            }
        }
    }
}


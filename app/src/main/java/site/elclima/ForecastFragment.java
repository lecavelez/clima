package site.elclima;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            weatherTask.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        //ArrayAdapter<String> mForecastAdapter;
        //Create some dummy data for the listView. Here´s a sample weekly forecast
        String[] data = {
                "Mon 6/23 - Sunny - 31/1",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

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

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        protected Void doInBackground(Void... params){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null; //Buffer de lectura
            String forecastJsonStr = null; //Realizar conexión sobre el hilo principal (No deberia hacerse).

            try{
                //http://openweathermap.org/API#forecast
                String base = "http://api.openweathermap.org/data/2.5/forecast/daily?q=3672486&mode=xml&units=metric&cnt=7";
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
            }

            return null;
        }
    }
}


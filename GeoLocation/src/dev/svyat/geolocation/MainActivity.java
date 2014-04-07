package dev.svyat.geolocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{

	// Google Map
    private GoogleMap googleMap;
    // Global variable to hold the current location
    Location mCurrentLocation;
    LocationClient mLocationClient;
    double lat;
    double lon;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
 
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
            else{
            	googleMap.setMyLocationEnabled(true);
            	googleMap.getUiSettings().setZoomControlsEnabled(true);
            	googleMap.getUiSettings().setZoomGesturesEnabled(true);
            	googleMap.getUiSettings().setCompassEnabled(true);
            	
            	lat = 0.0;
            	lon = 0.0;

            	googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            		
            		@Override
                    public void onMyLocationChange(Location location) {
            			if(lat == 0.0 && lon == 0.0){
            				lat = location.getLatitude();
            				lon = location.getLongitude();
            				googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("It's You"));
            				googleMap.addMarker(new MarkerOptions().position(new LatLng(55.743753, 37.673283)).title("It's finish"));
                			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15));
                			findDirections(lat, lon, 55.743753, 37.673283, GMapV2Direction.MODE_DRIVING );
            			}
            			else{
            				googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("It's You"));
            			}
            			
            		}
            	});
            	
            }
        }
    }
    
    public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList<LatLng>>
    {
        public static final String USER_CURRENT_LAT = "user_current_lat";
        public static final String USER_CURRENT_LONG = "user_current_long";
        public static final String DESTINATION_LAT = "destination_lat";
        public static final String DESTINATION_LONG = "destination_long";
        public static final String DIRECTIONS_MODE = "directions_mode";
        private Activity activity;
        private Exception exception;
     
        public GetDirectionsAsyncTask(Activity activity)
        {
            super();
            this.activity = activity;
        }
     
        public void onPreExecute()
        {
        }
     
        @Override
        public void onPostExecute(ArrayList<LatLng> result)
        {
            if (exception == null)
            {
                handleGetDirectionsResult(result);
            }
            else
            {
                processException();
            }
        }
     
        @Override
        protected ArrayList<LatLng> doInBackground(Map<String, String>... params)
        {
            Map<String, String> paramMap = params[0];
            try
            {
                LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)) , Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
                LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));
                GMapV2Direction md = new GMapV2Direction();
                Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
                ArrayList<LatLng> directionPoints = md.getDirection(doc);
                return directionPoints;
            }
            catch (Exception e)
            {
                exception = e;
                return null;
            }
        }
     
        private void processException()
        {
            Toast.makeText(activity, "Error!(", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints)
    {
        //Polyline newPolyline;
        PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.BLUE);
        for(int i = 0 ; i < directionPoints.size() ; i++)
        {
            rectLine.add(directionPoints.get(i));
        }
        //newPolyline = googleMap.addPolyline(rectLine);
        googleMap.addPolyline(rectLine);
    }
    
    @SuppressWarnings("unchecked")
	public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);
     
        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(map);
    }
    
    @Override
    protected void onStart(){
    	super.onStart();
    	initilizeMap();
    }
 
    @Override
    protected void onResume() {
        super.onResume();
    }

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		
		
	}

	@Override
	public void onDisconnected() {
		
		
	}

}

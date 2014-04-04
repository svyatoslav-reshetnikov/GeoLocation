package dev.svyat.geolocation;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.location.Location;
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
        
        try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
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
                			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15));
            			}
            			else{
            				googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("It's You"));
            			}
            		}
            	});
            }
        }
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
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

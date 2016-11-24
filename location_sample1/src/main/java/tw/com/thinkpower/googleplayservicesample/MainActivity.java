package tw.com.thinkpower.googleplayservicesample;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient = null;

    private TextView valueLongitude,valueLatitude,valueAddress;

    private static final String TAG = "LocationSample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        valueLongitude = (TextView)findViewById(R.id.value_longitude);
        valueLatitude = (TextView) findViewById(R.id.value_latitude);
        valueAddress = (TextView) findViewById(R.id.value_address);

        //----- google play service client-start
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        //----- google play service client-end
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null) {
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }
        super.onStart();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    Location mLastKnownLocation = null;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"onConnected invoked!!");
        if (mGoogleApiClient != null) {
            if (ActivityCompat.checkSelfPermission(this
                    , android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if(mLastKnownLocation != null){
                if(valueLongitude != null){
                    valueLongitude.setText(String.valueOf(mLastKnownLocation.getLongitude()));
                }
                if(valueLatitude != null){
                    valueLatitude.setText(String.valueOf(mLastKnownLocation.getLatitude()));
                }
                // get possible address
                List<Address> addresses;
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(
                            mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude(),
                            1);
                    Address address = addresses.get(0);
                    if(valueAddress != null){
                        valueAddress.setText(address.getAddressLine(0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getBaseContext(), "no location information available"
                        , Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"onConnectionSuspended invoked!!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"onConnectionFailed("+connectionResult.toString()+") invoked!!");
    }
}

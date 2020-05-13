package com.example.uberclone.NewUberClone;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Switch;
import android.widget.Toast;

import com.example.uberclone.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class WelcomeAct extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;

    private static final int My_permision_reqcode=7000;
    private static final int My_service_reqcode=7001;

    GoogleApiClient mgoogleApiClient;
    private FirebaseAuth mAuth;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private static int UpdateInterval=5000;
    private static int FastInterval=3000;
    private static int Dispaly=10;

    DatabaseReference driver;
    GeoFire geoFire;
    Marker marker;

    MaterialAnimatedSwitch switc;
    SupportMapFragment mapFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        switc=findViewById(R.id.sw);
        switc.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean b) {
                if (b){
                    statlocationupdate();
                    dispayLocation();
                    Snackbar.make(mapFragment.getView(),"you are online",Snackbar.LENGTH_SHORT).show();

                }else {
                    stopLocaton();
                    marker.remove();
                    Snackbar.make(mapFragment.getView(),"you are offline",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        driver= FirebaseDatabase.getInstance().getReference("VivuDriver");
        geoFire=new GeoFire(driver);
        setUplocation();

    }

    private void setUplocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            },My_permision_reqcode);
        }else {
            if (chekpalyserevice()){
                bulidgoogleApiclit();
                createlocationreq();

                if (switc.isChecked()){
                    dispayLocation();
                }
            }
        }
    }

    private void createlocationreq() {
        mLocationRequest =new LocationRequest();
        mLocationRequest.setInterval(UpdateInterval);
        mLocationRequest.setFastestInterval(FastInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(Dispaly);


    }

    private void bulidgoogleApiclit() {
        mgoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mgoogleApiClient.connect();
     }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case My_permision_reqcode:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                 {
                    if (chekpalyserevice()){
                        bulidgoogleApiclit();
                        createlocationreq();

                        if (switc.isChecked()){
                            dispayLocation();
                        }
                    }
                }
        }

    }

    private boolean chekpalyserevice() {
        int resultcode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultcode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultcode))
                GooglePlayServicesUtil.getErrorDialog(resultcode, this, My_permision_reqcode).show();
            else {
                Toast.makeText(this, "this device not support", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
       return true;
    }

    private void stopLocaton() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED)

            LocationServices.FusedLocationApi.removeLocationUpdates(mgoogleApiClient, this);

    }

    private void dispayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED)

        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (mLastLocation !=null){
            if (switc.isChecked()){
                final  double latitude=mLastLocation.getLatitude();
                final  double logtitud=mLastLocation.getLongitude();

                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        new GeoLocation(latitude,logtitud));
                if (marker!=null){
                    marker.remove();
                    marker=mMap.addMarker(new MarkerOptions()
                                       .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                       .position(new LatLng(latitude,logtitud))
                                       .title("You"));

                  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,logtitud),15.0f));
                  rotetemarker(marker,-360,mMap);
                }
            }
        }
    }

    private void rotetemarker(final Marker marker, final float i, GoogleMap mMap) {
        final Handler handler=new Handler();
        final long start= SystemClock.uptimeMillis();
        final float starrotatopn=marker.getRotation();
        final long duration=1500;

        final Interpolator interpolator=new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elaspad=SystemClock.uptimeMillis() -start;
                float t=interpolator.getInterpolation((float)elaspad/duration);
                float rot=t*i+(1-t)*starrotatopn;
                marker.setRotation(-rot >180?rot/2:rot);
                if (t<1.0){
                    handler.postDelayed(this,16);
                }
            }
        });
    }


    private void statlocationupdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED)

         LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mLocationRequest, this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

      /*  // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
       dispayLocation();
       statlocationupdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mgoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        dispayLocation();
    }
}

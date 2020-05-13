package com.example.uberclone;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CoustomerMapAct extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mgoogleApiClient;
    private FirebaseAuth mAuth;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    TextView logout,setting;
    FirebaseUser firebaseUser;
    Boolean driverFound=false;
    String useeid,driverid;
    DatabaseReference databaseReference,driverAvilebale,divref,driverlocationref;
    LatLng coustmerpicup;
    Fragment fragment;
    Button cur;
    Marker drivermarker;
    int Rediou=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coustomer_map);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

         useeid=FirebaseAuth.getInstance().getCurrentUser().getUid();
         databaseReference=FirebaseDatabase.getInstance().getReference().child("customer Request");
         driverAvilebale=FirebaseDatabase.getInstance().getReference().child("Drivers Availbal");
         driverlocationref=FirebaseDatabase.getInstance().getReference().child("Drivers Working  ");

        cur=findViewById(R.id.cur);
        cur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoFire geoFire=new GeoFire(databaseReference);
                geoFire.setLocation(useeid,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

                coustmerpicup=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
               // mMap.addMarker(new MarkerOptions().position(coustmerpicup).title("Pikup"));
              //  BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_walk_black_24dp);
                mMap.addMarker(new MarkerOptions()
                         //.icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                         // .icon(icon)
                        .icon(bitmapDescriptorFromVector(CoustomerMapAct.this,R.drawable.ic_directions_walk_black_24dp))
                        .position(coustmerpicup)
                        .title("You"));
                cur.setText("getting your Driver....");
                GetClosedriver();
            }
        });

    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_directions_walk_black_24dp);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private void GetClosedriver() {
        GeoFire geoFire=new GeoFire(driverlocationref);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(coustmerpicup.latitude,coustmerpicup.longitude),Rediou);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
               if (!driverFound){
                    driverFound=true;
                    driverid=key;
                    divref=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverid);
                    HashMap drivermap=new HashMap();
                    drivermap.put("cusride_id",useeid);
                    divref.updateChildren(drivermap);
                    GettingDriverrlocation();
                    cur.setText("Looking for Driver locatoin... ");

               }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
               if (!driverFound){
                   Rediou=Rediou+1;
                   GetClosedriver();
               }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void GettingDriverrlocation() {
        driverlocationref.child(driverid).child("1")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            List<Object> driverlocation=(List<Object>) dataSnapshot.getValue();
                            double Locationlat=0;
                            double Locationlng=0;
                            cur.setText("Driver Found");

                            if (driverlocation.get(0)!=null){
                                Locationlat=Double.parseDouble(driverlocation.get(0).toString());
                            }
                            if (driverlocation.get(1)!=null){
                                Locationlng=Double.parseDouble(driverlocation.get(1).toString());
                            }
                            LatLng driverlatlong=new LatLng(Locationlat,Locationlng);
                            if (driverFound !=null){
                                drivermarker.remove();
                            }

                            Location location1=new Location("");
                            location1.setLatitude(coustmerpicup.latitude);
                            location1   .setLatitude(coustmerpicup.longitude);


                            Location location2=new Location("");
                            location2.setLatitude(driverlatlong.latitude);
                            location2.setLatitude(driverlatlong.longitude);

                            float distance=location1.distanceTo(location2);
                            cur.setText("Driver Found" + String.valueOf(distance));

                            drivermarker=mMap.addMarker(new MarkerOptions().position(driverlatlong).title("Your Driver is hear..."));

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            return;
        }
        BulidGoogleApiClint();
        mMap.setMyLocationEnabled(true);
    }
    protected synchronized void BulidGoogleApiClint() {
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mgoogleApiClient.connect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
              LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
              mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
              mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

              useeid=FirebaseAuth.getInstance().getCurrentUser().getUid();

              databaseReference= FirebaseDatabase.getInstance().getReference().child("Driver Avbl");
              GeoFire geoFire=new GeoFire(databaseReference);

             geoFire.setLocation(useeid,new GeoLocation(location.getLatitude(),location.getLongitude()));
        //  geoworking.setLocation(useeid,new GeoLocation(location.getLatitude(),location.getLongitude()));
    }
}

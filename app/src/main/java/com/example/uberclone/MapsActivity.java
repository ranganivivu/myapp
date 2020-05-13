package com.example.uberclone;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Permission;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
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
    Boolean curruntdriverstatus=false;
    String useeid,driverid,custnerid="";
    DatabaseReference databaseReference,Assingdatarefrce,assingcoupic;

    LatLng coustmerpicup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);

        mapFragment.getMapAsync(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        driverid=mAuth.getCurrentUser().getUid();
       // useeid=FirebaseAuth.getInstance().getCurrentUser().getUid();
      //  databaseReference=FirebaseDatabase.getInstance().getReference().child("Custmer Requrd");

        setting=findViewById(R.id.setting);
        logout=findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                curruntdriverstatus=true;
                Discoonectdriver();

              //  mAuth.signOut();
                LogoutDriver();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoFire geoFire=new GeoFire(databaseReference);
                geoFire.setLocation(useeid,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

                coustmerpicup=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
              //  mMap.addMarker(new MarkerOptions().position(coustmerpicup).title("Pikup"));
                mMap.addMarker(new MarkerOptions()
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                        // .icon(icon)
                        .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_directions_car_black_24dp))
                        .position(coustmerpicup)
                        .title("Pikup"));
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        GetAssCustmerReq();
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_directions_car_black_24dp);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private void GetAssCustmerReq() {
       Assingdatarefrce=FirebaseDatabase.getInstance().getReference().child("Users")
               .child("Drivers").child(driverid).child("cusride_id");
       Assingdatarefrce.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (dataSnapshot.exists()){
                   custnerid=dataSnapshot.getValue().toString();
                   getAssingpik();
              }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void getAssingpik() {
       assingcoupic=FirebaseDatabase.getInstance().getReference().child("coutmer requst")
               .child(custnerid).child("1");
       assingcoupic.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()) {
                   List<Object> driverlocation = (List<Object>) dataSnapshot.getValue();
                   double Locationlat = 0;
                   double Locationlng = 0;
                 //   cur.setText("Driver Found");

                   if (driverlocation.get(0) != null) {
                       Locationlat = Double.parseDouble(driverlocation.get(0).toString());
                   }
                   if (driverlocation.get(1) != null) {
                       Locationlng = Double.parseDouble(driverlocation.get(1).toString());
                   }
                   LatLng driverlatlong=new LatLng(Locationlat,Locationlng);
                   mMap.addMarker(new MarkerOptions().position(driverlatlong).title("Pik up hear..."));

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void LogoutDriver() {
        Intent intent=new Intent(MapsActivity.this,MainPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
           /* LatLng Stdny=new LatLng(-34,251);
          mMap.addMarker(new MarkerOptions().position(Stdny).title("yout loction"));
          mMap.moveCamera(CameraUpdateFactory.newLatLng(Stdny));*/
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
    public void onLocationChanged(Location location) {
        if (getApplicationContext() !=null) {
            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

            useeid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Driver Available");
            GeoFire geoFire = new GeoFire(databaseReference);

            DatabaseReference driver_working= FirebaseDatabase.getInstance().getReference().child("Driver Working");
            GeoFire geoworking=new GeoFire(driver_working);

            switch (custnerid){
                case "":
                    geoworking.removeLocation(useeid);
                   // geoFire.setLocation(useeid, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    geoworking.setLocation(useeid,new GeoLocation(location.getLatitude(),location.getLongitude()));
                    break;
                    default:
                        geoworking.removeLocation(useeid);
                        geoFire.setLocation(useeid, new GeoLocation(location.getLatitude(), location.getLongitude()));
                     break;
            }
        }
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
    protected void onStop() {
        super.onStop();
        if (!curruntdriverstatus){
            Discoonectdriver();
        }
        Discoonectdriver();
    }
    public void Discoonectdriver(){
        useeid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("s",useeid);
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Driver Available");
        GeoFire geoFire = new GeoFire(databaseReference);
        geoFire.removeLocation(useeid);
    }
}

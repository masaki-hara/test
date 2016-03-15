package example.com;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.OnMapReadyCallback;

import android.app.Activity;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
//import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.touristspotinform.R;

public class RegistrationActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }
    /*
        public static class PlaceholderFragment extends MapFragment{
            private GoogleMap mMap;

            public PlaceholderFragment() {
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState){
                super.onCreateView(inflater, container, savedInstanceState);
                View rootView = inflater.inflate(R.layout.activity_registration, container, false);
                return rootView;
            }

            @Override
            public void onResume(){
                super.onResume();

                LatLng latLng = new LatLng(34.9950555,135.737253);

                if(mMap == null){
                    mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
                }

                if(mMap != null){
                    mMap.addMarker(new MarkerOptions().position(latLng).title("INTFLOAT Co.,Ltd."));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                            latLng, 16, 0, 0)));
                }
            }
        }*/

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng stl = new LatLng(31.600934,130.557511);
        float zoom = 18; // 2.0～21.0
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stl, zoom));

        mMap.addMarker(new MarkerOptions().position(stl).title("STL"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(stl));
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // この marker は保存するとリークすると思われる。
                String msg = "Marker onClick:" + marker.getTitle();
                Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_LONG).show();
                return false;
            }
        });

    }
    public void onGetCenter(View view) {
        CameraPosition cameraPos = mMap.getCameraPosition();
        LatLng stl = new LatLng(cameraPos.target.latitude,cameraPos.target.longitude);
        mMap.addMarker(new MarkerOptions().position(stl).title("NewPoint"));
        // オブジェクトを取得
        EditText etxtLocationName     = (EditText)findViewById(R.id.editLoCationName);
        //入力された場所の名前格納用(とりあえずここにおいているが，onGetCenterじゃない，登録ボタンを押したときに実行するクラスに移すべき！)
        EditText etxtUrl     = (EditText)findViewById(R.id.editURL);//URL格納用
        TextView txtIdo   = (TextView)findViewById(R.id.ido);//緯度を入れるための箱
        TextView txtKeido   = (TextView)findViewById(R.id.keido);//経度を入れるための箱
        // 入力内容を取得
        String strLocationName   = etxtLocationName.getText().toString();
        String strUrl = etxtUrl.getText().toString();


        //Toast.makeText(this, "中心位置\n緯度:" + cameraPos.target.latitude + "\n経度:" + cameraPos.target.longitude, Toast.LENGTH_LONG).show();
        // 結果表示用テキストに設定
        txtIdo.setText(String.valueOf("緯度："+cameraPos.target.latitude)); //double型をstringにしてから渡す
        txtKeido.setText(String.valueOf("経度："+cameraPos.target.longitude)); //double型をstringにしてから渡す

    }
    /*
    // 以下、GPS連動の設定。
    private void setUpLocation(boolean isManual) {
        if (isManual) {
            // 画面右上にGPSボタンが表示される。
            // タップすると現在地への移動までかってにやってくれる。
            mMap.setMyLocationEnabled(true);
        } else {
            // 現在地を定期的に取得する設定。
            if (locationClient == null) {
                locationClient = new LocationClient(
                        getApplicationContext(),
                        connectionCallbacks,
                        onConnectionFailedListener);
                locationClient.connect();
            }
        }
    }

    ConnectionCallbacks connectionCallbacks = new ConnectionCallbacks() {
        private final LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)         // 5 seconds
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        @Override
        public void onConnected(Bundle arg0) {
            locationClient.requestLocationUpdates(locationRequest, locationListener);
        }
        @Override
        public void onDisconnected() {
            // nop.
        }
    };
    OnConnectionFailedListener onConnectionFailedListener = new OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            // nop.;
        }
    };
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            float zoom = 20;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    };*/

}




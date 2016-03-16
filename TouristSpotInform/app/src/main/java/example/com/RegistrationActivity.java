package example.com;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
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

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.OnMapReadyCallback;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.maps.model.Marker;

import com.example.touristspotinform.R;
import com.google.android.gms.vision.barcode.Barcode;

public class RegistrationActivity extends FragmentActivity
        implements
        OnMapReadyCallback
        /*GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener */ {


    ///SQLLite///
    static final String DB = "sqlite_sample.db";
    static final int DB_VERSION = 1;
    static final String CREATE_TABLE = "create table mytable ( _id integer primary key autoincrement, Name text not null,URL text not null, IDO double not null, KEIDO double not null );";
    static final String DROP_TABLE = "drop table mytable;";

    static SQLiteDatabase mydb;

    private SimpleCursorAdapter myadapter;
    ////////////


    private GoogleMap mMap = null;
    private GoogleApiClient mLocationClient = null; //LocationClientは廃止，GoogleApiClientに。
    private String LocationName;
    private String URL;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private boolean mResolvingError = false;

    private FusedLocationProviderApi fusedLocationProviderApi;

    private LocationRequest locationRequest;
    private Location location;
    private long lastLocationTime = 0;

    private LocationManager mLocationManager;
    private String bestProvider;
    private Marker mMarker = null;

    /* private static final LocationRequest REQUEST = LocationRequest.create()
             .setInterval(5000) // 5 seconds
             .setFastestInterval(16) // 16ms = 60fps
             .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ////////////////////////////
        MySQLiteOpenHelper hlpr = new MySQLiteOpenHelper(getApplicationContext());
        mydb = hlpr.getWritableDatabase();
        ////////////////////////////

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);   //精度が高いを設定

        bestProvider = mLocationManager.getBestProvider(criteria, true);


    }
    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {
        public MySQLiteOpenHelper(Context c) {
            super(c, DB, null, DB_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }
        /*
        // LocationRequest を生成して精度、インターバルを設定
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(16);

        fusedLocationProviderApi = LocationServices.FusedLocationApi;

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        /////////////////////////////////////////////////////////////////////////////////////////////////
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (mLocationClient != null) {
            // Google Play Servicesに接続
            mLocationClient.connect();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

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
    public void onResume() {
        super.onResume();
        if (mLocationManager != null) {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) this);
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) this);
            }
        }
    }
        /*
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        // 現在地に移動
        CameraPosition cameraPos = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(7.0f)
                .bearing(0).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // TODO Auto-generated method stub
        Location currentLocation = fusedLocationProviderApi.getLastLocation(mLocationClient);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationProviderApi.requestLocationUpdates(mLocationClient, locationRequest, this);
            // Schedule a Thread to unregister location listeners
            Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                @Override
                public void run() {
                    fusedLocationProviderApi.removeLocationUpdates(mLocationClient, RegistrationActivity.this);
                }
            }, 60000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(this, "例外が発生、位置情報のPermissionを許可していますか？", Toast.LENGTH_SHORT);
            toast.show();

            //MainActivityに戻す
            finish();
        }
        }

    //@Override
    public void onDisconnected() {
        // TODO Auto-generated method stub
        }
    /////////////////////////////////////////////////////////////////////////////////////////////////
*/
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LocationManagerの取得
        //mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //GPSから現在地の情報を取得
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location myLocate = mLocationManager.getLastKnownLocation(bestProvider);
        if(myLocate == null){
            myLocate = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Toast.makeText(RegistrationActivity.this, "Use GPS...", Toast.LENGTH_LONG).show();
        }
        if(myLocate == null){
            myLocate = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Toast.makeText(RegistrationActivity.this, "Couldn't use GPS.\nUse NETWORK", Toast.LENGTH_LONG).show();
        }
        //MapControllerの取得
        //MapController MapCtrl = mapView.getController();
        if (myLocate != null) {
            //現在地情報取得成功
            //緯度の取得
            latitude = myLocate.getLatitude();
            //経度の取得
            longitude = myLocate.getLongitude();
        } else {
            //現在地情報取得失敗時の処理
            Toast.makeText(this, "現在地取得不可", Toast.LENGTH_SHORT).show();
        }

        // Add a marker in Sydney and move the camera
        LatLng stl = new LatLng(latitude, longitude);
        float zoom = 18; // 2.0～21.0
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stl, zoom));

        mMarker =  mMap.addMarker(new MarkerOptions().position(stl).title("現在地"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(stl));
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // この marker は保存するとリークすると思われる。
                String msg = marker.getTitle();
                Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_LONG).show();
                return false;
            }
        });

    }
    public void onGetCenter(View view) {
        mMarker.remove();
        CameraPosition cameraPos = mMap.getCameraPosition();
        LatLng stl = new LatLng(cameraPos.target.latitude, cameraPos.target.longitude);
        mMarker = mMap.addMarker(new MarkerOptions().position(stl).title("新規登録地点"));
        // オブジェクトを取得

        TextView txtIdo = (TextView) findViewById(R.id.ido);//緯度を入れるための箱
        TextView txtKeido = (TextView) findViewById(R.id.keido);//経度を入れるための箱



        //Toast.makeText(this, "中心位置\n緯度:" + cameraPos.target.latitude + "\n経度:" + cameraPos.target.longitude, Toast.LENGTH_LONG).show();
        // 結果表示用テキストに設定
        txtIdo.setText(String.valueOf("緯度：" + cameraPos.target.latitude)); //double型をstringにしてから渡す
        txtKeido.setText(String.valueOf("経度：" + cameraPos.target.longitude)); //double型をstringにしてから渡す
    }

        public void onRegistration(View view){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            // 入力内容を取得
            EditText etxtLocationName = (EditText) findViewById(R.id.editLoCationName);//入力された場所の名前格納用
            EditText etxtUrl = (EditText) findViewById(R.id.editURL);//URL格納用
            final String strLocationName = etxtLocationName.getText().toString();
            final String strUrl = etxtUrl.getText().toString();
        // ダイアログの設定
        //alertDialog.setIcon(R.drawable.icon);   //アイコン設定
        alertDialog.setTitle("確認");      //タイトル設定
        alertDialog.setMessage("以下の内容で登録しますか？\n\n場所名："
                + strLocationName + "\nURL：" + strUrl + "\n緯度：" + latitude + "\n経度：" + longitude);  //内容(メッセージ)設定

        // OK(肯定的な)ボタンの設定
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // OKボタン押下時の処理
                ContentValues values = new ContentValues();
                LocationName=strLocationName;
                URL=strUrl;
                values.put("Name",LocationName);
                values.put("URL",URL);
                values.put("IDO",latitude);
                values.put("KEIDO",longitude);

                mydb.insert("mytable", null, values);

                /*Cursor cursor = mydb.query("mytable", new String[]{"_id", "data"}, null, null, null, null, "_id DESC");
                startManagingCursor(cursor);
                myadapter.changeCursor(cursor);*/
                Log.d("AlertDialog", "Positive which :" + which);
                Toast.makeText(RegistrationActivity.this, "登録しました", Toast.LENGTH_LONG).show();
            }
        });

        // NG(否定的な)ボタンの設定
        alertDialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // NGボタン押下時の処理
                Log.d("AlertDialog", "Negative which :" + which);
            }
        });

        // ダイアログの作成と描画
//        alertDialog.create();
        alertDialog.show();


    }
}




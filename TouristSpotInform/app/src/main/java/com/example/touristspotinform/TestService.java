package com.example.touristspotinform;
/**
 * Created by Administrator on 2016/03/22.
 */

import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

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
import com.google.android.gms.maps.SupportMapFragment;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.OnMapReadyCallback;

import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderApi;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.example.touristspotinform.R;
import com.google.android.gms.vision.barcode.Barcode;

import static android.support.v7.app.NotificationCompat.*;

public class TestService extends Service
        /*implements
        OnMapReadyCallback*/ {

    //private  FragmentActivity  FragmentActivity;
    //private OnMapReadyCallback OnMapReadyCallback;

    private double latitude = 0.0;  //現在地の座標を格納
    private double longitude = 0.0;

    private double latitude_min = 0.0;//通知有効範囲設定用
    private double latitude_max = 0.0;
    private double longitude_min = 0.0;
    private double longitude_max = 0.0;

    private double latitude_db = 0.0;   //DBから取り出したばかりの座標を格納
    private double longitude_db = 0.0;

    private double latitude_db_old = 0.0;   //DBから取り出した古いほうの座標を格納
    private double longitude_db_old = 0.0;

    private double distance = 0.0;      //DBから取り出したばかりの座標への距離
    private double distance_old = 0.0;  //DBから取り出した古いほうの座標への距離

    private String Name;
    private String URL;
    public static int time=10;

    private int first_flag=0; //DB巡回時の初回判定用

    ///SQLite/////////////////////////////////////////////////////
    static final String DB = "sqlite_sample.db";
    static final int DB_VERSION = 1;
    static final String CREATE_TABLE = "create table mytable ( _id integer primary key autoincrement, Name text not null,URL text not null, IDO double not null, KEIDO double not null );";
    static final String DROP_TABLE = "drop table mytable;";
    static SQLiteDatabase mydb;
    private SimpleCursorAdapter myadapter;
    //////////////////////////////////////////////////////////////

    private GoogleMap mMap = null;
    private GoogleApiClient mLocationClient = null; //LocationClientは廃止，GoogleApiClientに。
    private LocationManager mLocationManager;
    private String bestProvider;

    private Timer mTimer = null;
    Handler mHandler = new Handler();


    @Override
    public void onCreate() {
        Log.i("TestService", "onCreate");

        ////////////////////////////
        MySQLiteOpenHelper hlpr = new MySQLiteOpenHelper(getApplicationContext());
        mydb = hlpr.getReadableDatabase();
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TestService", "onStartCommand");

        mTimer = new Timer(true);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
                        Name = null;
                        URL = null;
                        Map();

                        if(Name != null) sendNotification();
                        Log.d("TestService", "Timer run");

                    }
                });
            }
        }, 30000, 30000);//30秒ごとに処理(30000ms)


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("TestService", "onDestroy");

        // タイマー停止
        if( mTimer != null ){
            mTimer.cancel();
            mTimer = null;
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i("TestService", "onBind");
        return null;
    }

    public void Map() {
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
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location myLocate = mLocationManager.getLastKnownLocation(bestProvider);
        if(myLocate == null){
            myLocate = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if(myLocate == null){
            myLocate = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        //MapControllerの取得
        //MapController MapCtrl = mapView.getController();
        if (myLocate != null) {
            //現在地情報取得成功
            latitude = myLocate.getLatitude();  //緯度の取得
            longitude = myLocate.getLongitude();    //経度の取得
            Log.v("現在地_緯度" , "" + latitude);
            Log.v("現在地_経度" , "" + longitude);

            latitude_min = latitude - 0.001802689302;   //現在地より200m南
            latitude_max = latitude + 0.001802689302;   //現在地より200m北
            longitude_min = longitude - 0.002200673401; //現在地より200m西
            longitude_max = longitude + 0.002200673401; //現在地より200m東

            //         rawQueryを使用
            String SQL_SELECT = "SELECT _id,Name,URL,IDO,KEIDO FROM mytable";
            Cursor c = mydb.rawQuery(SQL_SELECT, null);
            if(c.moveToFirst()){
                do{
                    latitude_db = c.getDouble(c.getColumnIndex("IDO"));
                    longitude_db = c.getDouble(c.getColumnIndex("KEIDO"));

                    if((latitude_min <= latitude_db) && (latitude_max >= latitude_db) &&
                            (longitude_min <= longitude_db) && (longitude_max >= longitude_db)){    //DBから読みだしたポイントの緯経が範囲内なら
                        if(first_flag!=0) { //DBのデータが2番目以降なら

                            distance = Math.sqrt(Math.pow((latitude - latitude_db),2) + Math.pow((longitude - longitude_db),2));
                            distance_old = Math.sqrt(Math.pow((latitude - latitude_db_old),2) + Math.pow((longitude - longitude_db_old),2));
                            if(distance_old > distance) {                                   //一つ前のデータより距離がみじかければ
                                Name = c.getString(c.getColumnIndex("Name"));
                                URL = c.getString(c.getColumnIndex("URL"));
                                latitude_db_old = latitude_db;
                                longitude_db_old = longitude_db;
                            }
                        }else{                                                                  //DBのデータが1番目なら
                            Name = c.getString(c.getColumnIndex("Name"));
                            URL = c.getString(c.getColumnIndex("URL"));
                            latitude_db_old = latitude_db;
                            longitude_db_old = longitude_db;
                            first_flag=1;
                        }
                    }else{
                        ;
                    }

                }while(c.moveToNext());

            }
        } else {
            //現在地情報取得失敗時の処理:何もしない
            ;
        }
        first_flag=0;
        latitude_db_old = 0.0;
        longitude_db_old = 0.0;
    }
    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_cast_on_0_light);

        builder.setContentTitle(Name); // 1行目
        builder.setContentText("この場所のページを開く"); // 2行目
        builder.setSubText(URL); // 3行目
        builder.setContentInfo("Info"); // 右端
        //builder.setWhen(1400000000000l); // タイムスタンプ（現在時刻、メール受信時刻、カウントダウンなどに使用）
        builder.setAutoCancel(true); //タップするとキャンセル(消える)
        // 通知時の音・バイブ・ライト
        builder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS);

        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse(URL));
        PendingIntent pending = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pending);

        builder.setTicker("近くに登録されたスポットがあります"); // 通知到着時に通知バーに表示(4.4まで)
// 5.0からは表示されない

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(12345, builder.build());
    }
}

package example.com;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.SupportMapFragment;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.location.Location;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import android.util.Log;
import android.widget.TextView;
import android.widget.EditText;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import com.example.touristspotinform.R;


public class RegistrationActivity extends FragmentActivity implements OnMapReadyCallback{

    ///SQLite/////////////////////////////////////////////////////
    //static final String DB = "sqlite_sample.db";
    private static final String DB = Environment.getExternalStorageDirectory() + "/TouristSpotInform.db";   //内部ストレージにDBを保存
    static final int DB_VERSION = 1;
    static final String CREATE_TABLE = "create table mytable ( _id integer primary key autoincrement, Name text not null,URL text not null, IDO double not null, KEIDO double not null );";
    static final String DROP_TABLE = "drop table mytable;";
    static SQLiteDatabase mydb;
    private SimpleCursorAdapter myadapter;
    //////////////////////////////////////////////////////////////

    private GoogleMap mMap = null;  //map
    private LocationManager mLocationManager;   //map位置情報
    private Marker mMarker = null;  //mapマーカー

    //////////////////////////////////////////////////////////////

    private String LocationName;    //場所名格納
    private String URL;                //URL

    private double latitude = 0.0;  //現在地の座標を格納
    private double longitude = 0.0;

    private double latitude_camera = 0.0;   //「中心座標取得」で取得した座標を格納
    private double longitude_camera = 0.0;

    private double latitude_db = 0.0;   //DBから取り出した座標を格納
    private double longitude_db = 0.0;

    private int first_flag=0; //初回Map起動の判定用


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ////////////////////////////
        MySQLiteOpenHelper hlpr = new MySQLiteOpenHelper(getApplicationContext());
        mydb = hlpr.getWritableDatabase();
        ////////////////////////////

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);   //位置情報
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);   //精度が高いを設定
    }

    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {  //SQLite用
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

    public void onMapReady(GoogleMap googleMap) {   //map画面起動時の処理
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
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
        Location myLocate = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);    //GPSでの位置情報取得（過去に取得した最新の）
        if(myLocate == null){
            myLocate = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);     //NETWORK
        }
        if (myLocate != null) {
            //現在地情報取得成功
            latitude = myLocate.getLatitude();      //緯度の取得
            longitude = myLocate.getLongitude();    //経度の取得
        } else {
            //現在地情報取得失敗時の処理
            Toast.makeText(this, "現在地取得不可", Toast.LENGTH_SHORT).show();
        }

        // Add a marker in Sydney and move the camera
        LatLng stl = new LatLng(latitude, longitude);
        float zoom = 15; // 2.0～21.0
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stl, zoom));  //マップの表示を現在地中心に動かす
        MarkerOptions options_now = new MarkerOptions();    //マーカーオプションのインスタンス

        options_now.position(stl);  //マーカーの位置指定
        options_now.title("現在地");   //マーカーのタイトル設定
        // アイコンの色選択
        BitmapDescriptor icon_now = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);    //マーカー色：青
        options_now.icon(icon_now);
        mMarker = mMap.addMarker(options_now);  //上記の設定でマーカー追加

        mMap.setOnMarkerClickListener(new OnMarkerClickListener() { //マーカーがクリックされた時の処理

            public boolean onMarkerClick(Marker marker) {
                final String msg = marker.getTitle();
                // ダイアログの設定
                //alertDialog.setIcon(R.drawable.icon);   //アイコン設定
                alertDialog.setTitle("登録地点の削除");      //タイトル設定
                alertDialog.setMessage("選択した地点を削除していいですか？");  //内容(メッセージ)設定

                // OK(肯定的な)ボタンの設定
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OKボタン押下時の処理
                        //ContentValues values = new ContentValues();
                        LocationName = msg;//マーカータイトルをコピー
                        if (LocationName.equals("現在地") || LocationName.equals("新規登録地点")) {  //選んだマーカーが現在地か新規登録地点のどちらかだったら
                            Toast.makeText(RegistrationActivity.this, "現在地・新規登録地点はDBに登録されていません", Toast.LENGTH_LONG).show();
                        } else {
                            mydb.delete("mytable", "Name = ?", new String[]{LocationName});//DBにマーカータイトルと同じものがあれば削除
                            Toast.makeText(RegistrationActivity.this, "DBから削除しました", Toast.LENGTH_LONG).show();
                            reload();   //RegistrationActivityの再起動
                        }
                        Log.d("AlertDialog", "Positive which :" + which);
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
                alertDialog.show();
                return false;
            }
        });

//      rawQueryを使用し，DBを検索
        String SQL_SELECT = "SELECT _id,Name,IDO,KEIDO FROM mytable";
//     selectionArgs : WHERE句を使用するときに指定する。
        Cursor c = mydb.rawQuery(SQL_SELECT, null);

//      Cursorを先頭に移動する 検索結果が0件の場合にはfalseが返る
        if(c.moveToFirst()){    //DB内全ての地点情報をmap上に緑マーカーで設置
            do{
                String text = c.getString(c.getColumnIndex("Name"));    //マーカータイトルは「場所の名前」
                latitude_db = c.getDouble(c.getColumnIndex("IDO"));     //緯度
                longitude_db = c.getDouble(c.getColumnIndex("KEIDO"));  //経度
                LatLng point_db = new LatLng(latitude_db, longitude_db); //マーカーポジション用

                MarkerOptions options = new MarkerOptions();
                options.position(point_db);
                options.title(text);
                // (1) 色選択
                BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                options.icon(icon);
                mMarker = mMap.addMarker(options);
            }while(c.moveToNext());
        }


    }
    public void onGetCenter(View view) {    //中心座標取得ボタンが押された時の処理
        if(first_flag==0){  //初回時と地点登録直後だけ，直前のマーカーを消さない。
            first_flag++;   //現在地（青）と登録地点（緑）マーカーを消さないため
        }else{
            mMarker.remove();   //直前の新規地点登録マーカーを消す。
        }
        CameraPosition cameraPos = mMap.getCameraPosition();    //map上のカメラの位置
        latitude_camera=cameraPos.target.latitude;  //map中心部の緯度取得
        longitude_camera=cameraPos.target.longitude;    //map中心部の経度取得
        LatLng stl = new LatLng(latitude_camera,longitude_camera);  //位置情報
        mMarker = mMap.addMarker(new MarkerOptions().position(stl).title("新規登録地点"));    //新規登録地点マーカー追加（赤）
        // オブジェクトを取得

        TextView txtIdo = (TextView) findViewById(R.id.ido);//緯度を入れるための箱
        TextView txtKeido = (TextView) findViewById(R.id.keido);//経度を入れるための箱

        // 結果表示用テキストに設定
        txtIdo.setText(String.valueOf("緯度：" + latitude_camera)); //double型をstringにしてから渡す。map中心部の緯度を画面に表示する
        txtKeido.setText(String.valueOf("経度：" + longitude_camera)); //double型をstringにしてから渡す。map中心部の緯度を画面に表示する
    }

        public void onRegistration(View view){  //登録ボタンを押したときの処理
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);  //ダイアログ用インスタンス
            // 入力内容を取得
            EditText etxtLocationName = (EditText) findViewById(R.id.editLoCationName); //入力された場所の名前格納用
            EditText etxtUrl = (EditText) findViewById(R.id.editURL);   //URL格納用
            TextView txtIdo = (TextView) findViewById(R.id.ido);    //緯度を入っている箱(中心座標を一度は取得したことがあるかチェック用)
            final String strLocationName = etxtLocationName.getText().toString();   //入力された「場所の名前」を文字列として格納
            final String strUrl = etxtUrl.getText().toString(); //入力されたURL// を文字列として格納
            if(!etxtLocationName.getText().toString().equals("") && !txtIdo.getText().toString().equals("")) { //「場所の名前」が未入力じゃなく，過去に中心座標を取得していたら実行
                // ダイアログの設定
                //alertDialog.setIcon(R.drawable.icon);   //アイコン設定
                alertDialog.setTitle("確認");      //タイトル設定
                alertDialog.setMessage("以下の内容で登録しますか？\n\n場所名："
                        + strLocationName + "\nURL：" + strUrl + "\n緯度：" + latitude_camera + "\n経度：" + longitude_camera);  //内容(メッセージ)設定

                // OK(肯定的な)ボタンの設定
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OKボタン押下時の処理
                        ContentValues values = new ContentValues();
                        //以下，DBに登録地点の情報を追加
                        LocationName = strLocationName;
                        URL = strUrl;
                        values.put("Name", LocationName);
                        values.put("URL", URL);
                        values.put("IDO", latitude_camera);
                        values.put("KEIDO", longitude_camera);

                        mydb.insert("mytable", null, values);

                        Log.d("AlertDialog", "Positive which :" + which);
                        Toast.makeText(RegistrationActivity.this, "登録しました", Toast.LENGTH_LONG).show();

                        mMarker.remove();   //新規登録地点マーカーを消す
                        LatLng stl = new LatLng(latitude_camera, longitude_camera);
                        MarkerOptions options = new MarkerOptions();
                        options.position(stl);
                        options.title(LocationName);
                        // (1) 色選択
                        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);   //代わりに緑のマーカーをおく
                        options.icon(icon);
                        mMarker = mMap.addMarker(options);
                        first_flag = 0;   //次に中心座標を取得したとき，今置いたマーカーを消さないため．
                    }
                });

                // NG(否定的な)ボタンの設定
                alertDialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // キャンセルボタン押下時の処理
                        Log.d("AlertDialog", "Negative which :" + which);
                    }
                });

                // ダイアログの作成と描画
//              alertDialog.create();
                alertDialog.show();
            }
            else{   //場所の名前が未入力の時
                Toast.makeText(RegistrationActivity.this, "場所の名前が未入力か，中心座標を取得していません", Toast.LENGTH_SHORT).show();
            }

    }

    public void reload() {  //マーカーを削除したときに実行される再起動処理（マーカーの再描画が目的）
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }
/*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putInt("flag", Reset_flag);
        CameraPosition cameraPos = mMap.getCameraPosition();
        outState.putDouble("ido", cameraPos.target.latitude);
        outState.putDouble("keido", cameraPos.target.longitude);

        super.onSaveInstanceState(outState);
    }*/
}




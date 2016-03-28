package com.example.touristspotinform;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import example.com.RegistrationActivity;

public class InformSettingActivity extends AppCompatActivity implements OnCheckedChangeListener {

    private Context context;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private static int time=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // 位置測位プロバイダー一覧を取得
        String providers = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        Log.v("GPS", "Location Providers = " + providers);


        if (isMyServiceRunning() == true) {   //バックグラウンドでアプリが動いていたら，スイッチをオンにしておく
            ToggleButton mSwitch = (ToggleButton) findViewById(R.id.tglOnOff);
            mSwitch.setChecked(true);  // 通知スイッチをONに
        }
        if (providers.indexOf("gps", 0) < 0) {   //GPSがoffだったら
            // GPS設定画面の呼出し
            Toast.makeText(getApplicationContext(), "位置情報を有効にしてください", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);

            //通知設定を強制的にoffにする処理
            ToggleButton mSwitch = (ToggleButton) findViewById(R.id.tglOnOff);
            mSwitch.setChecked(false);  // 通知スイッチをOFFに


            finish();   //アプリ終了
        } else {   //GPSがonだったら
            Toast.makeText(getApplicationContext(), "現在の通知間隔は"+time+"分です", Toast.LENGTH_SHORT).show();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inform_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(InformSettingActivity.this, RegistrationActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCheckedChanged(View view) {
        ToggleButton tglOnOff = (ToggleButton) view;
        if (tglOnOff.isChecked()) { // トグルボタンがON状態になったとき
            //Toast.makeText(getApplicationContext(), "ToggleButtonがONになりました。", Toast.LENGTH_SHORT).show();

            // 遷移先のactivityを指定してintentを作成
            Intent intent = new Intent(InformSettingActivity.this, TestService.class);
            // intentへ添え字付で値を保持させる
            intent.putExtra("time", time);  //TestSeviceクラスへ渡す変数timeには通知間隔で決定した値が入っている
            // サービスの開始
            startService(intent);



        }
        if (!tglOnOff.isChecked()) {    //トグルボタンがOFF状態になったとき
            //do stuff when Switch if OFF
            // サービスの停止
            stopService(new Intent(InformSettingActivity.this, TestService.class));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
       /* if(isChecked==true){
            startService(new Intent(InformSettingActivity.this, TestService.class));
        }if(isChecked==false){
            stopService(new Intent(InformSettingActivity.this, TestService.class ) );
        }*/

    }

    private boolean isYourServiceWorking() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TestService.class.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TestService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "InformSetting Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.touristspotinform/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "InformSetting Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.touristspotinform/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    public void onCheckedDecision(View view){   //通知間隔の決定ボタンが押された時の処理
        EditText etxtNum = (EditText) findViewById(R.id.editText);  //Edit textから入力された値を取得
        if(!etxtNum.getText().toString().equals("")){   //空白でなければ
            final String strNum = etxtNum.getText().toString(); //Edit text の中身をString型に変換
            final int intNum = Integer.parseInt(strNum);    //String型からinteger型へ
            if (intNum != 0){   //integerが0以外なら実行
                time = Integer.parseInt(strNum);   //TestServiceクラスへ渡すtimeにintegerの中身を代入

                stopService(new Intent(InformSettingActivity.this, TestService.class)); //誤動作防止のためServiceを停止
                //通知設定を強制的にoffにする処理
                ToggleButton mSwitch = (ToggleButton) findViewById(R.id.tglOnOff);
                mSwitch.setChecked(false);  // 通知スイッチをOFFに
                Toast.makeText(getApplicationContext(), "通知間隔を"+time+"分に設定しました", Toast.LENGTH_LONG).show();
            }else{  //integerが0なら
                Toast.makeText(getApplicationContext(), "0は入力しないでください", Toast.LENGTH_SHORT).show();
            }
        }else{  //Edit textの中身が空白だったら
            Toast.makeText(getApplicationContext(), "数値が未入力です", Toast.LENGTH_SHORT).show();
        }
    }
}

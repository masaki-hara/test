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

import android.app.Activity;


import com.example.touristspotinform.R;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
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
    }
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
        }
    }




package com.example.slimetamagochi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    private ImageButton life;
    private ImageButton energy;
    private ImageButton humour;
    private TextView step;
    private TextView altitude;

    private ProgressBar pglife;
    private ProgressBar pghunger;
    private ProgressBar pgpower;
    private SensorManager sensorManager;
    private Sensor sensor;

    Boolean running = false;
    private Scene scene;
    private Node slime;
    private ArFragment arFragment;
    private ModelRenderable mon_modele;
    private SceneView scnView;




    private int numSteps;
    Boolean silence = true;

     MediaPlayer mp;

    int PERMISSION_ID = 44;

    FusedLocationProviderClient mFusedLocationClient;

    private Tamagochi tama;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game );

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Boolean s = extras.getBoolean("silent");
            if(s != null) {
                silence = s;
            }
            //The key argument here must match that used in the other activity
        }
        if(!silence) {
            mp = MediaPlayer.create(this, R.raw.gamemus);

            mp.setLooping(true);
            mp.start();
        }



        life = (ImageButton) findViewById( R.id.life );
        energy = (ImageButton) findViewById( R.id.energy );
        humour = (ImageButton) findViewById( R.id.humour );
        step = (TextView) findViewById( R.id.step );
        altitude = (TextView) findViewById(R.id.alt);

        pglife = (ProgressBar) findViewById( R.id.pb );
        pghunger = (ProgressBar) findViewById( R.id.pb2 );
        pgpower = (ProgressBar) findViewById( R.id.pb3 );


        // Code : Podométre
        sensorManager = (SensorManager) getSystemService( Context.SENSOR_SERVICE );
        scnView = (SceneView) findViewById(R.id.sceneView);

        scene = (scnView).getScene();

        renderObject();



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        getLastLocation();
        tama = new Tamagochi(100);
        startUpdate();

    }


    @Override
    public void onResume() {
        super.onResume();
        running = true;
        sensor = sensorManager.getDefaultSensor( Sensor.TYPE_STEP_COUNTER );

        if (sensor != null) {
            sensorManager.registerListener( this, sensor, SensorManager.SENSOR_DELAY_UI );
        } else {
            //step.setText( "error" );
        }

        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mp.stop();
        running = false;
        sensorManager.unregisterListener( this );
        stopUpdate();

    }

    // Code : Podométre
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // Code : Podométre
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            numSteps = (int) event.values[0];
            tama.addscore(numSteps%2);
            step.setText( "Score : " + tama.score );
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopUpdate();

    }


    // lance l'activité pour la réalitée augmenté
    public void launchAr(View v){
        startActivity(new Intent(this, ArActivity.class));
    }


    // Rendu du modéle 3d

    private void renderObject() {



       ModelRenderable.builder()
                .setSource(this, Uri.parse("slime.sfb"))
                .build()
                .thenAccept(renderable -> mon_modele = renderable);

    }

    private  void addToScene(ModelRenderable m){
        slime.setParent(scene);
        slime.setLocalPosition(new Vector3(0.f,0.f,0.f));
        slime.setLocalScale(new Vector3(3f, 3f, 3f));
        slime.setName("Slime");
        slime.setRenderable(m);

        scene.addChild(slime);

    }



    // Permission pour la localisation
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }


    // CODE : Géolocalisation
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {

                                //-------//
                                altitude.setText("Alt : " + location.getAltitude() );
                                location.getLatitude();
                                location.getLongitude();
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    // CODE : Géolocalisation
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }


    // CODE : Géolocalisation
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            //-------//
            altitude.setText("Alt : " + mLastLocation.getAltitude() );
            mLastLocation.getLatitude();
            mLastLocation.getLongitude();
        }
    };



    // Code : Tamagochi
    public void giveHealth(View v) {
        tama.heal();
        updateUi();
    }

    public void giveFood(View v) {
        tama.feed();
        updateUi();
    }

    public void givePower(View v) {
        tama.feedpower();
        updateUi();
    }

    private void startUpdate() {
        updateUi();
        slimUpdate.run();
    }

    private void stopUpdate() {
        mHandler.removeCallbacks(slimUpdate);
    }

    private void dead() {
        Intent i = new Intent(this, DeadActivity.class);
        startActivity(i);
    }

    private void updateUi() {
        pglife.setProgress((int) tama.health);
        pghunger.setProgress((int) tama.hunger);
        pgpower.setProgress((int) tama.power);

    }

    private Runnable slimUpdate = new Runnable() {
        @Override
        public void run() {

            tama.update();
            updateUi();
            if(!tama.alive) {
                dead();
            }
            mHandler.postDelayed(this,2000);

        }
    };
}

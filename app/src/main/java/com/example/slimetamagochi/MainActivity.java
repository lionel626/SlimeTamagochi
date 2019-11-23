package com.example.slimetamagochi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonplay;
    private Boolean silent = false;
    private Button silentbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        buttonplay = (Button) findViewById( R.id.buttonplay );
        silentbtn = (Button) findViewById(R.id.silentbtn);



        silentbtn.setBackgroundColor(getResources().getColor(R.color.colorGray));
        buttonplay.setOnClickListener( this );




    }

    @Override
    public void onClick(View v) {


        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("silent",silent);
        startActivity(i);




    }

    public void putSilent(View v) {
        silent = !silent;
        if(silent) {
            silentbtn.setBackgroundColor(getResources().getColor(R.color.colorBlue));
        } else {
            silentbtn.setBackgroundColor(getResources().getColor(R.color.colorGray));
        }
    }
}

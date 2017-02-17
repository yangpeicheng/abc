package com.ypc.abc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button main_walk,main_arm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_walk=(Button)findViewById(R.id.main_walk);
        main_arm=(Button)findViewById(R.id.main_arm);
        main_walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,walk.class);
                startActivity(intent);
            }
        });
        main_arm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,arm.class);
                startActivity(intent);
            }
        });
    }
}

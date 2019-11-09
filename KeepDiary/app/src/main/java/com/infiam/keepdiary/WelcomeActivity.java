package com.infiam.keepdiary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    private Button reg,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        reg = findViewById(R.id.register);
        login = findViewById(R.id.login);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAuth("new");
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAuth("ex");
            }
        });
    }

    private void gotoAuth(String state) {
        Intent auth = new Intent(getApplicationContext(),AuthActivity.class);
        auth.putExtra("state",state);
        startActivity(auth);
        finish();
    }


}

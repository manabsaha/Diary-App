package com.infiam.keepdiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DiaryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String currentUid;
    private DatabaseReference diaryRef;
    private String title,note, creationTime =null;
    private TextInputLayout inputTitle;
    private EditText inputNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        //toolbar custom
        Toolbar mToolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("KeepDiary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String getTitle = getIntent().getStringExtra("title");
        String getNote = getIntent().getStringExtra("note");
        String getTime = getIntent().getStringExtra("time");
        if(getTime!=null){
            creationTime = getTime;
        }

        inputTitle = findViewById(R.id.title);
        inputNote = findViewById(R.id.note);

        inputTitle.getEditText().setText(getTitle);
        inputNote.setText(getNote);

        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();
        diaryRef = FirebaseDatabase.getInstance().getReference();

    }

    //toolbar-menu show code
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar_option,menu);
        return true;
    }
    //toolbar-menu listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.myItem :
                title = inputTitle.getEditText().getText().toString();
                note = inputNote.getText().toString();
                if(title.length()>0 && note.length()>0){
                    saveToFireBase();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Empty fields", Toast.LENGTH_LONG).show();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveToFireBase() {
        if(creationTime ==null) {
            creationTime = getCreationTime();
        }
        HashMap<String,String> noteMap = new HashMap<>();
        noteMap.put("title",title);
        noteMap.put("note",note);
        noteMap.put("creationtime",creationTime);
        noteMap.put("color", "default");
        diaryRef.child(currentUid).child(creationTime).setValue(noteMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Saved your note",Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Failed to save",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String getCreationTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onBackPressed() {

        CharSequence options[] = new CharSequence[]{"Save","Discard"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to discard?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //click event for each item on dialog box.
                switch (i){
                    case 0:
                        title = inputTitle.getEditText().getText().toString();
                        note = inputNote.getText().toString();
                        if(title.length()>0 && note.length()>0){
                            saveToFireBase();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Empty fields", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 1:
                        finish();
                        break;
                }
            }
        });
        builder.show();
    }
}

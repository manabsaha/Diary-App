package com.infiam.keepdiary;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ViewDiaryActivity extends AppCompatActivity {

    private String creationTime;
    private DatabaseReference diaryRef;
    private FirebaseAuth mAuth;
    private String title,note,time;
    private int color;
    private TextView mTitle,mNote;
    private RelativeLayout diaryViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diary);

        //toolbar custom
        Toolbar mToolbar = findViewById(R.id.diaryViewToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("KeepDiary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        diaryViewLayout = findViewById(R.id.diaryViewLayout);

        mAuth = FirebaseAuth.getInstance();
        creationTime = getIntent().getStringExtra("creationTime");
        diaryRef = FirebaseDatabase.getInstance().getReference()
                .child(mAuth.getCurrentUser().getUid().toString()).child(creationTime);
        diaryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                title = dataSnapshot.child("title").getValue().toString();
                note = dataSnapshot.child("note").getValue().toString();
                time = dataSnapshot.child("creationtime").getValue().toString();
                color = Integer.parseInt(dataSnapshot.child("color").getValue().toString());

                mTitle = findViewById(R.id.diaryTitle);
                mNote = findViewById(R.id.diaryNote);
                mTitle.setText(title);
                mNote.setText(note);
                diaryViewLayout.setBackgroundColor(color);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    //toolbar-menu show code
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.diary_options,menu);
        return true;
    }
    //toolbar-menu listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.editDiary :
                Intent edit = new Intent(getApplicationContext(),DiaryActivity.class);
                edit.putExtra("title",title);
                edit.putExtra("note",note);
                edit.putExtra("time",time);
                startActivity(edit);
                break;

            case R.id.deleteDiary:
                diaryRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Failed to delete",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case R.id.colorDiary:
                colorDiary(diaryViewLayout,color);
                break;

            case R.id.shareDiary:
                /*ClipboardManager copyNote = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                copyNote.setText(mNote.getText());
                Toast.makeText(getApplicationContext(),"Copied to clipboard",Toast.LENGTH_SHORT).show();*/

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,title+"\n\n"+note);
                startActivity(Intent.createChooser(shareIntent,"Share using"));

        }
        return super.onOptionsItemSelected(item);
    }

    private void colorDiary(final RelativeLayout diaryViewLayout, int color) {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, final int newColor) {
                diaryRef.child("color").setValue(newColor);
            }

        });
        colorPicker.show();
    }
}

package com.infiam.keepdiary;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FloatingActionButton addDiary;
    private RecyclerView allDiaryView;
    private DatabaseReference diaryReference;
    private android.support.v7.view.ActionMode mActionMode;


    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent auth = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(auth);
            finish();
        } else {
            diaryReference = FirebaseDatabase.getInstance().getReference()
                    .child(mAuth.getCurrentUser().getUid().toString());
            FirebaseRecyclerAdapter<Diary, DiaryViewHolder> diaryRecyclerViewAdapter = new FirebaseRecyclerAdapter<Diary, DiaryViewHolder>
                    (Diary.class, R.layout.single_diary_layout, DiaryViewHolder.class, diaryReference) {

                @Override
                protected void populateViewHolder(final DiaryViewHolder viewHolder, final Diary model, int position) {

                    viewHolder.setTitle(model.getTitle());
                    viewHolder.setDate(model.getCreationtime());
                    viewHolder.setBg((int) model.getColor());

                    final String creationTime = getRef(position).getKey();  //This returns the value of the clicked item.

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(getApplicationContext(),model.getTitle() +" at "+creationTime,Toast.LENGTH_SHORT).show();
                            Intent viewDiary = new Intent(getApplicationContext(),ViewDiaryActivity.class);
                            viewDiary.putExtra("creationTime",creationTime);
                            startActivity(viewDiary);
                        }
                    });

                    viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if(mActionMode!=null){
                                return false;
                            }
                            mActionMode = startSupportActionMode(mActionModeCallback);
                            return true;
                        }
                    });
                }
            };

            allDiaryView.setAdapter(diaryRecyclerViewAdapter);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        addDiary = findViewById(R.id.newDiary);
        allDiaryView = findViewById(R.id.AllDiaryView);

        allDiaryView.setHasFixedSize(true);
        allDiaryView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //toolbar custom
        Toolbar mToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("KeepDiary");

        addDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DiaryActivity.class);
                i.putExtra("title","");
                i.putExtra("note","");
                startActivity(i);
            }
        });

    }

    //toolbar-menu show
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar_option, menu);
        MenuItem item = menu.findItem(R.id.myItem);
        item.setTitle("Logout");
        return true;
    }

    //toolbar-menu listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myItem:
                mAuth.signOut();
                Intent auth = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(auth);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    public static class DiaryViewHolder extends RecyclerView.ViewHolder {


        View mView;

        public DiaryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setTitle(String title) {
            TextView titleView = mView.findViewById(R.id.single_diary_title);
            titleView.setText(title);
        }

        public void setDate(String creationtime) {
            TextView dateView = mView.findViewById(R.id.single_diary_date);
            String date = creationtime.substring(8,10)+"-"+creationtime.substring(5,7)+"-"+creationtime.substring(2,4);
            dateView.setText(date);
        }

        public void setBg(int color) {

        }
    }

    private android.support.v7.view.ActionMode.Callback mActionModeCallback = new android.support.v7.view.ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.diary_options,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.editDiary:
                    Toast.makeText(getApplicationContext(),"edit",Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.deleteDiary:
                    Toast.makeText(getApplicationContext(),"delete",Toast.LENGTH_SHORT).show();
                    return true;
                case  R.id.colorDiary:
                    Toast.makeText(getApplicationContext(),"color",Toast.LENGTH_SHORT).show();
                    return true;
                case  R.id.shareDiary:
                    Toast.makeText(getApplicationContext(),"share",Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
            mActionMode = null;
        }
    };

}


package com.example.aws_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "태그";

    private String token;
    private Button userList;
    private Button shoppingList;
    private LinearLayout notice;
    private String SIGNIN_URL = "AWS Web Address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userList = (Button) findViewById(R.id.userList);
        shoppingList = (Button) findViewById(R.id.shoppingList);
        notice = (LinearLayout) findViewById(R.id.linearLayout);

        getToken();

        notice.setVisibility(View.GONE);
        shoppingList.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        userList.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, new UserFragment());
        fragmentTransaction.commit();

        userList.setOnClickListener(new View.OnClickListener() { // 회원 목록 버튼을 누르면
            @Override
            public void onClick(View view) {
                notice.setVisibility(View.GONE);
                shoppingList.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                userList.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new UserFragment());
                fragmentTransaction.commit();
            }
        });

        shoppingList.setOnClickListener(new View.OnClickListener() { // 주문 목록 버튼을 누르면
            @Override
            public void onClick(View view) {
                notice.setVisibility(View.GONE);
                shoppingList.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                userList.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new OrderFragment());
                fragmentTransaction.commit();
            }
        });

    }

    private void getToken() { //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        token = task.getResult(); // 사용자 토큰을 가져오고 따로 저장해둔다.
                        Log.d(TAG, "token : " + token);

                        if (token != null) {
                            class TokenConnection extends AsyncTask<Void,Void,Void>{
                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                }

                                @Override
                                protected Void doInBackground(Void... voids) {
                                    ConnectServer();
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                }
                            }
                            TokenConnection conn = new TokenConnection();
                            conn.execute();
                        }
                    }
                });
    }

    private void ConnectServer(){
        final String urlSuffix = "?token=" + token;
        class SignupUser extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(SIGNIN_URL + urlSuffix );
                    client.execute(post);
                    return "";
                } catch (Exception e) {
                    Log.d(TAG, "Error!! : " + e.getMessage());
                    return null;
                }
            }
        }
        SignupUser su = new SignupUser();
        su.execute( urlSuffix );
    }

}
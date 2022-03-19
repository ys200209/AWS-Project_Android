package com.example.aws_project;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class UserFragment extends Fragment {
    private final String TAG = "태그";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ListView userListView;
    private CustomUserAdapter adapter;
    private static List<CustomUser> userList;
    String userID = null;
    String date = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment(String userID, String date) {
        this.userID = userID;
        this.date = date;
    }

    public UserFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_fragment, container, false);

        userListView = view.findViewById(R.id.userList);
        userList = new ArrayList<CustomUser>();
        adapter = new CustomUserAdapter(UserFragment.this.getContext(), userList);

        ConnectServer();

        return view;
    }

    private void ConnectServer(){

        final String SIGNIN_URL = "http://18.117.14.150:8080/AWS_Project/json_user.jsp";
        class SignupUser extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s != null) {
                    try{
                        JSONArray jArr = new JSONArray(s);
                        JSONObject json = new JSONObject();
                        for (int i = 0; i < jArr.length(); i++) {
                            json = jArr.getJSONObject(i);
                            userList.add(0, new CustomUser(json.getString("userID"), json.getString("userPassword"), json.getString("userName"),
                                    json.getString("userGender"), json.getString("userEmail"), json.getString("userJoin")));
                            adapter.notifyDataSetChanged();
                            userListView.setAdapter(adapter);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(UserFragment.this.getContext(), "서버와의 통신에 문제가 발생했습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                BufferedReader bufferedReader = null;
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(SIGNIN_URL);
                    HttpResponse response = client.execute(post);
                    BufferedReader bufreader = new BufferedReader(
                            new InputStreamReader(
                                    response.getEntity().getContent(), "utf-8"));
                    String line = null;
                    String page = "";
                    while ((line = bufreader.readLine()) != null) {
                        page += line;
                    }
                    return page;
                } catch (Exception e) {
                    return null;
                }
            }
        }
        SignupUser su = new SignupUser();
        su.execute();
    }

    public class CustomUserAdapter extends BaseAdapter {

        private Context context;
        private List<CustomUser> customUserList;

        public CustomUserAdapter(Context context, List<CustomUser> customUserList) {
            this.context = context;
            this.customUserList = customUserList;
        }

        public class ViewHolder {
            private ImageView userGender;
            private TextView userName;
            private TextView userEmail;
            private TextView userJoin;
        }

        @Override
        public int getCount() {
            return customUserList.size();
        }

        @Override
        public Object getItem(int i) {
            return customUserList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder holder = null;
            if (view == null) {
                view = View.inflate(context, R.layout.custom_user, null);
                holder = new ViewHolder();
                holder.userGender = (ImageView) view.findViewById(R.id.userGender);
                holder.userName = (TextView) view.findViewById(R.id.userName);
                holder.userJoin = (TextView) view.findViewById(R.id.userJoin);
                holder.userEmail = (TextView) view.findViewById(R.id.userEmail);

                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            CustomUser country = userList.get(i);
            if (country.getUserGender().equals("남자")) {
                holder.userGender.setImageResource(R.drawable.human_male_icon);
            } else {
                holder.userGender.setImageResource(R.drawable.human_female_icon);
            }

            holder.userName.setText(country.getUserName()+" (" + country.getUserID() + ")");
            holder.userEmail.setText(country.getUserEmail());
            holder.userJoin.setText(country.getUserJoin());
            return view;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
package com.example.aws_project;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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

public class OrderFragment extends Fragment {
    private final String TAG = "태그";
    public static String commentId;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ListView orderListView;
    private CustomOrderAdapter adapter;
    private static List<CustomOrder> orderList;
    String userID = null;
    String date = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderFragment(String userID, String date) {
        this.userID = userID;
        this.date = date;
    }

    public OrderFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
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

        View view = inflater.inflate(R.layout.order_fragment, container, false);

        orderListView = view.findViewById(R.id.orderList);
        orderList = new ArrayList<CustomOrder>();
        adapter = new OrderFragment.CustomOrderAdapter(OrderFragment.this.getContext(), orderList);

        ConnectServer();

        return view;
    }

    private void ConnectServer(){

        final String SIGNIN_URL = "http://18.117.14.150:8080/AWS_Project/json_order.jsp";
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
                            orderList.add(new CustomOrder(json.getString("orderID"), json.getString("userID"), json.getString("itemName"),
                                    json.getString("itemPrice"), json.getString("orderDate")));
                            adapter.notifyDataSetChanged();
                            orderListView.setAdapter(adapter);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(OrderFragment.this.getContext(), "서버와의 통신에 문제가 발생했습니다", Toast.LENGTH_SHORT).show();
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

    public class CustomOrderAdapter extends BaseAdapter {

        private Context context;
        private List<CustomOrder> customUserList;

        public CustomOrderAdapter(Context context, List<CustomOrder> customUserList) {
            this.context = context;
            this.customUserList = customUserList;
        }

        public class ViewHolder {
            private LinearLayout stripe;
            private TextView orderID;
            private TextView userID;
            private TextView itemName;
            private TextView itemPrice;

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
                view = View.inflate(context, R.layout.custom_order, null);
                holder = new ViewHolder();
                holder.stripe = (LinearLayout) view.findViewById(R.id.stripe);
                holder.orderID = (TextView) view.findViewById(R.id.orderID);
                holder.userID = (TextView) view.findViewById(R.id.userID);
                holder.itemName = (TextView) view.findViewById(R.id.itemName);
                holder.itemPrice = (TextView) view.findViewById(R.id.itemPrice);

                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            CustomOrder country = orderList.get(i);
            if(i % 2 == 1) {
                holder.stripe.setBackgroundColor(Color.parseColor("#D2D2D2"));
            } else {
                holder.stripe.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            holder.orderID.setText(country.getOrderID());
            holder.userID.setText(country.getUserID());
            holder.itemName.setText(country.getItemName());
            holder.itemPrice.setText(country.getItemPrice());
            return view;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
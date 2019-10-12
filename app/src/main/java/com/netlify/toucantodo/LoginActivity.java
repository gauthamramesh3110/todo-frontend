package com.netlify.toucantodo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button login;
    Button signUp;
    EditText username;
    EditText password;
    private HashMap<String, String> headers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = Constants.BASE_URL + Constants.LOGIN;

                JSONObject body = new JSONObject();
                try {
                    body.put("username", username.getText().toString());
                    body.put("password", password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                headers = new HashMap<>();
                headers.put(Constants.CONTENT_TYPE, Constants.JSON_CONTENT_TYPE);
                final String params = body.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);

                            if (responseObject.getBoolean("success")) {
                                SharedPreferences preferences = getSharedPreferences("tokenData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                editor.putString("token", responseObject.getString("token"));
                                editor.putBoolean("loggedIn", true);
                                editor.commit();

                                Log.i("TOKEN", responseObject.getString("token"));

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                                Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();

                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        Log.e("JSON", "SENT");
                        return params == null ? null : params.getBytes();
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return headers;
                    }
                };

                queue.add(stringRequest);
            }
        });

        signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = Constants.BASE_URL + Constants.SIGN_UP;

                JSONObject body = new JSONObject();
                try {
                    body.put("username", username.getText().toString());
                    body.put("password", password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                headers = new HashMap<>();
                headers.put(Constants.CONTENT_TYPE, Constants.JSON_CONTENT_TYPE);
                final String params = body.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);

                            if (responseObject.getBoolean("success")) {
                                SharedPreferences preferences = getSharedPreferences("tokenData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                editor.putString("token", responseObject.getString("token"));
                                editor.commit();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                                Toast.makeText(getApplicationContext(), "Signed Up & Logged In", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Signup Failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Signup Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Signup Failed", Toast.LENGTH_SHORT).show();

                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        Log.e("JSON", "SENT");
                        return params == null ? null : params.getBytes();
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return headers;
                    }
                };

                queue.add(stringRequest);
            }
        });

    }
}

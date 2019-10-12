package com.netlify.toucantodo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.netlify.toucantodo.Adapters.TodoListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    RecyclerView todoList;
    TodoListAdapter adapter;
    ImageView add;
    HashMap<String, String> headers;
    JSONArray todoListData;
    TextView date;
    Button selectDate;
    ImageView addTodo;
    EditText titleEditText;

    String dateString;
    String params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("tokenData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String token = preferences.getString("token", null);
        Log.i("TOKEN", token);

        headers = new HashMap<>();
        headers.put("token", token);
        headers.put(Constants.CONTENT_TYPE, Constants.JSON_CONTENT_TYPE);

        loadList();

        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFeedbackDialog();
            }
        });
    }

    private void loadList() {
        String url = Constants.BASE_URL + Constants.GET_TODOS;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try {
                    responseObject = new JSONObject(response);
                    Log.i("LIST", responseObject.getString("result"));
                    todoListData = responseObject.getJSONArray("result");

                    todoList = findViewById(R.id.todoList);
                    adapter = new TodoListAdapter(todoListData, headers, getApplicationContext());
                    todoList.setAdapter(adapter);
                    todoList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error loading todo list", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error loading todo list", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        queue.add(request);
    }

    private void showFeedbackDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_todo_popup);

        dialog.show();

        selectDate = dialog.findViewById(R.id.selectDate);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        date = dialog.findViewById(R.id.date);
        titleEditText = dialog.findViewById(R.id.titleEditText);

        addTodo = dialog.findViewById(R.id.addTodo);
        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject data = new JSONObject();
                try {
                    data.put("title", titleEditText.getText().toString());
                    data.put("date", dateString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params = data.toString();

                String url = Constants.BASE_URL + Constants.ADD_TODO;
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getBoolean("success")) {
                                Toast.makeText(getApplicationContext(), "Todo Added", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Unable to add Todo", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Unable to add Todo", Toast.LENGTH_SHORT).show();
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

                requestQueue.add(stringRequest);
                dialog.dismiss();
                loadList();
            }
        });


        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(R.drawable.pop_up_background);
        window.setWindowAnimations(R.style.DialogAnimation);
    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.i("DATE: ", year + " " + month + " " + day);

        dateString = year + "-" + month + "-" + day;
        date.setText(day + "st " + new DateFormatSymbols().getMonths()[month] + ", " + year);
    }
}

package com.netlify.toucantodo.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.netlify.toucantodo.Constants;
import com.netlify.toucantodo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder> {

    JSONArray list;
    HashMap<String, String> headers;
    Context context;

    public TodoListAdapter(JSONArray list, HashMap<String, String> headers, Context context) {
        this.list = list;
        this.headers = headers;
        this.context = context;
    }

    @NonNull
    @Override
    public TodoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item, parent, false);
        TodoListViewHolder holder = new TodoListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TodoListViewHolder holder, final int position) {
        try {
            JSONObject itemObject = list.getJSONObject(position);

            if (itemObject.getBoolean("completed")) {
                holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.checkCircle.setImageResource(R.drawable.check_circle_checked);
            } else {
                holder.checkCircle.setImageResource(R.drawable.check_circle_unchecked);
            }

            if (itemObject.getBoolean("important")) {
                holder.important.setImageResource(R.drawable.important);
            } else {
                holder.important.setImageResource(R.drawable.not_important);
            }

            holder.title.setText(itemObject.getString("title"));

            holder.checkCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("ONCLICK", Integer.toString(position));
                    try {
                        toggleCompleted(list.getJSONObject(position).getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.length();
    }

    void toggleCompleted(String todoId) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("todoId", todoId);
        final String params = data.toString();
        String url = Constants.BASE_URL + Constants.TOGGLE_COMPLETED;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

        requestQueue.add(request);
    }

    public static class TodoListViewHolder extends RecyclerView.ViewHolder {

        ImageView checkCircle;
        ImageView important;
        TextView title;

        public TodoListViewHolder(@NonNull View itemView) {
            super(itemView);

            checkCircle = itemView.findViewById(R.id.checkCircle);
            important = itemView.findViewById(R.id.important);
            title = itemView.findViewById(R.id.todoTitle);
        }
    }
}

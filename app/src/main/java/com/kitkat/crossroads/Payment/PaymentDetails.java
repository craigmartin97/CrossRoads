package com.kitkat.crossroads.Payment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kitkat.crossroads.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity
{

    private TextView textViewId, textViewAmount, textViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        getViewsWidgetsByIds();

        Intent intent = getIntent();

        try
        {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }


    }

    private void showDetails(JSONObject response, String paymentAmount)
    {
        try
        {
            textViewId.setText(response.getString("id"));
            textViewStatus.setText(response.getString("state"));
            textViewAmount.setText(response.getString(String.format("$%s", paymentAmount)));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void getViewsWidgetsByIds()
    {
        textViewId = findViewById(R.id.textId);
        textViewAmount = findViewById(R.id.textAmount);
        textViewStatus = findViewById(R.id.textStatus);
    }

}

package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CurrencyActivity extends Activity
{
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void convert(View view)
    {
        final String baseCcy = ((TextView) findViewById(R.id.baseCcy)).getText().toString().trim().toUpperCase();
        final String quoteCcy = ((TextView) findViewById(R.id.quoteCcy)).getText().toString().trim().toUpperCase();
        final Double baseQty = Double.valueOf(((TextView) findViewById(R.id.baseCcyQty)).getText().toString());

        final TextView result = (TextView) findViewById(R.id.result);

        final HttpResponse response = fireHttpRequest(baseCcy, quoteCcy, baseQty);

        if (response == null)
        {
            result.setText("ERROR");
            return;
        }

        try
        {
            final InputStream content = response.getEntity().getContent();
            final InputStreamReader reader = new InputStreamReader(content);
            final BufferedReader bufferedReader = new BufferedReader(reader);

            String line = bufferedReader.readLine();

            if (line != null)
            {
                try
                {
                    final JSONObject jsonObject = new JSONObject(line);

                    if (!jsonObject.getBoolean("icc"))
                    {
                        result.setText("ERROR");
                        return;
                    }

                    final String lhs = jsonObject.getString("lhs");
                    final String rhs = jsonObject.getString("rhs");
                    result.setText(lhs + " = " + rhs);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private HttpResponse fireHttpRequest(String base, String quote, double amount)
    {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpGet httpGet = new HttpGet(String.format("http://www.google.com/ig/calculator?hl=en&q=%f%s=?%s", amount, base, quote));

        try
        {
            HttpResponse response = client.execute(httpGet);
            return response;
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}

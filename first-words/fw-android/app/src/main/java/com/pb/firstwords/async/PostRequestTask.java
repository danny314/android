package com.pb.firstwords.async;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

/**
 * Created by puneet on 10/20/14.
 */
public abstract class PostRequestTask extends AsyncTask<String, Void, String> {

    private Map<String,String> postParams = null;

    public PostRequestTask(Map<String,String> postParams) {
        this.postParams = postParams;
    }

    private AsyncHttpJsonResponseHandler delegate;

    public AsyncHttpJsonResponseHandler getDelegate() {
        return this.delegate;
    }

    public void setDelegate(AsyncHttpJsonResponseHandler _delegate) {
        this.delegate = _delegate;
    }

    @Override
    protected String doInBackground(String... urls) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(urls[0]);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();

            if (this.postParams != null) {
                Set<String> keys = this.postParams.keySet();
                for (String key : keys) {
                    jsonObject.accumulate(key, this.postParams.get(key));
                }

            }


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                //Log.d(DEBUG_TAG, "http response = " + result);
            }
            else {
                result = "Did not work!";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    @Override
    protected void onPostExecute(String result) {
        getDelegate().processJsonResponse(result);
    }
}

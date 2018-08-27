package com.example.cfibqqz.restapiexample2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    ListView listView;



    class Docs {
        String document_id;
        String document_name;
        String document_author;
        String document_thumbnail_url;
        String document_url;

        Docs(String document_id, String document_name, String document_author, String document_thumbnail_url, String document_url) {
            this.document_id = document_id;
            this.document_name = document_name;
            this.document_author = document_author;
            this.document_thumbnail_url = document_thumbnail_url;
            this.document_url = document_url;
        }
    }
    private ArrayList<Docs> docs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button search_btn=(Button)findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText search_val=(EditText)findViewById(R.id.search_val);
                String search_value=search_val.getText().toString();
                //System.out.println(search_value);
                listView=(ListView)findViewById(R.id.listView);
                //Log.d("Restapiex", "button clicked");
                new LoadAllDocs().execute("http://192.168.137.1/dse/api/searchDocument", search_value);
            }
        });

        /////////////////////

/*        listView.setOnItemClickListener(this);
*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class LoadAllDocs extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            try {
                URL url=new URL(args[0]);
                String search_value=args[1];
                //System.out.println("Search:"+search_value);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                JSONObject search_object=new JSONObject();
                search_object.put("document_search_string",search_value);
                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(String.valueOf(search_object)); // json data
                //System.out.println(String.valueOf(search_object));
                writer.close();


                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer response=new StringBuffer();
                    String inputLine;
                    while ((inputLine=br.readLine())!=null){
                        response.append(inputLine);
                    }
                    br.close();
                    System.out.println(response.toString());
                    return response.toString();
                }else{
                    return "Error";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String result) {
            // dismiss the dialog after getting all docs
            //Log.d("Restapiex", result+"1");
            super.onPostExecute(result);
            //Log.d("Restapiex", result+"2");
            if(result!=null){
                docs=new ArrayList<>();
                //Log.d("Restapiex", result+"3");
                //System.out.println(result);
                try {
                    JSONObject jsonRootObject = new JSONObject(result);

                    int code=jsonRootObject.getInt("code");

                    //Log.d("Restapiex","asdfasdfasdfasdf");

                    if(code==200){

                        JSONArray jsonArray =jsonRootObject.optJSONArray("response");

                        //JSONObject jsonResponseObject =jsonRootObject.getJSONObject("response");
                        //JSONArray jsonArray = jsonResponseObject.optJSONArray("document_list");

                        for(int i=0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                           // System.out.println("User" + i + ": " + jsonObject.getString("document_name"));
                            docs.add(new Docs(jsonObject.getString("document_id"),jsonObject.getString("document_name"),jsonObject.getString("document_author"),jsonObject.getString("document_thumbnail_url"),jsonObject.getString("document_url")));
                        }
                        CustomAdapter customAdapter=new CustomAdapter(MainActivity.this, docs);
                        listView.setAdapter(customAdapter);
                        listView.setOnItemClickListener(
                                new AdapterView.OnItemClickListener()
                                {
                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

                                        //Take action here.

                                        // Build the intent
                                        File file = new File(docs.get((int) id).document_url);
                                        MimeTypeMap map = MimeTypeMap.getSingleton();
                                        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                                        String type = map.getMimeTypeFromExtension(ext);

                                        if (type == null)
                                            type = "*/*";

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        //Uri data = Uri.fromFile(file);
                                        String data = docs.get((int) id).document_url;

                                        intent.setDataAndType(Uri.parse(data), type);

                                        //Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
                                        //Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

                                        // Verify it resolves
                                        PackageManager packageManager = getPackageManager();
                                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                                        boolean isIntentSafe = activities.size() > 0;

                                        // Start an activity if it's safe
                                        if (isIntentSafe) {
                                            //Log.d("Restapiex", String.valueOf(data));
                                            startActivity(intent);
                                        }

                                        //String temp;
                                        //temp = docs.get((int) id).document_url;
                                        //Log.d("Restapiex",temp);

                                    }
                                }
                        );
                    }else{
                        String message=jsonRootObject.getString("message");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.d("Restapiex", "qwerqwerqwerqwer");
                }
            }else{
            }
        }
    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream("/sdcard/file_name.extension");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }
}

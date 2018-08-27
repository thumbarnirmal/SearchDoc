package com.example.cfibqqz.restapiexample2;//////////////////////////////////////////////////////////////////////////////

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import com.bumptech.glide.Glide;

public class CustomAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private Context context;
    // As the name suggests, its the context of current state of the application/object. It lets newly created objects understand what has been going on.
    // Typically you call it to get information regarding another part of your program (activity, package/application)
    private ArrayList<MainActivity.Docs> docsArrayList;

    public CustomAdapter(Context context, ArrayList<MainActivity.Docs> docsArrayList) {
        this.context=context;
        this.docsArrayList=docsArrayList;

        /* Layout Inflater to call external xml layout () */
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Basically it is needed to create (or fill) View based on XML file in runtime.
    }

    @Override
    public int getCount() {
        if(docsArrayList.size()<=0)
            return 1;
        return docsArrayList.size();
    }

    @Override
    public Object getItem(int  position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        //convertView is used to reuse old view.
        View vi = convertView;
        ViewHolder holder;
        if(convertView==null){

            /****** Inflate card_view.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.list_item, null);

            /****** View Holder Object to contain card_view.xml file elements ******/

            holder = new ViewHolder();
            holder.document_id = (TextView) vi.findViewById(R.id.document_id);
            holder.document_name=(TextView)vi.findViewById(R.id.document_name);
            holder.document_author=(TextView)vi.findViewById(R.id.document_author);
            holder.document_thumbnail_url=(ImageView)vi.findViewById(R.id.document_thumbnail_url);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }else {
            holder = (ViewHolder) vi.getTag();
        }

        if(docsArrayList.size()<=0)
        {
            holder.document_id.setText("No Data");
            holder.document_name.setVisibility(View.GONE);
            holder.document_author.setVisibility(View.GONE);
            holder.document_thumbnail_url.setVisibility(View.GONE);

        }
        else {

            //Log.d("Restapiex", "11111");
            MainActivity.Docs document=docsArrayList.get(position);
            holder.document_id.setText("Doc ID: "+document.document_id);
            holder.document_name.setText("Name: "+document.document_name);
            holder.document_author.setText("Author: " + document.document_author);

            URL url = null;
            try {
                url = new URL(document.document_thumbnail_url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            //ImageView imageView = (ImageView) findViewById(R.id.document_thumbnail_url);
            Glide.with(context).load(url.toString()).into(holder.document_thumbnail_url);

            //Log.d("Restapiex", "22222");
            //URL url = null;
            //Bitmap bmp;
            //Log.d("Restapiex", "33333");
            /*try {
                Log.d("Restapiex", "44444");
                url = new URL(document.document_thumbnail_url);
                Log.d("Restapiex", url.toString());
                //bmp = BitmapFactory.decodeStream((InputStream)new URL(document.document_thumbnail_url).getContent());
                //bmp = GetBitmapfromUrl(url.toString());
                //bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                //Log.d("Restapiex", bmp.toString());
                //holder.document_thumbnail_url.setImageBitmap(bmp);
                //Log.d("Restapiex", "77777");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                //Log.d("Restapiex", "88888");
            } catch (IOException e) {
                e.printStackTrace();
                //Log.d("Restapiex", "99999");
            }*/
            //setText("Author: "+document.document_author);
        }

            return vi;
    }

    /*public Bitmap GetBitmapfromUrl(String scr) {
        try {
            URL url=new URL(scr);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            //Log.d("Restapiex", "55555");
            InputStream input=connection.getInputStream();
            //Log.d("Restapiex", "66666");
            Bitmap bmp = BitmapFactory.decodeStream(input);
            return bmp;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }*/


    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView document_id;
        public TextView document_name;
        public TextView document_author;
        public ImageView document_thumbnail_url;
    }

}

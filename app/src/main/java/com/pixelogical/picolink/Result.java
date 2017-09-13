package com.pixelogical.picolink;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Result extends AppCompatActivity {

    static TextView test;
    ArrayList<DataModel> dataModels;
    ListView listView;
    private static CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
//         test = (TextView) findViewById(R.id.textView);
//        test.setText(getIntent().getStringExtra("picoLink"));

        listView = (ListView) findViewById(R.id.resultListView);

        dataModels = new ArrayList<>();

//        dataModels.add(new DataModel("Apple Pie", "Android 1.0", "1","September 23, 2008"));
//        dataModels.add(new DataModel("Banana Bread", "Android 1.1", "2","February 9, 2009"));
//        dataModels.add(new DataModel("Cupcake", "Android 1.5", "3","April 27, 2009"));
//        dataModels.add(new DataModel("Donut","Android 1.6","4","September 15, 2009"));
//        dataModels.add(new DataModel("Eclair", "Android 2.0", "5","October 26, 2009"));
//        dataModels.add(new DataModel("Froyo", "Android 2.2", "8","May 20, 2010"));
//        dataModels.add(new DataModel("Gingerbread", "Android 2.3", "9","December 6, 2010"));
//        dataModels.add(new DataModel("Honeycomb","Android 3.0","11","February 22, 2011"));
//        dataModels.add(new DataModel("Ice Cream Sandwich", "Android 4.0", "14","October 18, 2011"));
//        dataModels.add(new DataModel("Jelly Bean", "Android 4.2", "16","July 9, 2012"));
//        dataModels.add(new DataModel("Kitkat", "Android 4.4", "19","October 31, 2013"));
//        dataModels.add(new DataModel("Lollipop","Android 5.0","21","November 12, 2014"));
//        dataModels.add(new DataModel("Marshmallow", "Android 6.0", "23","October 5, 2015"));
        try {
            JSONObject root = new JSONObject(getIntent().getStringExtra("result"));
            Log.e("JS000000N", root.toString());
            Iterator<String> iter = root.keys();
            while (iter.hasNext()) {
                String site = iter.next();
                JSONObject items = root.getJSONObject(site);
                Iterator<String> iter2 = items.keys();
                while (iter2.hasNext()) {
                    String fileName = iter2.next();
                    Object fileUrlObject = items.get(fileName);
                    String fileUrl = fileUrlObject.toString();
                    dataModels.add(new DataModel(fileName, site, fileUrl, "October 5, 2017"));
                    /////////////////////////////  name  , type, version number
                }
//                for (int i = 0; i < xmlLinksArray.length(); i++) {
//                    String link = xmlLinksArray.getString(i);
//                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new CustomAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModel dataModel = dataModels.get(position);

                    
//                Snackbar.make(view, dataModel.getName() + "\n" + dataModel.getType() + " API: " + dataModel.getVersion_number(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
                try {
                    String linkAddress = dataModel.type + dataModel.version_number;
//                    MimeTypeMap mime = MimeTypeMap.getSingleton();
//                    String endOfUrl = linkAddress.substring(linkAddress.length() - 5);
//                    String ext = endOfUrl.substring(endOfUrl.indexOf(".") + 1);
//                    String type = mime.getMimeTypeFromExtension(ext);
//
//                    Intent i = new Intent(Intent.ACTION_SEND);
//                    i.setType(type);
//                    i.putExtra(Intent.EXTRA_SUBJECT, "PicoLink");
//                    i.putExtra(Intent.EXTRA_TEXT, linkAddress);
//                    startActivity(Intent.createChooser(i, "Open with"));

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(linkAddress));
                    startActivity(i);
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });
    }
}

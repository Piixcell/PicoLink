package com.pixelogical.picolink;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button ok;
    String word = "minions";
    static int videosDigits[] = {R.id.ex_3gp, R.id.ex_mpeg, R.id.ex_mp4, R.id.ex_mkv, R.id.ex_flv, R.id.ex_avi, R.id.ex_mov};
    static int audiosDigits[] = {R.id.ex_mp3, R.id.ex_wma, R.id.ex_ogg, R.id.ex_wav, R.id.ex_flac, R.id.ex_aac, R.id.ex_amr};
    static int documentsDigits[] = {R.id.ex_srt,R.id.ex_pdf, R.id.ex_docx, R.id.ex_doc, R.id.ex_txt, R.id.ex_xml};
    static int applicationsDigits[] = {R.id.ex_exe, R.id.ex_apk, R.id.ex_jar};
    static int compressedDigits[] = {R.id.ex_zip, R.id.ex_rar, R.id.ex_7z, R.id.ex_deb, R.id.ex_targz, R.id.ex_gz, R.id.ex_bin};

    static CheckBox[] videoCB;
    static CheckBox[] audioCB;
    static CheckBox[] documentCB;
    static CheckBox[] applicationCB;
    static CheckBox[] compressCB;

    static CheckBox[] CategoryCB;
    static AVLoadingIndicatorView x;
    FloatingActionButton fab;
    SearchView sv;
    ProgressBar pb;
    TextView txtCurrentLink;
    TextView txtScannig;
    Button skip;
    static int progress = 0;
    static String currentLink = "";
    String googleExtensions;
    String yahooExtensions;
    ArrayList<String> totalExtensions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeCBs();

        x = (AVLoadingIndicatorView) findViewById(R.id.avi);
        x.hide();
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        sv = (SearchView) findViewById(R.id.searchView);
        sv.setImeOptions(EditorInfo.IME_ACTION_DONE);
        sv.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateQuery(sv.getQuery())) {
                    googleExtensions = getGoogleExtensionsFromCB();
                    Toast.makeText(MainActivity.this, googleExtensions, Toast.LENGTH_SHORT).show();
                    ConnectionThread connection = new ConnectionThread(new Connection());
                    connection.start();
                }
            }
        });

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        txtCurrentLink = (TextView) findViewById(R.id.currentLink);
        txtScannig = (TextView) findViewById(R.id.txtScannig);
        txtScannig.setVisibility(View.INVISIBLE);
        txtCurrentLink.setVisibility(View.INVISIBLE);
        skip = (Button) findViewById(R.id.button);
        skip.setVisibility(View.INVISIBLE);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection.skip = true;
            }
        });
    }

    private boolean validateQuery(CharSequence query) {
        if (query.length() == 0) {
            Snackbar.make((ConstraintLayout) findViewById(R.id.rootLayout), "Please input your file name", Snackbar.LENGTH_SHORT)
                    .setAction("No action", null).show();
        } else {
            String word = query.toString();
            if (word.matches("[_a-zA-Z0-9\\- ]+") && word.length() < 15) {
                this.word = word.replaceAll(" ", "\\+");
                return true;
            } else {
                Snackbar.make((ConstraintLayout) findViewById(R.id.rootLayout), "Invalid or long file name", Snackbar.LENGTH_SHORT)
                        .setAction("No action", null).show();
            }
        }
        return false;
    }

    private void initializeCBs() {
        categoryListener c = new categoryListener();

        videoCB = new CheckBox[videosDigits.length];
        for (int i = 0; i < videosDigits.length; i++) {
            videoCB[i] = new CheckBox(this);
            videoCB[i] = (CheckBox) findViewById(videosDigits[i]);
        }
        audioCB = new CheckBox[audiosDigits.length];
        for (int i = 0; i < audiosDigits.length; i++) {
            audioCB[i] = new CheckBox(this);
            audioCB[i] = (CheckBox) findViewById(audiosDigits[i]);
        }
        documentCB = new CheckBox[documentsDigits.length];
        for (int i = 0; i < documentsDigits.length; i++) {
            documentCB[i] = new CheckBox(this);
            documentCB[i] = (CheckBox) findViewById(documentsDigits[i]);
        }
        applicationCB = new CheckBox[applicationsDigits.length];
        for (int i = 0; i < applicationsDigits.length; i++) {
            applicationCB[i] = new CheckBox(this);
            applicationCB[i] = (CheckBox) findViewById(applicationsDigits[i]);
        }
        compressCB = new CheckBox[compressedDigits.length];
        for (int i = 0; i < compressedDigits.length; i++) {
            compressCB[i] = new CheckBox(this);
            compressCB[i] = (CheckBox) findViewById(compressedDigits[i]);
        }

        CategoryCB = new CheckBox[5];
        CategoryCB[0] = (CheckBox) findViewById(R.id.cat_video);
        CategoryCB[1] = (CheckBox) findViewById(R.id.cat_audio);
        CategoryCB[2] = (CheckBox) findViewById(R.id.cat_document);
        CategoryCB[3] = (CheckBox) findViewById(R.id.cat_application);
        CategoryCB[4] = (CheckBox) findViewById(R.id.cat_compressed);
        for (int i = 0; i < 5; i++)
            CategoryCB[i].setOnCheckedChangeListener(c);


    }

    private ArrayList<String> parseHTML(String result) {
        ArrayList<String> sites = new ArrayList<>();
        if (result == "" || result == "disconnect") {
            Log.e("ParseHTML", "NO RESULT");
        } else {
            Document doc = Jsoup.parse(result);
            Elements links = doc.getElementsByTag("cite");
            for (Element link : links) {
                sites.add(link.text());
                Log.e("CITE", link.text());
            }
        }
        return sites;
    }

    private ArrayList<String> parseXML(String result) {
        ArrayList<String> resultLinks = new ArrayList<>();
        if (result == "" || result == null || result == "disconnect") {
            Log.e("ParseXML", "NO XML RESULT");
            return null;
        } else {
//            int maxLogSize = 1000;
//            for(int i = 0; i <= result.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > result.length() ? result.length() : end;
//                Log.e("WEBPAGE !!!!", result.substring(start, end));
//            }
//            Log.e("FINISH","--------------------------------------------------------------------------------");
            Document doc = Jsoup.parse(result);
            Elements rows = doc.getElementsByTag("td");
            for (Element row : rows) {
                final Elements links = row.getElementsByTag("a");
                for (Element a : links) {
                    String name = a.text();
                    String address = a.attr("href");
                    String[] words = word.split("\\+");
                    boolean validFile = false;
                    for (int i = 0; i < totalExtensions.size(); i++)
                        if (address.toLowerCase().contains(totalExtensions.get(i))) {
                            validFile = true;
                            break;
                        }
                    if (validFile)
                        for (int i = 0; i < words.length; i++) {
                            if (address.toLowerCase().contains(words[i].toLowerCase())) {
                                resultLinks.add(name);
                            }
                        }
                }
            }
        }
        return resultLinks;
    }

    private ArrayList<String> parseXML2(String result) {
        ArrayList<String> resultLinks = new ArrayList<>();
        if (result == "" || result == null || result == "disconnect") {
            Log.e("ParseXML", "NO XML RESULT");
            return null;
        } else {
            try {
                Document doc = Jsoup.parse(result);
                Elements pres = doc.getElementsByTag("pre");
                Element pre = pres.get(0);
                final Elements links = pre.getElementsByTag("a");
                for (Element a : links) {
                    String name = a.text();
                    String address = a.attr("href");
                    String[] words = word.split("\\+");
                    boolean validFile = false;
                    for (int i = 0; i < totalExtensions.size(); i++)
                        if (name.toLowerCase().contains(totalExtensions.get(i))) {
                            validFile = true;
                            break;
                        }
                    if (validFile)
                        for (int i = 0; i < words.length; i++) {
                            if (name.toLowerCase().contains(words[i].toLowerCase())) {
                                resultLinks.add(name);
                            }
                        }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return resultLinks;
    }

    public String getGoogleExtensionsFromCB() {
        String ex = "";
        totalExtensions = new ArrayList<>();
        for (int i = 0; i < videoCB.length; i++) {
            if (videoCB[i].isChecked()) {
                String CBex = videoCB[i].getText().toString();
                CBex = CBex.substring(1, CBex.length());
                ex += CBex + "%7C";
                totalExtensions.add(CBex);
            }
        }
        for (int i = 0; i < audioCB.length; i++) {
            if (audioCB[i].isChecked()) {
                String CBex = audioCB[i].getText().toString();
                CBex = CBex.substring(1, CBex.length());
                ex += CBex + "%7C";
                totalExtensions.add(CBex);
            }
        }
        for (int i = 0; i < documentCB.length; i++) {
            if (documentCB[i].isChecked()) {
                String CBex = documentCB[i].getText().toString();
                CBex = CBex.substring(1, CBex.length());
                ex += CBex + "%7C";
                totalExtensions.add(CBex);
            }
        }
        for (int i = 0; i < applicationCB.length; i++) {
            if (applicationCB[i].isChecked()) {
                String CBex = applicationCB[i].getText().toString();
                CBex = CBex.substring(1, CBex.length());
                ex += CBex + "%7C";
                totalExtensions.add(CBex);
            }
        }
        for (int i = 0; i < compressCB.length; i++) {
            if (compressCB[i].isChecked()) {
                String CBex = compressCB[i].getText().toString();
                CBex = CBex.substring(1, CBex.length());
                ex += CBex + "%7C";
                totalExtensions.add(CBex);
            }
        }
        if (ex != null && !ex.isEmpty() && !ex.matches(""))
            ex = ex.substring(0, ex.length() - 3);
        return ex;
    }

    // ========inner class
    private class ConnectionThread extends Thread {

        private Connection connection;
        private String htmlResult = null;
        private String xmlPage = null;
        private ArrayList<String> html = null;
        private ArrayList<String> xmlLinks = null;
        private JSONObject result;

        public ConnectionThread(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                x.show();
                html = new ArrayList<>();
                xmlLinks = new ArrayList<>();
                result = new JSONObject();
                String code = "%5B" + word
                        + "%5D+-inurl%3A(htm%7Chtml%7Cphp%7Cpls%7Ctxt)+intitle%3Aindex.of+\"last+modified\"+(" + googleExtensions + ")&oq=%5Bdeadpool%5D+-inurl%3A(htm%7Chtml%7Cphp%7Cpls%7Ctxt)+intitle%3Aindex.of+\"last+modified\"+(" + googleExtensions + ")&ie=UTF-8";
                Log.e("PIC0LinK  ~> ", code);
                this.htmlResult = connection.connect("https://google.com/search?q=" + code);
                html = parseHTML(this.htmlResult);
                if (!html.isEmpty()) {
                    for (int i = 0; i < html.size(); i++) {
                        MainActivity.progress = (i + 1) * 10;
                        currentLink = html.get(i);
                        Log.e("PROGRESS %% ", MainActivity.progress + "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtScannig.setVisibility(View.VISIBLE);
                                txtCurrentLink.setVisibility(View.VISIBLE);
                                skip.setVisibility(View.VISIBLE);
                                txtCurrentLink.setText(currentLink);
                                pb.setVisibility(View.VISIBLE);
                                pb.setProgress(MainActivity.progress);
                            }
                        });
                        if (html.get(i) != null) {
                            String site = "http://" + html.get(i);
                            connection = new Connection();
                            xmlPage = connection.connect(site);
                            xmlLinks = parseXML(xmlPage);
                            if (xmlPage != null && xmlLinks != null && xmlLinks.size() != 0) {
                                Log.e(")))", "X: " + xmlLinks.get(0));
                                JSONArray xmlLinksArray = new JSONArray();
                                for (String a : xmlLinks) {
                                    xmlLinksArray.put(a);
                                }
                                result.put(site, xmlLinksArray);
                            } else {
                                xmlLinks = parseXML2(xmlPage);
                                if (xmlPage != null && xmlLinks != null && xmlLinks.size() != 0) {
                                    Log.e(")))", "X: " + xmlLinks.get(0));
                                    JSONArray xmlLinksArray = new JSONArray();
                                    for (String a : xmlLinks) {
                                        xmlLinksArray.put(a);
                                    }
                                    result.put(site, xmlLinksArray);
                                } else {
                                    Log.e(")))", "NULLLLLLL");
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            handler.sendEmptyMessage(0);
        }

        private Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                updateUI();
            }

            private void updateUI() {
                x.hide();
                MainActivity.progress = 0;
                pb.setProgress(0);
                pb.setVisibility(View.INVISIBLE);
                skip.setVisibility(View.INVISIBLE);
                txtScannig.setVisibility(View.INVISIBLE);
                txtCurrentLink.setVisibility(View.INVISIBLE);
                try {
                    Intent myIntent = new Intent(MainActivity.this, Result.class);
                    myIntent.putExtra("result", result.toString());
                    MainActivity.this.startActivity(myIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } catch (Exception e) {
                }
            }
        };
    }

    class categoryListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {

                case R.id.cat_video:
                    if (buttonView.isChecked()) {
                        for (int i = 0; i < videoCB.length; i++) {
                            videoCB[i].setChecked(true);
                        }
                    } else {
                        for (int i = 0; i < videoCB.length; i++) {
                            videoCB[i].setChecked(false);
                        }
                    }
                    break;
                case R.id.cat_audio:
                    if (buttonView.isChecked()) {
                        for (int i = 0; i < audioCB.length; i++) {
                            audioCB[i].setChecked(true);
                        }
                    } else {
                        for (int i = 0; i < audioCB.length; i++) {
                            audioCB[i].setChecked(false);
                        }
                    }
                    break;
                case R.id.cat_document:
                    if (buttonView.isChecked()) {
                        for (int i = 0; i < documentCB.length; i++) {
                            documentCB[i].setChecked(true);
                        }
                    } else {
                        for (int i = 0; i < documentCB.length; i++) {
                            documentCB[i].setChecked(false);
                        }
                    }
                    break;
                case R.id.cat_application:
                    if (buttonView.isChecked()) {
                        for (int i = 0; i < applicationCB.length; i++) {
                            applicationCB[i].setChecked(true);
                        }
                    } else {
                        for (int i = 0; i < applicationCB.length; i++) {
                            applicationCB[i].setChecked(false);
                        }
                    }
                    break;
                case R.id.cat_compressed:
                    if (buttonView.isChecked()) {
                        for (int i = 0; i < compressCB.length; i++) {
                            compressCB[i].setChecked(true);
                        }
                    } else {
                        for (int i = 0; i < compressCB.length; i++) {
                            compressCB[i].setChecked(false);
                        }
                    }
                    break;
            }
        }
    }
}

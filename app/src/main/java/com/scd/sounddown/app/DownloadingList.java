package com.scd.sounddown.app;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class DownloadingList extends ListActivity {

    // URL to get tracks JSON
    private static String url = "https://api.soundcloud.com/resolve.json?url=https://soundcloud.com/ahmad-el-batanouni/sets/top-favs&client_id=22e8f71d7ca75e156d6b2f0e0a5172b3";

    // JSON Node names
    private static final String TAG_TRACKS = "tracks";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_IMAGE = "artwork_url";
    private static final String TAG_DOWNLOAD_LINK = "mobile";

    // tracks JSONArray
    JSONArray tracks = null;

    public static String[] titles;
    public static String[] images;
    public static String[] downloadLinks;
    public static Bitmap[] bitmaps;


    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloading_list);
        String url = getIntent().getStringExtra("url");
        this.url = "https://api.soundcloud.com/resolve.json?url=" + url + "&client_id=22e8f71d7ca75e156d6b2f0e0a5172b3";
//        Toast.makeText(this, url, 3).show();
        new GetTracks().execute();
    }


    public void download(String url) {
        new DownloadFileFromURL().execute(url);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }


    private class GetTracks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DownloadingList.this);
            pDialog.setMessage("Getting Tracks... Please Be Patient");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    tracks = jsonObj.getJSONArray(TAG_TRACKS);

                    titles = new String[tracks.length()];
                    images = new String[tracks.length()];
                    downloadLinks = new String[tracks.length()];
                    // looping through All Contacts
                    for (int i = 0; i < tracks.length(); i++) {
                        JSONObject c = tracks.getJSONObject(i);


                        String waveform_url = c.getString("waveform_url");
                        String downloading_part = waveform_url.substring(0, 34).substring(22, 34);
                        String download_url = "http://media.soundcloud.com/stream/" + downloading_part + "?stream_token=6Umq7";


                        String id = c.getString(TAG_ID);
                        String title = c.getString(TAG_TITLE);
                        String image = c.getString(TAG_IMAGE);

                        titles[i] = title;
                        images[i] = image;
                        downloadLinks[i] = download_url;


//                        try {
//                            URL url = new URL(image);
//                            HttpGet httpRequest = null;
//
//                            httpRequest = new HttpGet(url.toURI());
//
//                            HttpClient httpclient = new DefaultHttpClient();
//                            HttpResponse response = (HttpResponse) httpclient
//                                    .execute(httpRequest);
//
//                            HttpEntity entity = response.getEntity();
//                            BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
//                            InputStream input = b_entity.getContent();
//
////                            bitmaps[i] = BitmapFactory.decodeStream(input);
//
////                            img.setImageBitmap(bitmap);
//
//                        } catch (Exception ex) {
//                            Log.d("ImageDownloadError", ex.getMessage());
//                        }

                        // tmp hashmap for single contact
//                        HashMap<String, String> contact = new HashMap<String, String>();
//
//                        // adding each child node to HashMap key => value
//                        contact.put(TAG_ID, id);
//                        contact.put(TAG_TITLE, title);
//                        contact.put(TAG_IMAGE, image);
//                        contact.put(TAG_DOWNLOAD_LINK, download_url);
//
//                        // adding contact to contact list
//                        trackList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            setListAdapter(new CustomAdapter(DownloadingList.this, titles, images, downloadLinks));
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "" + System.currentTimeMillis() + ".mp3");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

        }

    }

}

package appyhours.www.whodat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    public ImageView mImageView = null;
    public TextView mTextView = null;
    public TextView mTextView2 = null;
    public Button mButton = null;
    public Context mContext = null;
    public TextToSpeech tts = null;
    public ToggleButton mSwitch = null;
    private Button searchButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.imageView);
        mButton = findViewById(R.id.button);
        mTextView = findViewById(R.id.details);
        mTextView2 = findViewById(R.id.details2);
        searchButton = findViewById(R.id.search_screen);

        mContext = this;
        Log.i("Carboon", "onCreate: MAIN ACTIVITY");
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        mSwitch = (ToggleButton) findViewById(R.id.switch1);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

//            Log.i("Carboon", "onActivityResult: "+imageEncoded);
            makePost2(imageEncoded);
        }
    }

//    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
//    {
//        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
//        image.compress(compressFormat, quality, byteArrayOS);
//        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
//    }

    public void makePost2(final String image) {
        mTextView.setText("Processing . . .");
        mTextView2.setText("");
        mSwitch.setVisibility(View.INVISIBLE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://35.204.157.241:5001/classify");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("base64image", image);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());
                    String responseString = readFullyAsString(conn.getInputStream(), "UTF-8");
                    Log.i("carboon", responseString);
                    try{
                    JSONObject obj = new JSONObject(responseString);
//                    Log.i("carboon", obj.toString());
                    //JSONArray pred = obj.getJSONArray("prediction");
//                    Log.i("carboon", pred.toString());
                        //Log.i("carboon", pred.getString(0) + " " + pred.getString(1) + " "+ pred.getString(2));
                    //final String displayString = "Looks like a: \n" + pred.getString(0) + "\nor " + pred.getString(1) + "\nor " + pred.getString(2);
                    final String displayString = obj.getString("caption");

                    if (mSwitch.isChecked()) {
                        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if(status == TextToSpeech.SUCCESS){
                                    tts.speak(displayString, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        });
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mTextView.setText(displayString);
                            mSwitch.setVisibility(View.VISIBLE);
                        }
                    });

                    } catch(Exception e){
                        Log.i("carboon", e.getMessage());
                        mTextView.setText("Something went wrong :(");
                        mSwitch.setVisibility(View.VISIBLE);
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
        return readFully(inputStream).toString(encoding);
    }

    private ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }


}

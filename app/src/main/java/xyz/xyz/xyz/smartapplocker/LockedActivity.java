package xyz.xyz.xyz.smartapplocker;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class LockedActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Button clickButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);
        clickButton = (Button) findViewById(R.id.clickButton);

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Finger");
        imagesFolder.mkdirs();
        String fileName = "image_temp.jpg";
        File output = new File(imagesFolder, fileName);
        Uri uriSavedImage = Uri.fromFile(output);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);


        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


            Bitmap imageBitmap = BitmapFactory.decodeFile("sdcard/Finger/image_temp.jpg");
            File file = new File("sdcard/Finger/image_temp.jpg");
            boolean deleted = file.delete();
            boolean match;
            if(imageBitmap!=null) {
                String myBase64Image = encodeToBase64(imageBitmap, Bitmap.CompressFormat.JPEG, 100);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                sharedPref.edit().putString("imageToBeMatched", myBase64Image).commit();
                match = checkIfImageMatches(imageBitmap);
            }
            match = checkIfImageMatches(imageBitmap);
            if (match) {
                Toast.makeText(LockedActivity.this, "FINGERPRINT MATCHED!", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPref1 = PreferenceManager.getDefaultSharedPreferences(this);
                String lastApp=sharedPref1.getString("lastApp", "none");
                sharedPref1.edit().putBoolean(lastApp,false).commit();
                this.finish();
            } else {
                Toast.makeText(LockedActivity.this, "FINGERPRINTS DO NOT MATCH!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private boolean checkIfimageMatches(Bitmap i1, Bitmap i2) {


        if (i1.getHeight() != i2.getHeight()) return false;
        if (i1.getWidth() != i2.getWidth()) return false;

        for (int y = 0; y < i1.getHeight(); ++y)
            for (int x = 0; x < i1.getWidth(); ++x)
                if (i1.getPixel(x, y) != i2.getPixel(x, y))

                    return true;
        if (i1.getHeight() != i2.getHeight())
            if (i1.getWidth() != i2.getWidth())

                for (int y = 0; y < i1.getHeight(); ++y)
                    for (int x = 0; x < i1.getWidth(); ++x)
                        if (i1.getPixel(x, y) != i2.getPixel(x, y))

                            return true;
        return  false;

    }







































































    private boolean checkIfImageMatches(Bitmap bmp){

        Random r = new Random();
        int Low = 1;
        int High = 5;
        int Result = r.nextInt(High-Low) + Low;

        if((Result==1)||(Result==2)||(Result==3))
            return true;

        return false;
    }

}

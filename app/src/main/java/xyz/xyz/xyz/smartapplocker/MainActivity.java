package xyz.xyz.xyz.smartapplocker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView apps;
    PackageManager packageManager;
    TreeSet<String> checkedValue;
    SharedPreferences sharedPref;
    HashSet<String> tempSet,lockedappList;
    HashSet<String> allApps;
    Button bt1,lockButton;
    private int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt1 = (Button) findViewById(R.id.button1);
        apps = (ListView) findViewById(R.id.listView1);

        lockButton=(Button)findViewById(R.id.lockButton);
        tempSet= new HashSet<String>();
        checkedValue= new TreeSet<String>();
        checkedValue.clear();
        sharedPref= PreferenceManager.getDefaultSharedPreferences(this);


        lockedappList=(HashSet<String>)sharedPref.getStringSet("LockedApps",tempSet);


        Intent intent = new Intent();
        intent.setAction("com.finger.START_MYREC");
        sendBroadcast(intent);


        packageManager = getPackageManager();

        final List<PackageInfo> packageList = packageManager
                .getInstalledPackages(PackageManager.GET_META_DATA); // all apps in the phone
        final List <PackageInfo> packageList1 = packageManager
                .getInstalledPackages(0);

        try {
            packageList1.clear();
            for (int n = 0; n < packageList.size(); n++)
            {

                PackageInfo PackInfo = packageList.get(n);
                if (((PackInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) != true)
                //check weather it is system app or user installed app
                {
                    try
                    {

                        packageList1.add(packageList.get(n)); // add in 2nd list if it is user installed app
                        Collections.sort(packageList1, new Comparator<PackageInfo>()
                                // this will sort App list on the basis of app name
                        {
                            public int compare(PackageInfo o1, PackageInfo o2) {
                                return o1.applicationInfo.loadLabel(getPackageManager()).toString()
                                        .compareToIgnoreCase(o2.applicationInfo.loadLabel(getPackageManager())
                                                .toString());// compare and return sorted packagelist.
                            }
                        });


                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        allApps=new HashSet<String>();
        for(PackageInfo inf:packageList1){
            String appName = packageManager.getApplicationLabel(
                    inf.applicationInfo).toString();

            allApps.add(appName);
        }
        sharedPref.edit().putStringSet("allApps",allApps).commit();

        Listadapter Adapter = new Listadapter(this,packageList1, packageManager);
        apps.setAdapter(Adapter);
        apps.setOnItemClickListener(this);
        bt1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });


        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.edit().putStringSet("LockedApps",lockedappList).commit();
                for(String s:lockedappList){
                    sharedPref.edit().putBoolean(s, true).commit();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                //enocde the string and store it in the app datastore
                String myBase64Image = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
                sharedPref.edit().putString("passwordFingerprint", myBase64Image).commit();

                Log.d("MainActivity", "onActivityResult: uri::"+uri.toString());
                Log.d("MainActivity", "onActivityResult: bas64 string::"+myBase64Image);
                //ImageView imageView = (ImageView) findViewById(R.id.imageView1);
               // imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
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

    @Override
    public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
        // TODO Auto-generated method stub
        CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
        TextView tv = (TextView) v.findViewById(R.id.textView1);
        Log.d("MAIN ACTIBVI", "Checkbox is clicked on item" + tv.getText().toString());
        cb.performClick();

        if (cb.isChecked()) {
            lockedappList.add(tv.getText().toString());
            Log.d("MAIN ACTIBVI", "Checkbox is checked");
        } else if (!cb.isChecked()) {
            lockedappList.remove(tv.getText().toString());
            Log.d("MAIN ACTIBVI", "Checkbox is unchecked");
        }



    }
}

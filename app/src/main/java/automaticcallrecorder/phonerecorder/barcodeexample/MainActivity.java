package automaticcallrecorder.phonerecorder.barcodeexample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


import java.io.IOException;

import automaticcallrecorder.phonerecorder.barcodeexample.Models.Product;
import automaticcallrecorder.phonerecorder.barcodeexample.Models.Rootmodel;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class MainActivity extends AppCompatActivity {

    TextView txtView;
    ImageView myImageView;
    BarcodeDetector detector;
    Bitmap myBitmap;
    SurfaceView cameraView;
    CameraSource cameraSource;
    RecyclerView myrecycle;
    CircleImageView scan , barcodimg;
    Toolbar mytoolbar;
    Button enterbarcode;
    android.app.AlertDialog.Builder builder;
    android.app.AlertDialog alertDialog;
    productsdatabase mydatabase;
    Call<Rootmodel> mcall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       mydatabase=new productsdatabase(this);





        mytoolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setCustomView(R.layout.cutom_action_bar);
        View view =getSupportActionBar().getCustomView();

       enterbarcode=findViewById(R.id.barcodenumber);
       myrecycle = findViewById(R.id.productrecycle);
        myrecycle.setHasFixedSize(true);
        myrecycle.setLayoutManager(new LinearLayoutManager(this));
       myrecycle.setAdapter(new Recycleadapter(this));

    scan= view.findViewById(R.id.camerascan);
    barcodimg=view.findViewById(R.id.barcodee);

        barcodimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(MainActivity.this,Camera_activity.class);
                startActivityForResult(intent1,0);

            }
        });

        enterbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText myedit;
                Button ok,cancel;

                builder = new android.app.AlertDialog.Builder(MainActivity.this);

               View myview = LayoutInflater.from(MainActivity.this.getApplicationContext()).inflate(R.layout.layoutenterbar, null);
               myedit=myview.findViewById(R.id.barcodedittext);
               ok=myview.findViewById(R.id.okk);
               cancel=myview.findViewById(R.id.cancell);
                builder.setView(myview);
                alertDialog = builder.create();
                alertDialog.show();


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        OkHttpClient.Builder builderr = new OkHttpClient.Builder();

                        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                        builderr.addInterceptor(loggingInterceptor);


                        Retrofit retrofitt = new Retrofit.Builder()
                                .baseUrl("https://api.barcodelookup.com/v2/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(builderr.build())
                                .build();

                        final Endpoints myendpoints = retrofitt.create(Endpoints.class);

                        mcall = myendpoints.getbarcodedetails(myedit.getText().toString());
                        mcall.enqueue(new Callback<Rootmodel>() {
                            @Override
                            public void onResponse(Call<Rootmodel> call, Response<Rootmodel> response) {

                                if (response.isSuccessful())
                                {
                                    Product newproduct= response.body().getProducts().get(0);
                                    mydatabase.insertdataforproduct(newproduct.getBarcodeNumber(),newproduct.getProductName(),newproduct.getCategory(),
                                            newproduct.getBrand(),newproduct.getColor(),newproduct.getDescription(),newproduct.getImages().get(0));
                                }
                                else{
                                    mydatabase.insertdataforproduct(myedit.getText().toString(),null,null,null,null,null,null);

                                }



                            }

                            @Override
                            public void onFailure(Call<Rootmodel> call, Throwable t) {

                                mydatabase.insertdataforproduct(myedit.getText().toString(),null,null,null,null,null,null);

                            }
                        });





                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        });

        cameraView =  findViewById(R.id.camera_view);

        /** Check if this device has a camera */
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            Toast.makeText(this,"The device has A Camera",Toast.LENGTH_LONG).show();
        } else {
            // no camera on this device
            Toast.makeText(this,"The device Not has A Camera",Toast.LENGTH_LONG).show();


        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(getApplicationContext(), "Please accept permission!", Toast.LENGTH_LONG).show();


            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();

        }



    }

/*
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Call<Rootmodel> mycall;

        if (requestCode==0)
        {
            if (resultCode== CommonStatusCodes.SUCCESS)
            {
                if (data!=null)
                {
                    final String barcodedata= String.valueOf(data.getParcelableExtra("open")) ;
                    OkHttpClient.Builder builderr = new OkHttpClient.Builder();

                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    builderr.addInterceptor(loggingInterceptor);


                    Retrofit retrofitt = new Retrofit.Builder()
                            .baseUrl("https://api.barcodelookup.com/v2/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(builderr.build())
                            .build();

                    final Endpoints myendpoints = retrofitt.create(Endpoints.class);

                    mycall = myendpoints.getbarcodedetails(barcodedata);
                    mycall.enqueue(new Callback<Rootmodel>() {
                        @Override
                        public void onResponse(Call<Rootmodel> call, Response<Rootmodel> response) {

                           if (response.isSuccessful())
                           {
                               Product newproduct= response.body().getProducts().get(0);
                               mydatabase.insertdataforproduct(newproduct.getBarcodeNumber(),newproduct.getProductName(),newproduct.getCategory(),
                                       newproduct.getBrand(),newproduct.getColor(),newproduct.getDescription(),newproduct.getImages().get(0));
                           }
                           else{
                               mydatabase.insertdataforproduct(barcodedata,null,null,null,null,null,null);

                           }



                        }

                        @Override
                        public void onFailure(Call<Rootmodel> call, Throwable t) {

                            mydatabase.insertdataforproduct(barcodedata,null,null,null,null,null,null);

                        }
                    });
                    builder = new android.app.AlertDialog.Builder(MainActivity.this);

                    View myview = LayoutInflater.from(MainActivity.this.getApplicationContext()).inflate(R.layout.additemdialog, null);

                    builder.setView(myview);
                    alertDialog = builder.create();
                    alertDialog.show();


                }
                else{
                    Toast.makeText(MainActivity.this,"No barcode found",Toast.LENGTH_LONG).show();
                }



            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /*
        Button btn =  findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Barcode> barcodes = detector.detect(frame);

                Barcode thisCode = barcodes.valueAt(0);
                txtView.setText(thisCode.rawValue);



            }
        });

*/



    }





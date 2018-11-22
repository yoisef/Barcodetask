package automaticcallrecorder.phonerecorder.barcodeexample;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AllPermission;

public class Camera_activity extends AppCompatActivity {

    BarcodeDetector detector;
    TextView resultscan;

    SurfaceView cameraView;
    CameraSource cameraSource;
    android.app.AlertDialog.Builder builder;
    android.app.AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_activity);

        resultscan = findViewById(R.id.resultmyscan);
        cameraView = findViewById(R.id.camera_view);



        detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(0)
                        .build();
        if (!detector.isOperational()) {
            //  txtView.setText("Could not set up the detector!");
            Toast.makeText(Camera_activity.this, "Could not set up the detector!", Toast.LENGTH_LONG).show();

            return;
        }

        cameraSource = new CameraSource
                .Builder(this, detector)
                .setRequestedPreviewSize(640, 480)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {

                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });



        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();


                if (barcodes.size() != 0) {
                    Intent myintent=new Intent();
                    myintent.putExtra("open", barcodes.valueAt(0).rawValue);
                    setResult(CommonStatusCodes.SUCCESS,myintent);
                    finish();



                }
            }
        });
    }


}

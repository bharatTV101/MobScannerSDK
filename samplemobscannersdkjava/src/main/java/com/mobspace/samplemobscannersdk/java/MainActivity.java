package com.mobspace.samplemobscannersdk.java;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import com.mobspace.docscanner.sdk.MobScannerSDK;
import com.mobspace.docscanner.sdk.config.CameraConfig;
import com.mobspace.docscanner.sdk.config.PDFConfig;
import com.mobspace.docscanner.sdk.helpers.Image2Pdf;
import com.mobspace.docscanner.sdk.interfaces.IDocumentReadyListener;
import com.mobspace.docscanner.sdk.interfaces.ISdkInitCallback;
import com.mobspace.docscanner.sdk.v1.InputImagePicker;
import com.mobspace.docscanner.sdk.v1.MobScannerActivityCreator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ContentLoadingProgressBar loader;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loader = findViewById(R.id.loader);
        MobScannerSDK.init("F38F995DCA22D54114AD90B8515660E2FF218D44", "32937f98-6e8e-451d-a932-fd64b9c7635e", this, new ISdkInitCallback() {
            @Override
            public void onInitSuccess() {

                Toast.makeText(getApplicationContext(), "Init success", Toast.LENGTH_SHORT).show();
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onInitError(@NotNull String message) {

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                loader.setVisibility(View.GONE);
            }
        });

        MobScannerSDK.setTheme(R.style.MsScannerTheme);

        checkPermission();
    }

    // check for required permission
    void checkPermission() {

        String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        String cameraPermission = Manifest.permission.CAMERA;
        List<String> list = new ArrayList<>();

        boolean permissionGranted = ContextCompat.checkSelfPermission(this, writePermission) == PackageManager.PERMISSION_GRANTED;
        if (!permissionGranted) {
            list.add(writePermission);
        }
        permissionGranted = ContextCompat.checkSelfPermission(this, readPermission) == PackageManager.PERMISSION_GRANTED;
        if (!permissionGranted) {
            list.add(readPermission);
        }

        permissionGranted = ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED;
        if (!permissionGranted)
            list.add(cameraPermission);

        if (list.size() > 0) {

            String[] array = new String[list.size()];
            // Requesting the permission
            ActivityCompat.requestPermissions(
                    this, list.toArray(array),
                    1000
            );
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case InputImagePicker.IMAGE_PICKER_CAMERA:
            case InputImagePicker.IMAGE_PICKER_GALLERY: {
                if (resultCode == Activity.RESULT_OK) {

                    ArrayList<String> files = InputImagePicker.getImages(data);
                    // Image output directory. Can be External storage or App own Storage using context.getExternalFilesDir
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+getString(R.string.app_name)+"/sample");

                    if(!dir.exists())
                        dir.mkdirs();

                    MobScannerActivityCreator.Builder builder = new MobScannerActivityCreator.Builder()
                            .with(this)
                            .setOutputDirectory(dir.getAbsolutePath())
                            .setInputFiles(files)
                            .observeForFinalDocument(this, arrayList -> {

                                if (arrayList != null && !arrayList.isEmpty()) {

                                    if (files.size() > 1) {
                                        String outputFile = Image2Pdf.createPdf(
                                                arrayList,
                                                "output_file",
                                                getApplicationContext(),
                                                new PDFConfig()
                                        );

                                        Toast.makeText(MainActivity.this, "PDF save at " + outputFile, Toast.LENGTH_LONG).show();

                                    } else {

                                        Toast.makeText(MainActivity.this,
                                                "Image save at " + arrayList.get(0).getAbsolutePath(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    builder.build().create();

                }
            }

        }

    }

    public void onClick(View view) {

        if (!MobScannerSDK.isInitialized()) {
            Toast.makeText(getApplicationContext(), "SDK not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (view.getId()) {

            case R.id.camera: {

                CameraConfig cameraConfig = new CameraConfig();
                cameraConfig.setShowCapturePreview(true);
                InputImagePicker.fromCamera(this,InputImagePicker.IMAGE_PICKER_CAMERA,cameraConfig);
                break;
            }
            case R.id.gallery: {

                InputImagePicker.fromGallery(false, this);
                break;
            }
        }
    }
}
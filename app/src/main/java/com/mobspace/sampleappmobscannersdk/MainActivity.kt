package com.mobspace.sampleappmobscannersdk

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar

import com.mobspace.docscanner.sdk.MobScannerSDK
import com.mobspace.docscanner.sdk.config.PDFConfig
import com.mobspace.docscanner.sdk.helpers.Image2Pdf
import com.mobspace.docscanner.sdk.interfaces.IDocumentReadyListener
import com.mobspace.docscanner.sdk.interfaces.ISdkInitCallback
import com.mobspace.docscanner.sdk.v1.InputImagePicker
import com.mobspace.docscanner.sdk.v1.MobScannerActivityCreator
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var loader: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loader = findViewById(R.id.loader)
        MobScannerSDK.init("0922c2075ed349eee0b54d610848aad1de285e36","32937f98-6e8e-451d-a932-fd64b9c7635e",this,object : ISdkInitCallback{
            override fun onInitError(message: String) {

                Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
                loader.visibility = View.GONE
            }

            override fun onInitSuccess() {

                Toast.makeText(applicationContext,"Init success",Toast.LENGTH_SHORT).show()
                loader.visibility = View.GONE
            }
        })

        MobScannerSDK.setTheme(R.style.MsScannerTheme)

        checkPermission()

    }

    // check for required permission
    fun checkPermission() {

        val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val cameraPermission = Manifest.permission.CAMERA
        val list = mutableListOf<String>()

        var permissionGranted = ContextCompat.checkSelfPermission(this, writePermission) == PackageManager.PERMISSION_GRANTED
        if(!permissionGranted) {
            list.add(writePermission)
        }
        permissionGranted = ContextCompat.checkSelfPermission(this, readPermission) == PackageManager.PERMISSION_GRANTED
        if(!permissionGranted) {
            list.add(readPermission)
        }

        permissionGranted = ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED
        if(!permissionGranted)
            list.add(cameraPermission)

        if (list.size > 0) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this, list.toTypedArray(),
                1000
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){

            InputImagePicker.IMAGE_PICKER_CAMERA,InputImagePicker.IMAGE_PICKER_GALLERY->{
                if(resultCode == Activity.RESULT_OK){

                    val files = InputImagePicker.getImages(data)

                    // Image output directory. Can be External storage or App own Storage using context.getExternalFilesDir
                    val dir = File(
                        Environment.getExternalStorageDirectory()
                            .absolutePath + "/" + getString(R.string.app_name) + "/sample"
                    )

                    if (!dir.exists())
                        dir.mkdirs()

                    MobScannerActivityCreator.Builder()
                        .with(this)
                        .setInputFiles(files!!)
                        .setOutputDirectory(dir.absolutePath)
                        .observeForFinalDocument(this, object: IDocumentReadyListener{

                            override fun onDocumentReady(files: ArrayList<File>?) {

                            if (files != null && files.isNotEmpty()) {

                                if(files.size > 1) {
                                    val outputFile = Image2Pdf.createPdf(
                                        files,
                                        "output_file",
                                        applicationContext,
                                        PDFConfig()
                                    )

                                    Toast.makeText(this@MainActivity,
                                        "PDF save at $outputFile",Toast.LENGTH_LONG).show()

                                }else{

                                    Toast.makeText(this@MainActivity,
                                        "Image save at "+files[0].absolutePath,Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                    }).build().create()
                }
            }

        }

    }

    public fun onClick(v: View){

        if(!MobScannerSDK.isInitialized()){
            Toast.makeText(applicationContext,"SDK not initialized",Toast.LENGTH_SHORT).show()
            return
        }

        when(v.id){

            R.id.camera->{

                InputImagePicker.fromCamera(this)
            }
            R.id.gallery->{

                InputImagePicker.fromGallery(false,this)
            }
        }
    }
}
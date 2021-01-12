# MobScannerSDK for Android
The Mobscanner App (Available for Android at https://play.google.com/store/apps/details?id=com.mera.doc.scanner), designed to perform faster image scanning and produce quality PDFs with few clicks. The App aims to bring best quality images by removing shadows and any other noise from the input image. 
The heart of the App, the MobScanner SDK provides out of the box accurate, fast, and reliable mobile document scanning SDK for Android and [IOS](https://github.com/mobspace/MobScannerSDK-IOS). Use any smart device to quickly and accurately digitize the documents you need. Implement the Ready-To-Use UI (RTU UI) with only a few lines of code cuts down the development cost and time for the business. 

## Requirements
Operating System

•	Android 5.0 (API Level 21) and higher

Hardware

•	Rear-facing camera with autofocus

•	Supported CPUs and Architectures (ABIs): armeabi-v7, arm64-v8a, x86, x86_64

## No Internet Connection Required
The MobScanner SDK works completely offline. All data generated by the MobScanner SDK is only stored on the end user’s device and in absolutely no case ever transferred to a server / cloud service controlled by us. You as the customer will need to take care of uploading the scans / data to your backend, if desired.

## Features
Pick images from camera or gallery

Supports multiple image scanning

Inbuilt ID Card maker

Auto edge detection

Alignment correction 

Perspective Transformation

Intelligent Image Filter for enhancement

Offline mode 

API support

## Using SDK

We will explain the usage of the SDK in the following section.

### SDK setup

The SDK can be downloaded from maven using gradle. Add below line inside app module gradle file dependency section

```
implementation 'com.mobspace.mobscanner:docscannerlib:0.1.14-beta' // or latest version
```

Also, add below lines in the project level gradle file

```
allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url 'https://dl.bintray.com/blogtech90/MobScannerSDK/'
        }
        maven { url 'https://jitpack.io' }
    }
}
```

Additionally add runtime permission request for *android.permission.WRITE_EXTERNAL_STORAGE*,*android.permission.READ_EXTERNAL_STORAGE*,*android.permission.CAMERA*.

```
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
    ActivityCompat.requestPermissions(this, list.toArray(array),0);
}
```

### SDK API Key 
In order to use the SDK, the developer needs an Unique Account Id (UUID) and an API Key. The UUID defines an unique ID for a vendor/bussiness, while API Key is specific to an App owned by the Vendor and it is unique to the "App Package Id". The same can be obtained by [contacting us](mailto:blogtech90@gmail.com). However, for evaluation purpose there is an API key/UUID available in the sample project. Download the project and open in Android Studio to run the App. Please note, the given demo key wouldn't work in any other App. The sample project contains test apps both in Java and Kotlin. 

### SDK Initialization
The SDK can be integrated in Kotlin or Java based Android Apps. Once you get the API key and UUID, use the below code to initialize the App. 

```
MobScannerSDK.init(YOUR_API_KEY, YOUR_UUID, getApplicationContext(), new ISdkInitCallback() {
       @Override
       public void onInitSuccess() {
                Toast.makeText(getApplicationContext(), "Init successful", Toast.LENGTH_SHORT).show();      
        }
        @Override
        public void onInitError(@NotNull String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();         
        }
});
```

`MobScannerSDK.init()` is an asyn call. The initialization status would be reported through `ISdkInitCallback` interface callback. The Initialization would fail for invalid UUID or API Key.

### Setting Up Theme
Optionally, you can set theme to match the SDK UI with your App. Create or modify theme colour inside your `styles.xml`

```
<style name="MsScannerTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <item name="windowActionModeOverlay">true</item>
        <item name="windowActionBar">false</item>
        <item name="windowNoTitle">true</item>
</style>

```

Set the Theme using

```
MobScannerSDK.setTheme(R.style.MsScannerTheme);
```

### Using Camera
Before using Camera or Gallery, make sure that your App has necessary permissions as described above. 

Call `InputImagePicker.fromCamera` from an Activity instance. The captured images from the Camera Activity would be returned using `onActivityResult` callback.
```
InputImagePicker.fromCamera(Activity activity);
```
or
```
InputImagePicker.fromCamera(Activity activity, int requestCode, CameraConfig cameraConfig)
```
You can pass your own request code to handle inside onActivityResult callback. Additionally, you can pass `CameraConfig` object to control different camera UI options.

### Camera Config
The `CameraConfig` class controls different UI options associated with the camera preview. Below are the methods available-

`setShowGrid`- Flag to show a 3x3 grid on the camera preview. Default value `true`

`setEnableZoom`- Flag to enable/disable pinch zoom. Default value `true`

`setExposureCorrection`- Flag to enable/disable exposure correction on vertical scroll up/down on the image preview. Default value `true`

`setShowCapturePreview` - Flag to enable/disable capture preview once an image has been captured. Default value `false`

`setSingleImageMode` - Flag to capture only one image (single shot). Default value `false`

### Using Gallery
You can request the SDK to load Gallery images using-
```
InputImagePicker.fromGallery(boolean isSingleSelect, Activity activity);
```
Where `isSingleSelect` flag enables the SDK to select single or multiple images from the Gallery. 

### Handling Camera or Gallery Result
Override `onActivityResult` to handle to camera or gallery image selection results

```
public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case InputImagePicker.IMAGE_PICKER_CAMERA:
            case InputImagePicker.IMAGE_PICKER_GALLERY: {
                if (resultCode == Activity.RESULT_OK) {

                    // list of captured images
                    ArrayList<String> files = InputImagePicker.getImages(data);
                    
              }
          }

      }
}
```

### Using In-built Image Editor Activity
Our RTU SDK UI enables you to quickly edit images and apply filters. 
```
// Create optional output directory to store output images. Can be External storage or App own Storage using context.getExternalFilesDir
File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+getString(R.string.app_name)+"/sample");
if(!dir.exists())
      dir.mkdirs();

 MobScannerActivityCreator.Builder builder = new MobScannerActivityCreator.Builder()
      .with(getApplicationContext())
      .setOutputDirectory(dir.getAbsolutePath()) // optional
      .setInputFiles(files) // files captured from Camera or Gallery
      // add observer for images, when user finishes edit. parameter 'this' should be LifecycleOwner
      .observeForFinalDocument(this, arrayList -> { 

            //Final images output as list
          
       });

 builder.build().create();
```
### Generating PDF
By default, the SDK would store images as JPEG in phone's persistent storage. You can use inbuilt PDF generator to create PDF from images.
```
// jpegList the list of JPEG images
String outputFile = Image2Pdf.createPdf(jpegList,"pdf_file_name",getApplicationContext(),new PDFConfig());
Toast.makeText(MainActivity.this, "PDF save at " + outputFile, Toast.LENGTH_LONG).show();
```

`PDFConfig` class provides options to customize the generated PDF. Below are the methods to provide available options-

`setQuality`- To set the quality of the output images. Being 0 as min and 100 as max value. Default value set to 90

`setApplyAutoMargin`- Set flag to apply margin to the PDF pages. Default value `false`.

`setPageSize`- PageSize of the PDF. A4 is the default size. Supported sizes are- A3,A4,A5,Letter,PostCard,Legal

`setWaterMarkEnabled`- Flag to set enable water mark on the left bottom position of the PDF page. Default value `false`.

`setWaterMarkText`- Text to display as custom water mark text.

### Support
For any issue of the SDK please [write to us](mailto:blogtech90@gmail.com). Or you can create a github issue [here](https://github.com/bharatTV101/MobScannerSDK/issues)

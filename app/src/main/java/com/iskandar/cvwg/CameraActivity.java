package com.iskandar.cvwg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.zip.Inflater;

import static android.os.Environment.DIRECTORY_PICTURES;

public class CameraActivity extends AppCompatActivity implements Animation.AnimationListener {

    // related to photo-album
    List<ImageView> myAlbumPhotos; // to store imageViews data (thumbnails: 20x smaller)
    File[] sdDirFiles; // to store directories of images // same index as imageViews
    GridView gridAlbum; // gridView pointer
    CameraAlbumAdapter myAdapter; // adapter to connect DATA to gridView

    ImageButton btnBack, btnShoot;
    CheckBox checkAutoRotate;
    TextView txtLoading;
    ProgressBar progressBar;
    Context context;
    Animation entryLeft, entryRight, entryTop, entryBottom;

    // camera shoot stuff
    final private int CAPTURE_IMAGE_PERMISSION_REQUEST_CODE = 11;
    final private int CAMERA_RESULT = 22;
    ImageView imgPictureTmp; // temporary, to hold image, for check //
    String fileName,directoryName; //pointer to fileName & its parent directory
    boolean havePermission; // if we have a permission to the camera & read-write permissions


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        setPointers();
        setListeners();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) checkForPermission();

        refreshAlbum(); // load images to List & re-set Adapter! //

    }

    @Override
    protected void onStart() {
        super.onStart();
        startAnimations();
    }

    private void refreshAlbum() {

        // check permissions, if we have them .... before loading/reading files from storage ! //
        if (!havePermission) {
            Toast.makeText(context, "missing permissions! .. Cannot load images to gallery!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        loadImagesToList(); // AsyncTask, so it won't slow down activity loading !
    }

    @SuppressLint("StaticFieldLeak")
    private void loadImagesToList() {
        txtLoading.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                myAlbumPhotos.clear(); // to start fresh //
                int picIndex=1;
                createDir(directoryName); // if not exist !! // to avoid errors //
                File sdDir = new File(directoryName);
                sdDirFiles = sdDir.listFiles();
                for(File singleFile : sdDirFiles)
                {
                    ImageView myImageView = new ImageView(context);
                    BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
                    bmpOptions.inSampleSize = 20; // to handle memory well ! // decrease dimensions by 20/dimension //
                    Bitmap resizedBmp = BitmapFactory.decodeFile(singleFile.getAbsolutePath(),bmpOptions);
                    myImageView.setImageBitmap(resizedBmp);
                    myImageView.setId(picIndex);
                    myAlbumPhotos.add(myImageView);

                    publishProgress(picIndex++,sdDirFiles.length);
                }
                SystemClock.sleep(2500); // "special effects ! "
                return null;
            }
            /*

            // another method to resize bitmap by changing matrix
            // and creating new bitmap from the original
            // i.e.  POST-SCALE .... not PRE-SCALE as is done above !

            private Bitmap getDownsizedBitmap(Bitmap originalBmp, int scaleFactor) {
                // create a matrix for the manipulation
                Matrix matrix = new Matrix();
                // resize the bit-map
                matrix.postScale(1.0f/scaleFactor, 1.0f/scaleFactor);
                // re-create the new Bitmap
                Bitmap resizedBitmap = Bitmap.createBitmap(originalBmp, 0, 0,
                        originalBmp.getWidth(), originalBmp.getHeight(), matrix, false);
                // free up memory
                originalBmp.recycle();
                return resizedBitmap;

            }
            */

            @Override
            protected void onProgressUpdate(Integer... values) {
                txtLoading.setText(new StringBuilder(values[0]+"/"+values[1]));
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //gridAlbum.setAdapter(new CameraAlbumAdapter(context,myAlbumPhotos)); // recreate & reconnect adapter ! //
                myAdapter.notifyDataSetChanged(); // better way to do it // less garbage collection !!
                txtLoading.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    private void setListeners() {

        checkAutoRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAutoRotate.setTextColor(checkAutoRotate.isChecked()?
                        Color.rgb(255,255,0)
                        :Color.rgb(0,120,255));
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootPhoto();
            }
        });


        gridAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // to get image at full-scale, we get it from file directly
                // and not from myAlbumPhotos list (downSized at load-time!)
                showImageDialog(position,checkAutoRotate.isChecked());
            }
        });

        gridAlbum.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showImgDeleteDialog(position);
                return true;
            }
        });


        // animation listeners
        entryLeft.setAnimationListener(this);
        entryRight.setAnimationListener(this);
        entryTop.setAnimationListener(this);
        entryBottom.setAnimationListener(this);
        // startAnimations(); // moved to onStart
    }

    private void startAnimations() {
        btnShoot.startAnimation(entryTop);
        btnBack.startAnimation(entryLeft);
        checkAutoRotate.startAnimation(entryRight);
        //gridAlbum.startAnimation(entryBottom);
    }


    private void shootPhoto() {

        // check permissions, if we have them .... before shooting ! //
        if (!havePermission) {
            Toast.makeText(context, "missing permissions!", Toast.LENGTH_SHORT).show();
            checkForPermission();
            return;
        }

        // by this point, we have all needed permissions ....

        //to avoid api26+ policy restrictions
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // check external storage STATE FIRST ! //
        if(!extStorageOK()) return; // exit, no need to continue, as photo cannot be stored ! //

        File file = assignFile(); // assign file & create directory if NOT exist

        // android image capture + add filepath to store it (if taken by user eventually!) //
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        //since it can create an exception, we surround with try/catch to avoid app-crash
        try {
            //call the intent and wait for result after intent is closed
            startActivityForResult(intent, CAMERA_RESULT);
        } catch (Exception e) {
            Log.e("Err_PHOTO_SHOOT", "shootPhoto():Intent_Exception " + e.getMessage());
        }
    }

    private File assignFile() {
        fileName = directoryName + File.separator + "img_"+getTimeStamp() + ".jpg";
        createDir(directoryName); // if NOT exist! //
        return new File(fileName);
    }

    private void createDir(String dirName) {
        File directory = new File(dirName);
        // create directory, if NOT exist! // If the parent dir doesn't exist, create it //
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                Log.e("DIR", "Successfully created the parent dir:" + directory.getName());
            } else {
                Log.e("DIR", "Failed to create the parent dir:" + directory.getName());
            }
        }
    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return sdf.format(new Date());
    }

    private boolean extStorageOK() {
        String extStorageState = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
                Toast.makeText(context, "Storage Media is READ-ONLY!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "Storage ERROR!", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK  && requestCode==CAMERA_RESULT) {
            //get our saved file into a bitmap object
            final File file = new File(fileName);
            //read the image from the file and convert it to a bitmap
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            // show result of capture // rotate as required //
            showImageDialog(myBitmap,checkAutoRotate.isChecked());
            // refresh GridView with new image
            refreshAlbum();
        }
    }

    private int getImageOrientation(String fileName) {
        //get exif, from the image, so we will know orientation of the image
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert exif != null;
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }

    private Bitmap rotateImage(Bitmap myBitmap, int orientation) {
        //we create a matrix, so we can put the image on it and just rotate
        //it will be much faster then copy pixel by pixel
        Matrix matrix = new Matrix();
        //depending on the orientation that we got from the exif, we will rotate
        //in this sample we will deal with all kind of rotating from api 15 to api 27
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                //all is o.k. no need to rotate, just return the image
                return myBitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                //flip the matrix horizontal
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                //rotate the matrix 180 degrees....
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                //flip the picture vertical
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                //rotate 90 def and flip
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                //rotate 90 deg.
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                //rotate 90 deg to other side and flip
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                //rotate 90 deg to other side
                matrix.setRotate(-90);
                break;
            default:
                return myBitmap;
        }
        try {
            //create an image from our rotated solution
            Bitmap bmRotated = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
            //recycle the data by calling the garbage collector
            //in this case, we free memory for big images.
            myBitmap.recycle();
            //return the rotated image
            return bmRotated;
        } catch (OutOfMemoryError e) {
            //if we have memory leak/error, we need to know about it....
            e.printStackTrace();
            return null;
        }
    }


    private void showImageDialog(int pos,boolean autoRotate) {

        View vvv = LayoutInflater.from(context).inflate(R.layout.dialog_img, null);
        imgPictureTmp = vvv.findViewById(R.id.imgViewDialog);
        // get bitmap from its source file // by accessing files_array's position //
        final Bitmap bitmap = BitmapFactory.decodeFile(sdDirFiles[pos].getAbsolutePath());
        if(bitmap!=null) {
            imgPictureTmp.setImageBitmap(autoRotate?
                    rotateImage(bitmap, getImageOrientation(sdDirFiles[pos].getAbsolutePath()))
                    :bitmap);
        }
        showImageDialog_Common(vvv,bitmap);
    }

    private void showImageDialog_Common(View vvv,final Bitmap bitmap) {
        final Dialog dialogImg = new Dialog(context);
        dialogImg.setContentView(vvv);
        dialogImg.setCanceledOnTouchOutside(false);
        vvv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImg.dismiss();
                if(!bitmap.isRecycled()) bitmap.recycle(); // to free up memory
            }
        });

        dialogImg.show();
    }

    private void showImageDialog(final Bitmap bitmap,boolean autoRotate) {
        View vvv = LayoutInflater.from(context).inflate(R.layout.dialog_img, null);
        imgPictureTmp = vvv.findViewById(R.id.imgViewDialog);
        if(bitmap!=null) {
            imgPictureTmp.setImageBitmap(autoRotate?
                    rotateImage(bitmap, getImageOrientation(fileName))
                    :bitmap);
        }
        showImageDialog_Common(vvv,bitmap);
    }

    private void showImgDeleteDialog(final int pos) {
        AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle("Delete Image!")
                .setIcon(R.drawable.icon_alert)
                .setMessage("Are you sure ?!")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(sdDirFiles[pos].delete()){
                            Toast.makeText(context, "Image Deletion Successful", Toast.LENGTH_SHORT).show();
                        }
                        refreshAlbum();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("NO!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void checkForPermission() {
        //we create a list of permission to ask, so we can add more later on.
        List<String> listPermissionNeeded = new ArrayList<>();
        int cameraPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int writePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);

        //check if we have permission
        if (cameraPerm != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(Manifest.permission.CAMERA);
        if (writePerm != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (readPerm != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        // in case missing a permission or two .... //
        if (!listPermissionNeeded.isEmpty()) {
            //send request for permission to the user
            //it will be in alert dialog, that the android will present.
            //we don't need to build one!!!
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),
                    CAPTURE_IMAGE_PERMISSION_REQUEST_CODE);
        } else { // all permissions are OK & GRANTED
            havePermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //checking if we got a permission result
        if(grantResults.length>0) {
            int i=0;
            while(i<grantResults.length && grantResults[i]==PackageManager.PERMISSION_GRANTED) {
                i++;
            }
            // if all are GRANTED
            if(i==grantResults.length) {havePermission=true; return;}
        }
        // if we reached here, then either no permissions were given, or some were denied! //
        Toast.makeText(context, "Cannot take photo without permissions", Toast.LENGTH_SHORT).show();

    }

    private void setPointers() {
        this.context = this;
        havePermission=Build.VERSION.SDK_INT<Build.VERSION_CODES.M; // nice trick for older android APIs //
        gridAlbum = findViewById(R.id.gridAlbum);
        btnBack = findViewById(R.id.btnBackInCamera);
        btnShoot = findViewById(R.id.btnCameraShoot);
        checkAutoRotate = findViewById(R.id.checkAutoRotate);
        txtLoading = findViewById(R.id.txtLoadingPhotos);
        progressBar = findViewById(R.id.progressBar);

        // default value, in case no photo came back // or error //
        imgPictureTmp = findViewById(R.id.imgViewDialog);

        // animations
        entryLeft = AnimationUtils.loadAnimation(context,R.anim.entry_from_left);
        entryRight = AnimationUtils.loadAnimation(context,R.anim.entry_from_right);
        entryTop = AnimationUtils.loadAnimation(context,R.anim.entry_from_top);
        entryBottom = AnimationUtils.loadAnimation(context,R.anim.entry_from_bottom);

        // photo-album
        directoryName = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) +
                File.separator + "CVWG";
        myAlbumPhotos = new Vector<>();
        myAdapter = new CameraAlbumAdapter(context,myAlbumPhotos); // create new adapter
        gridAlbum.setAdapter(myAdapter); // connect photoList to gridView with adapter
    }

    @Override  public void onAnimationStart(Animation animation) { }
    @Override  public void onAnimationEnd(Animation animation) { }
    @Override  public void onAnimationRepeat(Animation animation) { }
}

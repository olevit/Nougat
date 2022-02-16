package com.example.nougat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int START_CAMERA_APP = 2;
    private static final int REQUEST_EXTERNAL_STORAGE_RESULT = 3;
    //private Uri mOutputFileUri;
    private Uri mImageUri;
    private String mImageFileLocation = "";

    private ImageView mPhotoImageView;
    private TextView mInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhotoImageView = findViewById(R.id.imageView);
        mInfoTextView = findViewById(R.id.textView);
    }
    public void onClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                callCameraApp();
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(getApplicationContext(),
                            "Требуется разрешение на запись", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_RESULT);
            }
        } else {
            callCameraApp();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_CAMERA_APP && resultCode == RESULT_OK) {
            mPhotoImageView.setImageURI(mImageUri);
            mInfoTextView.setText(mImageUri.toString() + "\n"
                    + mImageFileLocation);
        }
        else if (requestCode == 4 && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            mPhotoImageView.setImageURI(selectedImageUri);
            mInfoTextView.setText(selectedImageUri.toString());
        }
    }
    private void callCameraApp() {
        Intent cameraAppIntent = new Intent();
        cameraAppIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String authorities = getApplicationContext().getPackageName() + ".fileprovider";
        mImageUri = FileProvider.getUriForFile(this, authorities, photoFile);
        Log.d("Provider", mImageUri.toString());
        //mOutputFileUri = Uri.fromFile(photoFile);
        //cameraAppIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
        cameraAppIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(cameraAppIntent, START_CAMERA_APP);
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        //this code works with path"."
       /*File storageDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);*/
       //this code works with path "Android/data/com.example.nougat/files/Pictures"
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDirectory);
        mImageFileLocation = imageFile.getAbsolutePath();

        return imageFile;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callCameraApp();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Нет разрешения на запись, фото не сохранено", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onClick1(View view) {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        // мы будем обрабатывать возвращенное значение в onActivityResult
        startActivityForResult(
                Intent.createChooser(pickIntent, "Выберите картинку"),
                4);
    }
}
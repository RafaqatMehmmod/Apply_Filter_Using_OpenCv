package com.example.signaturedetector;


import static com.example.signaturedetector.Constant.MY_TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.example.signaturedetector.databinding.ActivityMainBinding;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Bitmap bitmap;
    ActivityMainBinding binding;
    Mat mat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (OpenCVLoader.initDebug())
        {
            Log.i(MY_TAG, "LOADED: ");
        }
        else
        {
            Log.i(MY_TAG, "EDDROR: ");
        }


        binding.select.setOnClickListener(view -> {
            //Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            picImage.launch("image/*");

        });
        binding.camera.setOnClickListener(view -> {
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //mGetContent.launch("image/*");
            picCamera.launch(intent);

        });
    }

    //Way 1
    ActivityResultLauncher<String> picImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    Log.i(MY_TAG, "onActivityResult: "+uri);
                    try {
                        bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

                        //1st Initialize Mat
                        mat=new Mat();
                        //Convert Bitmap to mat
                        Utils.bitmapToMat(bitmap,mat);

                        //Convert mat to grace scale
                        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
                        //Convert mat to bitmap
                        Utils.matToBitmap(mat,bitmap);

                        //Again Set
                        binding.image.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

    //way 2
    ActivityResultLauncher<Intent> picCamera=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode()==RESULT_OK)
            {
                Intent data=result.getData();
                Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");
                //1st Initialize Mat
                mat=new Mat();
                //Convert Bitmap to mat
                Utils.bitmapToMat(bitmap1,mat);

                //Convert mat to grace scale
                Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2HLS_FULL);
                //Convert mat to bitmap
                Utils.matToBitmap(mat,bitmap1);

                //Again Set
                binding.image.setImageBitmap(bitmap1);



            }
        }
    });
}
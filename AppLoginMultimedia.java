package com.example.jaroga.applogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class AppLoginMultimedia extends AppCompatActivity {


    Button carama;
    Button play_pause;

    Button video;

    MediaPlayer mp;
    ImageView ImageView;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login_multimedia);

        play_pause = (Button) findViewById(R.id.btnplay);
        mp = MediaPlayer.create(this, R.raw.musica);
        carama=(Button) findViewById(R.id.btncamara);
        video=(Button)findViewById(R.id.btnvideo);

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    mp.pause();
                    play_pause.setBackgroundResource(R.drawable.play);
                    Toast.makeText(AppLoginMultimedia.this, "Pausa", Toast.LENGTH_SHORT);

                } else {
                    mp.start();
                    play_pause.setBackgroundResource(R.drawable.pausa);
                    Toast.makeText(AppLoginMultimedia.this, "Play", Toast.LENGTH_SHORT);


                }

            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirVideo();
            }
        });


        carama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrircamara();
            }
        });
    }






    public void abrirVideo(){
        path = Environment.getExternalStorageDirectory()+ File.separator;
        File video = new File(path);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(video));
        startActivityForResult(intent, 21);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED

                &&
                ActivityCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED



        ) //cierra if


        {
            //cuerpo del if

            //llamar a los permisos

            ActivityCompat.requestPermissions(
                    AppLoginMultimedia.this,
                    new  String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);



        }
    }

    public void abrircamara(){







        Intent intent1= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent1, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //guardar imagrn en la memoria
        // Super: ejecuta las versiones de mrtodos de la super clase que esta tomando la herencia
        switch (requestCode)
        {
            case 2:
                // aqui guardo mi imagen
                // el 2 significa el numero de requerimientos que manda el starActivityForResult
                // varieble que almacena l respuesta de la toma de fotografia




                Date date = new Date();

                Bitmap picture =(Bitmap)data.getExtras().get("data");
                ByteArrayOutputStream  arrayOutputStream = new ByteArrayOutputStream();
                picture.compress(Bitmap.CompressFormat.PNG, 0, arrayOutputStream );

                File file  = new File (Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_PICTURES), "nombre"
                        + date.getTime()+
                        date.getHours()
                        +date.getMinutes()
                        + date.getSeconds()
                        + ".png");

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);

                    fileOutputStream.write(arrayOutputStream.toByteArray());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                super.onActivityResult(requestCode, resultCode, data);



                break;
        }


    }
}




package com.example.jaroga.applogin;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import modelosdatos.Upload;

public class Estorage extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST=1;

    Button chooseFile,upload,showImages;
    EditText txtfileName;
    ImageView img;
    ProgressBar progressBar;


    //variable Uri para sockets de comunicacion
    private Uri mImageUri;

    //firebase storage variable
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    //hilo secundario para realizar conexiones
    private StorageTask mUploadTask;







    //on create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estorage);

        chooseFile=findViewById(R.id.btnChooseFile);
        upload=findViewById(R.id.btnUpload);
        showImages=findViewById(R.id.btnShowImages);
        txtfileName=findViewById(R.id.txtFileName);
        img=findViewById(R.id.imgView);
        progressBar=findViewById(R.id.progressBar);

        chooseFile.setOnClickListener(this);
        upload.setOnClickListener(this);
        showImages.setOnClickListener(this);

        //storage instance
        mStorageRef= FirebaseStorage.getInstance().getReference("uploads");

        //databse instance
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");

    }

    //on click
    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.btnChooseFile:

                seleccionarImagen();

                break;

            case R.id.btnUpload:

                if(mUploadTask!=null && mUploadTask.isInProgress()) {

                    //toast para avisar que ya se esta subiendo un archivo
                    Toast.makeText(getApplicationContext(), getString(R.string.msgInProgress),
                            Toast.LENGTH_SHORT).show();

                }else{

                    //se sube el archivo
                    subirArchivo();

                }

                break;


            case R.id.btnShowImages:

                //show images

                break;

        }

    }

    private void subirArchivo() {

        //validar que si tengmaos imagen cargada
        if(mImageUri!=null){

            //subimos archivo
            final StorageReference fileReference=
                    mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));

            //abrir conexion
            mUploadTask= fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //abrir hilo
                    Handler handler= new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setProgress(0);

                        }
                    },5000);

                    Toast.makeText(getApplicationContext(), getString(R.string.msgSuccess),
                            Toast.LENGTH_SHORT).show();

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            /*sumamos nuestro modelo de datos para crear la estructura que subiremos
                            a firebase dentro de la base de datos*/
                            Upload upload= new Upload(txtfileName.getText().toString().trim(),
                                    uri.toString());

                            String uploadId= mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    //obtener cantidad de progress
                    int p= (int) (100*(taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));

                    //asignar a progress bar
                    progressBar.setProgress(p);

                }
            });

        }else{

            //toast de que no hay imagen cargada
            Toast.makeText(getApplicationContext(),getString(R.string.msgNSelected),
                    Toast.LENGTH_SHORT).show();

        }

    }

    //get file extension
    private String getFileExtension(Uri mImageUri) {

        ContentResolver cR=getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(mImageUri));

    }

    //seleccionar imagen
    private void seleccionarImagen(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    //on activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST &&
                resultCode== RESULT_OK &&
                data!=null &&
                data.getData()!=null){

            //consultamos la informacion que regresa el chooser de android
            mImageUri=data.getData();

            //previusualizacion de la imagen
            img.setImageURI(mImageUri);

        }

    }
}
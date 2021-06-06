package com.example.proyecto_final;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ImageView imageview;
    Button cameraButton;
    Button galleryButton;
    Button guardarButton;
    EditText nombre;
    Bitmap captureImage;
    String currentPath;
    String URL_SERVER = "https://pruebasoo.000webhostapp.com/Backend_Mobile/Alumnos/insertAlumno.php";
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageview=findViewById(R.id.image_View1);
        cameraButton=findViewById(R.id.image_Button);
        galleryButton=findViewById(R.id.gallery_Button);
        guardarButton=findViewById(R.id.guardar_Button);
        nombre=findViewById(R.id.EditText);
        FloatingActionButton btnList = (FloatingActionButton)findViewById(R.id.btnList);
        //Guardar datos
        guardarButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String Name = nombre.getText().toString();
                Bitmap bm= ((BitmapDrawable)imageview.getDrawable()).getBitmap();
                String imagen =getBase64String(bm);
                new AsyncTaskServer().execute(Name, imagen);

            }
        });
        //Ver Alumnos creados
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(MainActivity.this, ConsultaActivity.class);
                startActivity(i);
            }
        });

        // pedir permiso de la camara
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.CAMERA
            },100);
        }
        // Abrir galeria
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent.createChooser(intent,"seleccione la Aplicación"),10);
            }
        });
        // Abrir Camara
        cameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //abrir camara
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try{
                    File photoFile =createImageFile();
                    if(photoFile!=null) {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),"com.example.proyecto_final.fileprovider",photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                        startActivityForResult(intent, 100);
                    }
                }
                catch (Exception ex){}
            }
        });
    }

    private String getBase64String(Bitmap captureImage) {
        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        captureImage.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte [] imageBytes=baos.toByteArray();
        String base64String= Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return base64String;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //capturar imagen
        if(requestCode ==100){
            try {
                File file = new File(currentPath);
                captureImage= MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),Uri.fromFile(file));
                // introducir imagen capturada en ImageView
                imageview.setImageBitmap(captureImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Cargar imagen de galería
        if(requestCode==10){
            Uri path=data.getData();
            imageview.setImageURI(path);
        }
    }
    private File createImageFile() throws  Exception{
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName="JPEG_" +timestamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image =File.createTempFile(imageFileName,".jpg",storageDir);
        currentPath=image.getAbsolutePath();
        return image;
    }
    private void openAlert(String title, String msj) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(msj);
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alert.create();
        alert.show();
    }
    private void openLoading(String msj){
        loading = new ProgressDialog(this);
        loading.setMessage(msj);
        loading.setCancelable(false);
        loading.show();
    }

    private void closeLoading(){
        loading.dismiss();
    }
    class AsyncTaskServer extends AsyncTask <String, String, String>{
        protected void onPreExecute(){
            openLoading("Guardando...");
        }

        protected String doInBackground(String... params) {

            try {

                JSONObject parameter = new JSONObject();
                parameter.put("Nombre", params[0]);
                parameter.put("Image", params[1]);

                URL url = new URL(URL_SERVER);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setDoOutput(true);
                con.setDoInput(true);
                con.setReadTimeout(10000);
                con.setConnectTimeout(15000);
                con.setRequestMethod("POST");

                //Cabeceras
                con.setRequestProperty("Content-Type", "application/json;");
                con.setRequestProperty("Accept", "application/json;");
                con.setChunkedStreamingMode(0);

                String response = "";

                //Para poder enviar los parametros al servidor
                try (OutputStream out = con.getOutputStream()) {
                    out.write(parameter.toString().getBytes());
                    out.flush();
                }

                //Para poder recibir la contestacion del servidor
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String line;
                    StringBuilder sb = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    response = sb.toString();
                }

                con.disconnect();

                Thread.sleep(2000);

                return response;

            } catch (Exception ex) {
                return ex.getMessage();
            }
        }

        protected void onPostExecute(String result) {

            closeLoading();

            try {

                JSONObject obj = new JSONObject(result);

                if (obj.getInt("estado") == 1) {
                    openAlert("Excelente", "Se guardo exitosamente!!!");
                } else {
                    openAlert("Lo sentimos!", obj.getString("mensaje"));
                }

            } catch (Exception ex) {

            }
        }
    }
}
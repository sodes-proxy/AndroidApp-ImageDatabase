package com.example.proyecto_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConsultaActivity extends AppCompatActivity {
    String URL_SERVER = "https://pruebasoo.000webhostapp.com/Backend_Mobile/Alumnos/getAlumnos.php";
    ProgressDialog loading;
    TableLayout table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);
        new AsyncTaskServer().execute();
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

    private void openAlert(String title, String msj){
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

    class AsyncTaskServer extends AsyncTask<String, String, String> {

        protected void onPreExecute(){
            openLoading("Guardando...");
        }

        protected String doInBackground(String... params) {

            try {

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

        protected void onPostExecute(String result){

            closeLoading();

            try{

                JSONObject obj = new JSONObject(result);

                if(obj.getInt("estado") == 1){
                    JSONArray data = obj.getJSONArray("data");

                    //Construir el Table
                    createCardView(data);
                }

            }catch (Exception ex){

            }

        }

    }

    /*private void createDetail(JSONArray detail) throws Exception {
        for(int i = 0; i < detail.length(); i++){
            TableRow row = new TableRow(this);
            table.addView(row);

            JSONObject obj = detail.getJSONObject(i);

            Iterator<String> iterator = obj.keys();

            while (iterator.hasNext()){
                String key = iterator.next();
                String value = obj.getString(key);

                TextView text = new TextView(this);
                text.setText(value);
                text.setTextColor(getColor(R.color.teal_700));
                text.setGravity(Gravity.CENTER);
                row.addView(text);
            }
        }
    }*/

    private void createCardView(JSONArray data) throws Exception{
        List<DataAlumno> listAlumn=new ArrayList<>();
        for(int i=0;i<data.length();i++){
            JSONObject obj =data.getJSONObject(i);
            DataAlumno alumno= new DataAlumno();
            alumno.NameAlumno=obj.getString("Nombre");
            alumno.ImageAlumno=obj.getString("archivo");
            listAlumn.add(alumno);
        }
        RecyclerView recyclerView =(RecyclerView)findViewById(R.id.alumnosList);
        AdapterAlumno mAdapter= new AdapterAlumno(ConsultaActivity.this,listAlumn);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ConsultaActivity.this));
    }
}
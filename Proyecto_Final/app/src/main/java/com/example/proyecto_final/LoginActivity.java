package com.example.proyecto_final;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Map<String,String> Users= new HashMap<String,String>();
    Button continueButton;
    EditText User;
    EditText password;
    String URL_SERVER = "https://pruebasoo.000webhostapp.com/Backend_Mobile/Clientes/getClients.php";
    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        continueButton=findViewById(R.id.continueButton);
        User=findViewById(R.id.userEdit);
        password=findViewById(R.id.passwordEdit);
        new AsyncTaskServer().execute();
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                if(Users.containsKey(password.getText().toString())){
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                }
                else{
                    openAlert("Error","El usuario o contraseña son incorrectos");
                    User.setText("");
                    password.setText("");
                }}catch (Exception ex){}
            }
        });
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

                    fillMap(data);

                }

            }catch (Exception ex){

            }

        }

    }
    private void fillMap(JSONArray data) throws Exception{
        for(int i=0;i<data.length();i++){
            JSONObject obj =data.getJSONObject(i);
            Users.put(obj.getString("password"),obj.getString("Nombre"));
        }
    }
}
package com.example.proyecto_final;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

public class AdapterAlumno extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List <DataAlumno> data= Collections.emptyList();

    public AdapterAlumno(Context context, List<DataAlumno> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.container_alumno, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder= (MyHolder) holder;
        DataAlumno current = data.get(position);
        myHolder.textName.setText(current.NameAlumno);
        // load image into imageview using glide
        Glide.with(context).load(
                "https://pruebasoo.000webhostapp.com/Backend_Mobile/Imagenes" +
                        "/" + current.ImageAlumno).into(myHolder.imageAlumno);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView textName;
        ImageView imageAlumno;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textName= (TextView) itemView.findViewById(R.id.txtName);
            imageAlumno= (ImageView) itemView.findViewById(R.id.imgAlumno);
        }

    }
}

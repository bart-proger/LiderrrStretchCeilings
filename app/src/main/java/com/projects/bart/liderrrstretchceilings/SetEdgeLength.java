package com.projects.bart.liderrrstretchceilings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SetEdgeLength extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_edge_lenght);

        Intent intent = getIntent();
        String letters = intent.getStringExtra("letters");
        float length = intent.getFloatExtra("length", 0f);


    }
}

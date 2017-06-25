package com.projects.bart.liderrrstretchceilings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetEdgeLength extends AppCompatActivity implements View.OnClickListener
{
    Button btnOk, btnCancel;
    EditText etLength;

    float length;
    String edge;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_edge_lenght);

        Intent intent = getIntent();
        edge = intent.getStringExtra("edge");
        length = intent.getFloatExtra("length", 0f);

        btnOk = (Button)findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);

        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        etLength = (EditText)findViewById(R.id.etLength);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnOk:
                Intent data = new Intent();
                data.putExtra("edge", edge);
                data.putExtra("new_length", etLength.getText().toString());
                break;

            case R.id.btnCancel:

                break;
        }
    }
}

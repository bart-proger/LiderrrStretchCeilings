package com.projects.bart.liderrrstretchceilings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetEdgeLength extends AppCompatActivity implements View.OnClickListener
{
    Button btnOk, btnCancel;
    EditText etLength;
    TextView tvEdge;

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
        etLength.setText(String.format("%.2f", length));
        etLength.selectAll();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        tvEdge = (TextView)findViewById(R.id.tvEdge);
        tvEdge.setText(edge + " = ");
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnOk:
                Intent data = new Intent();
                data.putExtra("new_length", Float.parseFloat(etLength.getText().toString()));
                setResult(RESULT_OK, data);
                finish();
                break;

            case R.id.btnCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}

package com.projects.bart.liderrrstretchceilings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    Button btnLayoutEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnLayoutEditor = (Button) findViewById(R.id.btnLayoutEditor);
        btnLayoutEditor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnLayoutEditor:
                Intent intent = new Intent(this, LayoutEditor.class);
                startActivity(intent);
        }
    }
}

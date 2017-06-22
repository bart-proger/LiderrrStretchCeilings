package com.projects.bart.liderrrstretchceilings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class LayoutEditor extends AppCompatActivity implements View.OnClickListener
{
    LinearLayout llLayoutView;
    Button btnNew, btnSave;
    LayoutView layoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_editor);

        btnNew = (Button)findViewById(R.id.btnNew);
        btnNew.setOnClickListener(this);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        llLayoutView = (LinearLayout)findViewById(R.id.llLayoutView);
        layoutView = new LayoutView(this);
        llLayoutView.addView(layoutView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnNew:
                layoutView.newLayout();
                break;
            case R.id.btnSave:
                // TODO: сохранить чертеж
                break;
        }
    }
}

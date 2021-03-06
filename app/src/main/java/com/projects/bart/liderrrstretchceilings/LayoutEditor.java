package com.projects.bart.liderrrstretchceilings;

import android.app.Activity;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class LayoutEditor extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
    LinearLayout llLayoutView;
    Button btnNew, btnSave, btnDeletePoint;
    LayoutView layoutView;
    CheckBox cb90;
    ToggleButton tbInsertPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_editor);

        btnNew = (Button)findViewById(R.id.btnNew);
        btnNew.setOnClickListener(this);

        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        cb90 = (CheckBox)findViewById(R.id.cb90);
        cb90.setOnClickListener(this);

        tbInsertPoint = (ToggleButton)findViewById(R.id.tbInsertPoint);
        tbInsertPoint.setOnCheckedChangeListener(this);

        btnDeletePoint = (Button)findViewById(R.id.btnDeletePoint);
        btnDeletePoint.setOnClickListener(this);

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
            case R.id.cb90:
                layoutView.setStraight(cb90.isChecked());
                break;
            case R.id.btnDeletePoint:
                layoutView.deleteSelectedPoints();
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId())
        {
            case R.id.tbInsertPoint:
                if (isChecked)
                    tbInsertPoint.setChecked(layoutView.changeEditMode(LayoutView.EditMode.InsertPoint));
                else
                    layoutView.changeEditMode(LayoutView.EditMode.SelectAndMove);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case LayoutView.RC_SET_EDGE_LENGTH:
                if (resultCode == RESULT_OK)
                {
                    float newLength = data.getFloatExtra("new_length", 0.f);
                    layoutView.setSelectedEdgeLength(newLength);
                    Toast.makeText(this, "new_length=" + String.valueOf(newLength), Toast.LENGTH_LONG);
                }
                break;
            default:
                Toast.makeText(this, "Warning: Undefined requestCode!", Toast.LENGTH_LONG);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
    {
        super.onSaveInstanceState(outState, outPersistentState);

        //TODO: сохранять состояние layoutView при повороте экрана
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        //TODO: загрузить состояние
    }
}

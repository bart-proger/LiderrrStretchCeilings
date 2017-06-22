package com.projects.bart.liderrrstretchceilings;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by BART on 21.06.2017.
 */

public class LayoutView extends View implements View.OnTouchListener
{
    private enum DrawStyle { Normal/*, Selected, Error*/ }
    private enum EditMode { New, Select }

    EditMode editMode = EditMode.New;
    boolean straight = false;

    Canvas g;
    Paint normalPaint, fillPaint, linePaint;

    PointF touch;

    final float POINT_SIZE = 12.0f;
    final float SELECT_DISTANCE = 12.0f;


    ArrayList<PointF> points;


    public LayoutView(Context context)
    {
        super(context);
        setOnTouchListener(this);

        normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        normalPaint.setColor(Color.rgb(0, 127, 0)); //темно-зеленый

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3.0f);
        linePaint.setColor(Color.BLACK);

        points = new ArrayList<PointF>();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        g = canvas;

        g.drawColor(Color.GRAY);
        g.save();
        g.scale(Resources.getSystem().getDisplayMetrics().density, Resources.getSystem().getDisplayMetrics().density);


        if (points.size() > 1)
        {
            Path p = new Path();
            p.moveTo(points.get(0).x, points.get(0).y);
            for (int i = 1; i < points.size(); i++)
            {
                p.lineTo(points.get(i).x, points.get(i).y);
            }
            //TODO: рисовать замыкающую сторону
            if (editMode != EditMode.New)
            {
                p.lineTo(points.get(0).x, points.get(0).y);
                canvas.drawPath(p, fillPaint);
            }
            canvas.drawPath(p, linePaint);
            p.close();
        }



        for (int i = 0; i < points.size(); i++)
        {
            drawVertex(points.get(i), DrawStyle.Normal);
        }

        g.restore();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        touch = new PointF(pxToDp(event.getX()), pxToDp(event.getY()));

        switch (editMode)
        {
            case New:
                if (event.getAction() == MotionEvent.ACTION_UP)
                    addPoint(touch);
                break;
            case Select:

                break;
            default:
                break;
        }

        return true;
    }

    public void newLayout()
    {
        points.clear();
        editMode = EditMode.New;

        onChangeLayout();
    }

    private void addPoint(PointF p)
    {
        if (points.size() > 2 && Geometry.distance(p, points.get(0)) < SELECT_DISTANCE)
        {
            editMode = EditMode.Select;
            if (straight)
            {
                PointF first = points.get(0);
                PointF last = points.get(points.size() - 1);
                PointF prelast = points.get(points.size() - 2);
                points.remove(points.size() - 1);

                if (prelast.y == last.y)
                {
                    last.x = first.x;
                } else if (prelast.x == last.x)
                {
                    last.y = first.y;
                }
                points.add(last);
            }
            // calcLengths();

            return;
        }
        if (straight && points.size() > 0)
        {
            PointF last = points.get(points.size() - 1);
            if (Math.abs(last.x - p.x) > Math.abs(last.y - p.y))
                p.y = last.y;
            else
                p.x = last.x;
        }
        points.add(p);

        onChangeLayout();
    }

    private void onChangeLayout()
    {
        invalidate();
    }

    private void drawVertex(PointF p, DrawStyle style)
    {
        switch(style)
        {
            case Normal:
                g.drawCircle(p.x, p.y, POINT_SIZE, normalPaint);
                break;
            default: //Error:

                break;
        }
    }

    public static float dpToPx(float dp)
    {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    public static float pxToDp(float px)
    {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }
}

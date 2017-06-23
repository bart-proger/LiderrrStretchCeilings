package com.projects.bart.liderrrstretchceilings;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by BART on 21.06.2017.
 */

public class LayoutView extends View implements View.OnTouchListener
{
    private enum DrawStyle { Normal/*, Selected, Error*/ }
    private enum EditMode { New, SelectAndMove}


    EditMode editMode = EditMode.New;

    boolean straight = false;
    public void setStraight(boolean straight)
    {
        this.straight = straight;
    }

    boolean moving = false,
            selecting = false;

    Paint pointPaint, fillPaint, linePaint, letterPaint, lengthPaint;

    PointF touch;
    PointF selectFrom, selectTo;

    final float POINT_SIZE = 8.0f;
    final float SELECT_DISTANCE = 12.0f;
    final float DENSITY = Resources.getSystem().getDisplayMetrics().density;


    ArrayList<PointF> points;
    ArrayList<Integer> selectedIndices;
    Path path;


    public LayoutView(Context context)
    {
        super(context);
        setOnTouchListener(this);

        touch = new PointF();
        selectFrom = new PointF();
        selectTo = new PointF();

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.rgb(0, 127, 0)); //темно-зеленый

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2.0f);
        linePaint.setColor(Color.BLACK);

        letterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        letterPaint.setColor(Color.WHITE);
        letterPaint.setSubpixelText(true);

        lengthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lengthPaint.setTextSize(lengthPaint.getTextSize() * 0.7f);
        lengthPaint.setColor(Color.BLACK);
        lengthPaint.setSubpixelText(true);

        points = new ArrayList<>();
        selectedIndices = new ArrayList<>();

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);


        canvas.drawColor(Color.LTGRAY);
        canvas.save();
        canvas.scale(DENSITY, DENSITY);

        if (points.size() > 1)
        {
            //полотно
            path.reset();
            path.moveTo(points.get(0).x, points.get(0).y);
            for (int i = 1; i < points.size(); i++)
            {
                path.lineTo(points.get(i).x, points.get(i).y);
            }
            if (editMode != EditMode.New)
            {
                path.lineTo(points.get(0).x, points.get(0).y);
                canvas.drawPath(path, fillPaint);
            }
            //стороны
            canvas.drawPath(path, linePaint);
            path.close();
        }

        for (int i = 0; i < points.size(); i++)
        {
            //длина стороны
            int j = i+1;
            if (editMode != EditMode.New || j != points.size())
            {
                j %= points.size();
                drawLength(i, j, canvas);
            }

            //вершина с литералом
            PointF p = points.get(i);
            canvas.drawCircle(p.x, p.y, POINT_SIZE, pointPaint);
            canvas.drawText(getPointLetter(i), p.x - 4, p.y + 4, letterPaint);
        }

        //TODO: рисовать область выделения и выделенные точки

        canvas.restore();
    }

    private void drawLength(int index1, int index2, Canvas canvas)
    {
        PointF p = points.get(index1),
                p2 = points.get(index2);

        float d = Geometry.distance(p, p2);
        String s = String.format("%.2f", d / 20f);
        float w = lengthPaint.measureText(s);

        if (w+20f > d)  //FIX длина перекрывает линию если сторона маленькая
            return;

        float h = lengthPaint.getTextSize();
        PointF c = new PointF((p.x + p2.x) / 2f, (p.y + p2.y) / 2f);
        RectF r = new RectF(c.x - w / 2f - 2, c.y - h / 2f - 2, c.x + w / 2f + 2, c.y + h / 2f + 2);
        canvas.drawRoundRect(r, 5f, 5f, fillPaint);
        canvas.drawText(s, r.left + 2, r.bottom - 2, lengthPaint);
    }

    private String getPointLetter(int index)
    {
        index = (index + points.size()) % points.size();

        String letter = String.valueOf((char) ('A' + index));
        if ('A' + index > 'Z')
            letter += (char) ('A' + index / ('Z' - 'A'));

        return letter;
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
            case SelectAndMove:
                selectAndMoveMode(event);
                break;
            default:
                break;
        }

        invalidate();
        return true;
    }

    private void selectAndMoveMode(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN: // нажатие
                selectFrom.set(touch);
                selectTo.set(touch);

                if (isSelectedPointAt(touch) || selectPointAt(touch))
                {
                    moving = true;
                    break;
                }
                break;

            case MotionEvent.ACTION_MOVE: // движение
                if (moving)
                {
                    moveSelectedPoints(touch.x - selectFrom.x, touch.y - selectFrom.y);

                    //((EditLayout)getContext()).onClick(btnCalcS);

                    selectFrom.set(touch);
                } else
                {
                    selecting = true;
                    selectTo.set(touch);
                    selectPointsInArea(selectFrom, selectTo);
                }
                break;

            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
//                int edge;
//                if (!selecting && !moving && (edge = ceilingLayout.selectEdgeAt(touch)) > -1)
//                {
//                    Intent intent = new Intent(getContext(), actSetEdgeLength.class);
//                    intent.putExtra("letters", ceilingLayout.getPointLetter(edge) + ceilingLayout.getPointLetter(edge + 1));
//                    intent.putExtra("length", ceilingLayout.getEdgeLength(edge) / 20f);
//                    startActivityForResult(intent, SET_EDGE_LENGTH);
//                }

                selecting = false;
                moving = false;
                break;
        }
    }

    //---------------------

    private boolean selectPointAt(PointF p)
    {
        selectedIndices.clear();

        for (int i = 0; i < points.size(); i++)
        {
            if (Geometry.distance(p, points.get(i)) < SELECT_DISTANCE)
            {
                selectedIndices.add(i);
                return true;
            }
        }
        return false;
    }

    private boolean selectPointsInArea(PointF from, PointF to)
    {
        selectedIndices.clear();

        float left, right, top, bottom;
        if (from.x < to.x)
        {
            left = from.x;
            right = to.x;
        } else
        {
            left = to.x;
            right = from.x;
        }
        if (from.y < to.y)
        {
            top = from.y;
            bottom = to.y;
        } else
        {
            top = to.y;
            bottom = from.y;
        }

        for (int i = 0; i < points.size(); i++)
        {
            PointF p = points.get(i);
            if (!selectedIndices.contains(i) && p.x >= left && p.x <= right && p.y >= top && p.y <= bottom)
            {
                selectedIndices.add(i);
            }
        }
        return selectedIndices.size() > 0;
    }

    private boolean isSelectedPointAt(PointF p)
    {
        for (int i = 0; i < points.size(); i++)
        {
            if (Geometry.distance(p, points.get(i)) < SELECT_DISTANCE && selectedIndices.contains(i))
            {
                return true;
            }
        }
        return false;
    }

    private void moveSelectedPoints(float dx, float dy)
    {
        for (int i = 0; i < selectedIndices.size(); i++)
        {
            points.get(selectedIndices.get(i)).offset(dx, dy);
        }
    }

    //---------------------

    public void newLayout()
    {
        points.clear();
        editMode = EditMode.New;

//        onChangeLayout();
        invalidate();
    }

    private void addPoint(PointF p)
    {
        if (points.size() > 2 && Geometry.distance(p, points.get(0)) < SELECT_DISTANCE)
        {
            editMode = EditMode.SelectAndMove;
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
//            onChangeLayout();
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

//        onChangeLayout();
    }

//    private void onChangeLayout()
//    {
//        invalidate();
//    }

    private float dpToPx(float dp)
    {
        return dp * DENSITY;
    }

    private float pxToDp(float px)
    {
        return px / DENSITY;
    }
}

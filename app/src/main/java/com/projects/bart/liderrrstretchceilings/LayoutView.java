package com.projects.bart.liderrrstretchceilings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by BART on 21.06.2017.
 */

//TODO: zoom,

public class LayoutView extends View implements View.OnTouchListener
{
    private enum DrawStyle { Normal/*, Selected, Error*/ }
    public enum EditMode { New, SelectAndMove, InsertPoint }


    EditMode editMode = EditMode.New;

    boolean straight = false;
    public void setStraight(boolean straight)
    {
        this.straight = straight;
    }

    boolean moving = false,
            selecting = false;

    Paint pointPaint, fillPaint, linePaint, letterPaint, lengthPaint, selPointPaint,
            selAreaPaint, infoPaint;

    PointF touch;
    PointF selectFrom, selectTo;
    float left, right, top, bottom;

    final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
    final float POINT_SIZE = 10.0f;
    final float SELECT_DISTANCE = 16.0f * DENSITY;
    final float LETTER_FONT_SIZE = 14.0f;
    final float LENGTH_FONT_SIZE = 8.0f;

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
        pointPaint.setColor(Color.rgb(0, 160, 0)); //темно-зеленый

        selPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selPointPaint.setColor(Color.rgb(80, 80, 255)); //синий

        selAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selAreaPaint.setStyle(Paint.Style.FILL);
        selAreaPaint.setColor(Color.argb(127, 160, 160, 255));

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2f);
        linePaint.setColor(Color.BLACK);

        letterPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        letterPaint.setColor(Color.WHITE);
        letterPaint.setTextAlign(Paint.Align.CENTER);
        letterPaint.setTextSize(LETTER_FONT_SIZE);
        letterPaint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));

        lengthPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        lengthPaint.setTypeface(Typeface.MONOSPACE);
        lengthPaint.setTextAlign(Paint.Align.CENTER);
        lengthPaint.setTextSize(LENGTH_FONT_SIZE);
        lengthPaint.setColor(Color.BLACK);

        infoPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        infoPaint.setTypeface(Typeface.MONOSPACE);
        infoPaint.setTextSize(14);
        infoPaint.setColor(Color.BLACK);

        points = new ArrayList<>();
        selectedIndices = new ArrayList<>();

        path = new Path();
    }

    private void drawInfo(Canvas canvas)
    {
        canvas.drawText("density=" + String.valueOf(DENSITY), 10, 20, infoPaint);
        canvas.drawText("select_dist=" + String.valueOf(SELECT_DISTANCE), 10, 40, infoPaint);
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
            if (selectedIndices.contains(i))
                canvas.drawCircle(p.x, p.y, POINT_SIZE, selPointPaint); //выделенные
            else
                canvas.drawCircle(p.x, p.y, POINT_SIZE, pointPaint);

            canvas.drawText(getPointLetter(i), p.x, p.y + 6, letterPaint);
        }

        //область выделения
        if (selecting)
            canvas.drawRect(left, top, right, bottom, selAreaPaint);

        drawInfo(canvas);
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
//        RectF r = new RectF(c.x - w / 2f - 2, c.y - h / 2f - 2, c.x + w / 2f + 2, c.y + h / 2f + 2);
        RectF r = new RectF(c.x, c.y, c.x, c.y);
        r.inset(-w / 2 - 3, -h / 2 - 3);
        canvas.drawRoundRect(r, 5f, 5f, fillPaint);
        //canvas.drawText(s, r.left + 2, r.bottom - 2, lengthPaint);
        canvas.drawText(s, c.x, c.y + h / 2f - 1f, lengthPaint);
    }

    private String getPointLetter(int index)
    {
        if (!points.isEmpty())
            index = (index + points.size()) % points.size();

        int az = 'Z' - 'A' + 1;

        String letter = String.valueOf((char) ('A' + (index % az)));
        if ('A' + index > 'Z')
            letter += String.valueOf(index / az);

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

            case InsertPoint:
                if (event.getAction() == MotionEvent.ACTION_UP)
                    insertPointAt(touch);
                break;

            default:
                Toast.makeText(getContext(), "Внимание! Режим не проеделен", Toast.LENGTH_SHORT).show();
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
                int edge = -1;

                if (isSelectedPointAt(touch) || selectPointAt(touch))
                {
                    moving = true;
                }
                else if ((edge = selectEdgeAt(touch)) > -1)
                {
                    Intent intent = new Intent(getContext(), SetEdgeLength.class);
                    intent.putExtra("letters", getPointLetter(edge) + getPointLetter(edge + 1));
                    intent.putExtra("length", getEdgeLength(edge) / 20f);
                    startActivity(getContext(), intent, null);
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
                selecting = false;
                moving = false;
                break;
        }
    }

    private float getEdgeLength(int edge)
    {
        if (points.size() < 2)
            return 0;

        int i, j;
        i = (edge + points.size()) % points.size();
        j = (edge + 1 + points.size()) % points.size();

        return Geometry.distance(points.get(i), points.get(j));

    }

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

    private int selectEdgeAt(PointF p)
    {
        PointF c = new PointF();
        for (int i = 0; i < points.size(); ++i)
        {
            int j = (i+1) % points.size();
            c.set((points.get(i).x + points.get(j).x) / 2f, (points.get(i).y + points.get(j).y) / 2f);

            if (Geometry.distance(p, c) < SELECT_DISTANCE)
                return i;
        }
        return -1;
    }

    private void moveSelectedPoints(float dx, float dy)
    {
        for (int i = 0; i < selectedIndices.size(); i++)
        {
            points.get(selectedIndices.get(i)).offset(dx, dy);
        }
    }

    public void deleteSelectedPoints()
    {
        if (selectedIndices.isEmpty() || (points.size() - selectedIndices.size()) < 3)
            return;

        Collections.sort(selectedIndices, Collections.reverseOrder());
        for (int i = 0; i < selectedIndices.size(); i++)
        {
            points.remove((int)selectedIndices.get(i));
//            Log.d("ml", String.format("remove point %d", (int)selectedPoints.get(i)));
        }

        selectedIndices.clear();

        invalidate();
    }

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

    private void insertPointAt(PointF p)
    {
        for (int i = 0; i < points.size(); i++)
        {
            int j = (i + 1) % points.size();
            if (Geometry.intersectLineCircle(points.get(i), points.get(j), p, SELECT_DISTANCE))
            {
                points.add(j, Geometry.projectionPointToLine(p, points.get(i), points.get(j)));
                break;
            }
        }
    }

    public boolean changeEditMode(EditMode newMode)
    {
        if (newMode == EditMode.InsertPoint && (editMode != EditMode.SelectAndMove))
            return false;

        editMode = newMode;
        return true;
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

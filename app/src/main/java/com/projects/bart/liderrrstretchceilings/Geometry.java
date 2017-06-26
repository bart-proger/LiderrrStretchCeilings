package com.projects.bart.liderrrstretchceilings;

import android.graphics.PointF;

/**
 * Created by BART on 22.06.2017.
 */

public final class Geometry
{
    // расстояние между точками
    public static float distance(PointF a, PointF b)
    {
        return (float)Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
    }

    // сумма векторов
    public static PointF add(PointF v1, PointF v2)
    {
        return new PointF(v1.x + v2.x, v1.y + v2.y);
    }

    // разность векторов
    public static PointF sub(PointF v1, PointF v2)
    {
        return new PointF(v1.x - v2.x, v1.y - v2.y);
    }

    // умножение вектора на скаляр
    public static PointF scale(PointF v, float s)
    {
        return new PointF(v.x * s, v.y * s);
    }

    // нормализованый вектор
    public static PointF normalized(PointF v)
    {
        float l = v.length();
        return scale(v, 1f / l);
    }

    public static boolean intersectLineCircle(PointF p1,  PointF p2, PointF center, float radius) // пересечение отрезка p1p2 и окружности
    {

        float x01 = p1.x - center.x;
        float y01 = p1.y - center.y;

        float x02 = p2.x - center.x;
        float y02 = p2.y - center.y;

        float dx = x02 - x01;
        float dy = y02 - y01;

        float a = dx * dx + dy * dy;
        float b = 2f * (x01 * dx + y01 * dy);
        float c = x01 * x01 + y01 * y01 - radius * radius;

        if (-b < 0)
            return (c < 0);
        if (-b < (2f*a))
            return ((4f*a*c - b*b) < 0);

        return ((a + b + c) < 0);
    }

    public static PointF projectionPointToLine(PointF p, PointF a, PointF b) // проекция точки P на прямую AB
    {
        float t = ((p.x-a.x)*(b.x-a.x) + (p.y-a.y)*(b.y-a.y)) / ((b.x-a.x)*(b.x-a.x) + (b.y-a.y)*(b.y-a.y));
        return new PointF(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t);
    }
}

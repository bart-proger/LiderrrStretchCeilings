package com.projects.bart.liderrrstretchceilings;

import android.graphics.PointF;

/**
 * Created by BART on 22.06.2017.
 */

public final class Geometry
{
    public static float distance(PointF a, PointF b)    // расстояние между точками
    {
        return (float)Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
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

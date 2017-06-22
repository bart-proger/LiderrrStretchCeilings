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
}

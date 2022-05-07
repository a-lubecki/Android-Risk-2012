package fr.istic.androidrisk.ihm.play;

import android.R.color;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MapLabel extends Overlay {

    public static final int ETAT_AUCUN = 0;
    public static final int ETAT_SOURCE = 1;
    public static final int ETAT_DESTINATION = 2;

    private Paint paint;
    private String text;
    private final GeoPoint point;
    private int color;
    private int etat;

    public MapLabel(String text, int color, GeoPoint point) {
        paint = new Paint();
        this.color = color;
        setEtat(ETAT_AUCUN);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);
        this.text = text;
        this.point = point;
    }

    public void setColor(int color) {
        paint.setColor(color);
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public GeoPoint getGeoPoint() {
        return point;
    }

    @Override
    public void draw(Canvas canvas, MapView map, boolean shadow) {
        Projection projection = map.getProjection();
        Point p = new Point();
        projection.toPixels(point, p);
        if (etat != ETAT_AUCUN) {
            paint.setTextSize(25);
            Paint circlePaint = new Paint(paint);
            circlePaint.setAlpha(64);
            int radius = (int) (15 * Math.pow(2, map.getZoomLevel() - 6));
            if (etat==ETAT_SOURCE) {
                Paint pain = new Paint(paint);
                pain.setStyle(Paint.Style.STROKE);
                pain.setStrokeWidth(5);
                canvas.drawCircle(p.x, p.y, radius, pain);
            }
            canvas.drawCircle(p.x, p.y, radius, circlePaint);
            Paint textPaint = new Paint(paint);
            textPaint.setColor(Color.BLACK);
            canvas.drawText(text, p.x, p.y + paint.getTextSize() * 1 / 3,
                    textPaint);
        } else {
            paint.setTextSize(20);
            canvas.drawText(text, p.x, p.y + paint.getTextSize() * 1 / 3, paint);
        }
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }
    
}

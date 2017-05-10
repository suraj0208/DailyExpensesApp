package com.suraj.dailyexpenses;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PieChartActivity extends AppCompatActivity {
    private Paint paint1 = new Paint();
    private Paint paint2 = new Paint();
    private Paint paint3 = new Paint();
    private Paint paint4 = new Paint();

    static double angles[] = new double[4];

    private float BRUSH_WIDTH = 10f;
    private float RAD_DECREMENT = 1f;

    Paint.Style STYLE = Paint.Style.STROKE;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        // final ImageView pieChartView = (ImageView) findViewById(R.id.pieChartView);

        final int w = 250, h = 250;
/*
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                paint1.setColor(Color.BLACK);
                paint1.setStrokeWidth(BRUSH_WIDTH);
                paint1.setStyle(STYLE);

                paint2.setColor(Color.CYAN);
                paint2.setStrokeWidth(BRUSH_WIDTH);
                paint2.setStyle(STYLE);

                paint3.setColor(Color.MAGENTA);
                paint3.setStrokeWidth(BRUSH_WIDTH);
                paint3.setStyle(STYLE);

                paint4.setColor(Color.RED);
                paint4.setStrokeWidth(BRUSH_WIDTH);
                paint4.setStyle(STYLE);

                paint1.setAntiAlias(true);
                paint1.setFilterBitmap(true);
                paint1.setDither(true);

                paint2.setAntiAlias(true);
                paint2.setFilterBitmap(true);
                paint2.setDither(true);

                paint3.setAntiAlias(true);
                paint3.setFilterBitmap(true);
                paint3.setDither(true);

                paint4.setAntiAlias(true);
                paint4.setFilterBitmap(true);
                paint4.setDither(true);

                bitmap = drawOnCanvas(w / 2, h / 2);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                pieChartView.setImageBitmap(bitmap);
                System.out.println("set");
            }
        }).execute();*/

    }

    public Bitmap drawOnCanvas(int p, int q) {
        float r = 125 - 10;

        for (int i = 1; i < 4; i++)
            angles[i] += angles[i - 1];


        Bitmap output = Bitmap.createBitmap(p * 2, q * 2, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        int q1t1;
        int q1t2;
        int q1t3;
        int q1t4;

        int q2t1;
        int q2t2;
        int q2t3;
        int q2t4;

        int q3t1;
        int q3t2;
        int q3t3;
        int q3t4;

        int q4t1;
        int q4t2;
        int q4t3;
        int q4t4;

        while (((int) r) >= 0) {
            int x = (int) r;
            int y = 0;
            int err = 0;

            while (x >= y) {
                q1t1 = p + x;
                q1t2 = q + y;
                q1t3 = p + y;
                q1t4 = q + x;

                drawPoint(canvas, q1t3, q1t4, 0);
                drawPoint(canvas, q1t1, q1t2, 0);

                q2t1 = p - y;
                q2t2 = q + x;
                q2t3 = p - x;
                q2t4 = q + y;

                drawPoint(canvas, q2t1, q2t2, 90);
                drawPoint(canvas, q2t3, q2t4, 90);

                q3t1 = p - x;
                q3t2 = q - y;
                q3t3 = p - y;
                q3t4 = q - x;

                drawPoint(canvas, q3t1, q3t2, 360);
                drawPoint(canvas, q3t3, q3t4, 360);

                q4t1 = p + y;
                q4t2 = q - x;
                q4t3 = p + x;
                q4t4 = q - y;

                drawPoint(canvas, q4t1, q4t2, 360);
                drawPoint(canvas, q4t3, q4t4, 360);

                if (err <= 0) {
                    y += 1;

                    err += 2 * y + 1;
                }
                if (err > 0) {
                    x -= 1;
                    err -= 2 * x + 1;
                }
            }

            r -= RAD_DECREMENT;
        }

        return output;
    }

    private void drawPoint(Canvas canvas, float x, float y, double base) {
        double tan;

        tan = Math.toDegrees(Math.atan2(y - (canvas.getHeight() / 2), x - (canvas.getWidth() / 2)));

        if (base > 180)
            tan += base;

        if (tan <= angles[0]) {
            canvas.drawPoint(x, y, paint1);
        } else if (tan <= angles[1]) {
            canvas.drawPoint(x, y, paint2);
        } else if (tan <= angles[2]) {
            canvas.drawPoint(x, y, paint3);
        } else if (tan > angles[2] && tan <= 360) {
            canvas.drawPoint(x, y, paint4);
        }
    }


}

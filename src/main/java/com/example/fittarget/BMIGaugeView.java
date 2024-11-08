package com.example.fittarget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class BMIGaugeView extends View {

    private Paint underweightPaint, normalPaint, overweightPaint, obesePaint, indicatorPaint, textPaint;
    private float bmiValue = 0;

    public BMIGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        underweightPaint = createPaint("#FFEA00", 100);
        normalPaint = createPaint("#FFBF00", 100);
        overweightPaint = createPaint("#CD7F32", 100);
        obesePaint = createPaint("#5D3A00", 100);

        indicatorPaint = new Paint();
        indicatorPaint.setColor(Color.BLACK);
        indicatorPaint.setStrokeWidth(8);
        indicatorPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private Paint createPaint(String color, float width) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int radius = Math.min(width, height) / 2 - 50;
        int startAngle = 180;

        drawArcSegment(canvas, startAngle, 45, underweightPaint);
        drawArcSegment(canvas, startAngle + 45, 45, normalPaint);
        drawArcSegment(canvas, startAngle + 90, 45, overweightPaint);
        drawArcSegment(canvas, startAngle + 135, 45, obesePaint);

        drawBMIValue(canvas, width / 2, height / 2 + 150);
        drawBMIIndicator(canvas, width / 2, height / 2 + 100, radius);

        // Draw the range labels to the left of the BMI value
        drawBMIRanges(canvas, width / 2 -450, height / 2 +250); // Adjust position as needed
        drawLabels(canvas, width / 2, height / 2, radius);
    }

    private void drawBMIValue(Canvas canvas, float x, float y) {
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#FFFFE0")); // Light yellow color
        backgroundPaint.setStyle(Paint.Style.FILL);

        Paint bmiTextPaint = new Paint();
        bmiTextPaint.setColor(Color.BLACK);
        bmiTextPaint.setTextSize(100);
        bmiTextPaint.setTextAlign(Paint.Align.CENTER);
        bmiTextPaint.setFakeBoldText(true); // Set text to bold

        float padding = 10; // Padding around the text
        float textWidth = bmiTextPaint.measureText("BMI: " + String.format("%.1f", bmiValue));
        float textHeight = bmiTextPaint.getTextSize();

        canvas.drawRect(x - textWidth / 2 - padding, y - textHeight, x + textWidth / 2 + padding, y + padding, backgroundPaint);

        String bmiText = "BMI: " + String.format("%.1f", bmiValue);
        canvas.drawText(bmiText, x, y, bmiTextPaint);
    }

    private void drawArcSegment(Canvas canvas, int startAngle, int sweepAngle, Paint paint) {
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 50;
        canvas.drawArc(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius,
                startAngle, sweepAngle, false, paint);
    }

    private float calculateIndicatorAngle(float bmi) {
        if (bmi < 18.5) {
            return 202.5f; // Angle for Underweight
        } else if (bmi < 25) {
            return 247.5f; // Angle for Normal
        } else if (bmi < 30) {
            return 292.5f; // Angle for Overweight
        } else {
            return 337.5f; // Angle for Obese
        }
    }

    private void drawBMIIndicator(Canvas canvas, int cx, int cy, int radius) {
        float angle = calculateIndicatorAngle(bmiValue);
        drawIndicator(canvas, cx, cy, radius, angle);
    }

    private void drawIndicator(Canvas canvas, int cx, int cy, int radius, float angle) {
        Paint indicatorPaint = new Paint();
        indicatorPaint.setStrokeWidth(8);
        indicatorPaint.setStyle(Paint.Style.STROKE);

        if (bmiValue < 18.5) {
            indicatorPaint.setColor(Color.parseColor("#FFEA00")); // Underweight color
        } else if (bmiValue < 25) {
            indicatorPaint.setColor(Color.parseColor("#FFBF00")); // Normal color
        } else if (bmiValue < 30) {
            indicatorPaint.setColor(Color.parseColor("#CD7F32")); // Overweight color
        } else {
            indicatorPaint.setColor(Color.parseColor("#5D3A00")); // Obese color
        }

        float xStart = cx;
        float yStart = cy - 40;

        double radians = Math.toRadians(angle);
        float xEnd = (float) (cx + radius * Math.cos(radians));
        float yEnd = (float) (cy + radius * Math.sin(radians));

        canvas.drawLine(xStart, yStart, xEnd, yEnd, indicatorPaint);
        drawArrowhead(canvas, xEnd, yEnd, angle, indicatorPaint.getColor());
    }

    private void drawArrowhead(Canvas canvas, float x, float y, float angle, int color) {
        Paint arrowPaint = new Paint();
        arrowPaint.setColor(color);
        arrowPaint.setStyle(Paint.Style.FILL);

        float arrowSize = 50;

        double radians = Math.toRadians(angle);
        float xLeft = (float) (x - arrowSize * Math.cos(radians - Math.PI / 6));
        float yLeft = (float) (y - arrowSize * Math.sin(radians - Math.PI / 6));

        float xRight = (float) (x - arrowSize * Math.cos(radians + Math.PI / 6));
        float yRight = (float) (y - arrowSize * Math.sin(radians + Math.PI / 6));

        Path arrowPath = new Path();
        arrowPath.moveTo(x, y);
        arrowPath.lineTo(xLeft, yLeft);
        arrowPath.lineTo(xRight, yRight);
        arrowPath.close();

        canvas.drawPath(arrowPath, arrowPaint);
    }

    private void drawLabels(Canvas canvas, int cx, int cy, int radius) {
        drawAlignedText(canvas, "Underweight", cx, cy, radius, 202.5f);
        drawAlignedText(canvas, "Normal", cx, cy, radius, 247.5f);
        drawAlignedText(canvas, "Overweight", cx, cy, radius, 292.5f);
        drawAlignedText(canvas, "Obese", cx, cy, radius, 337.5f);
    }

    private void drawBMIRanges(Canvas canvas, float x, float y) {
        Paint rangeTextPaint = new Paint();
        rangeTextPaint.setTextSize(50);
        rangeTextPaint.setTextAlign(Paint.Align.LEFT);

        float yOffset = 50;

        // Draw Severe Underweight
        if (bmiValue < 16.0) {
            rangeTextPaint.setColor(Color.parseColor("#FFEA00"));

            rangeTextPaint.setFakeBoldText(true);// Severe Underweight color
        } else {
            rangeTextPaint.setColor(Color.BLACK);
        }
        canvas.drawText("Severe Underweight: <16.0", x, y, rangeTextPaint);
        y += yOffset;

        // Draw Underweight
        if (bmiValue >= 16.0 && bmiValue < 18.5) {
            rangeTextPaint.setColor(Color.parseColor("#FFEA00"));
            rangeTextPaint.setFakeBoldText(true);// Underweight color
        } else {
            rangeTextPaint.setColor(Color.BLACK);
        }
        canvas.drawText("Underweight: 16.0 - 18.5", x, y, rangeTextPaint);
        y += yOffset;

        // Draw Normal
        if (bmiValue >= 18.5 && bmiValue < 25) {
            rangeTextPaint.setColor(Color.parseColor("#FFBF00"));
            rangeTextPaint.setFakeBoldText(true);// Normal color
        } else {
            rangeTextPaint.setColor(Color.BLACK);
        }
        canvas.drawText("Normal: 18.5 - 24.9", x, y, rangeTextPaint);
        y += yOffset;

        // Draw Overweight
        if (bmiValue >= 25 && bmiValue < 30) {
            rangeTextPaint.setColor(Color.parseColor("#CD7F32"));
            rangeTextPaint.setFakeBoldText(true);// Overweight color
        } else {
            rangeTextPaint.setColor(Color.BLACK);
        }
        canvas.drawText("Overweight: 25.0 - 29.9", x, y, rangeTextPaint);
        y += yOffset;

        // Draw Obese
        if (bmiValue >= 30 && bmiValue < 35) {
            rangeTextPaint.setColor(Color.parseColor("#5D3A00"));
            rangeTextPaint.setFakeBoldText(true);// Obese color
        } else {
            rangeTextPaint.setColor(Color.BLACK);
        }
        canvas.drawText("Obese: 30.0 - 34.9", x, y, rangeTextPaint);
        y += yOffset;

        // Draw Severe Obese
        if (bmiValue >= 35) {
            rangeTextPaint.setColor(Color.parseColor("#5D3A00"));

            rangeTextPaint.setFakeBoldText(true);// Severe Obese color
        } else {
            rangeTextPaint.setColor(Color.BLACK);
        }
        canvas.drawText("Severe Obese: ≥35.0", x, y, rangeTextPaint);
    }


    private void drawAlignedText(Canvas canvas, String text, int cx, int cy, int radius, float angle) {
        double radians = Math.toRadians(angle);
        float x = (float) (cx + radius * Math.cos(radians));
        float y = (float) (cy + radius * Math.sin(radians));

        canvas.save();
        canvas.rotate(angle + 90, x, y);
        textPaint.setTextSize(40);
        textPaint.setFakeBoldText(true);
        canvas.drawText(text, x, y, textPaint);
        canvas.restore();
    }

    public void setBMIValue(float bmi) {
        this.bmiValue = bmi;
        invalidate();
    }
}



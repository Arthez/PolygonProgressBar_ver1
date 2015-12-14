package arthur.hexagonprogressanimation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;


public class PolygonProgressBar extends View {

    /* TODO default optimal values for size/other_attributes */
    private static final float DEFAULT_PROGRESSBAR_SIZE = 600f;//150f;
    private static final float DEFAULT_STROKE_THICKNESS = 5;
    private static final int DEFAULT_NUMBER_OF_POLYGON_SIDES = 6;
    private static final int DEFAULT_ANIMATION_DURATION = 5000;
    private static final int DEFAULT_NUMBER_OF_POLYGONS = 20;
    private static final int DEFAULT_POLYGON_ANIMATION_DEGREE_DELAY = 20;
    private static final boolean DEFAULT_DRAW_DIAGONALS = false;

    private Paint mProgressBarPaint;
    private ValueAnimator mProgressAnimation;
    private float mAnimationProgressValue;
    private int mAnimationDuration;
    private float mStrokeThickness;
    private float mProgressBarSize;
    private int mProgressBarColor;
    private int mNumberOfPolygonSides;
    private int mNumberOfPolygons;
    private int mPolygonAnimationDegreeDelay;
    private boolean mDrawDiagonals;

    private float mCenterX;
    private float mCenterY;
    private float mPolygonRadius;
    private double mRadianValueOfPolygonSection;


    public PolygonProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public PolygonProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PolygonProgressBar);
            try {
                mAnimationDuration = array.getInteger(R.styleable.PolygonProgressBar_animation_duration,
                        DEFAULT_ANIMATION_DURATION);
                mStrokeThickness = array.getDimension(R.styleable.PolygonProgressBar_stroke_thickness,
                        DEFAULT_STROKE_THICKNESS);
                mProgressBarSize = array.getDimension(R.styleable.PolygonProgressBar_progressBar_size,
                        DEFAULT_PROGRESSBAR_SIZE);
                mNumberOfPolygonSides = array.getInteger(R.styleable.PolygonProgressBar_number_of_progressBar_polygonSides,
                        DEFAULT_NUMBER_OF_POLYGON_SIDES);
                mNumberOfPolygons = array.getInteger(R.styleable.PolygonProgressBar_number_of_progressBar_polygons,
                        DEFAULT_NUMBER_OF_POLYGONS);
                mPolygonAnimationDegreeDelay = array.getInteger(R.styleable.PolygonProgressBar_polygon_animation_degree_delay,
                        DEFAULT_POLYGON_ANIMATION_DEGREE_DELAY);
                mProgressBarColor = array.getColor(R.styleable.PolygonProgressBar_progressBar_color,
                        getDefaultProgressBarColor());
                mDrawDiagonals = array.getBoolean(R.styleable.PolygonProgressBar_draw_polygon_diagonals,
                        DEFAULT_DRAW_DIAGONALS);
            } finally {
                array.recycle();
            }
        }
        setupPaint();
        setupAnimation();
    }

    private void setupPaint() {
        mProgressBarPaint = new Paint();
        mProgressBarPaint.setColor(mProgressBarColor);
        mProgressBarPaint.setStyle(Paint.Style.STROKE);
        mProgressBarPaint.setStrokeWidth(mStrokeThickness);
        mProgressBarPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    private int getDefaultProgressBarColor() {
        return ContextCompat.getColor(getContext(), R.color.default_progress_bar_color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(Math.round(DEFAULT_PROGRESSBAR_SIZE), Math.round(DEFAULT_PROGRESSBAR_SIZE));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        mPolygonRadius = mProgressBarSize / 2 - mStrokeThickness;
        mRadianValueOfPolygonSection = 2 * Math.PI / mNumberOfPolygonSides;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        if(mProgressAnimation.isStarted()) //TODO toggle turning off drawing
            for(int polygonIndex = 1; polygonIndex <= mNumberOfPolygons; polygonIndex++) {
                drawProgressBarAnimation(canvas, ((float) polygonIndex / mNumberOfPolygons), polygonIndex);
            }
    }

    private void drawProgressBarAnimation(Canvas canvas, float polygonRadiusRatio, int polygonIndex) {
        double degreeDelay = getDegreeDelayForPolygon(polygonIndex);
        drawPolygon(canvas, polygonRadiusRatio, degreeDelay);
        if(mDrawDiagonals) drawPolygonDiagonals(canvas, polygonRadiusRatio, degreeDelay);
    }

    private void drawPolygon(Canvas canvas, float polygonRadiusRatio,  double degreeDelay) {
        Path polygonPath = new Path();
        polygonPath.moveTo(
                (float) (mCenterX + (mPolygonRadius * polygonRadiusRatio) * Math.cos(degreeDelay)),
                (float) (mCenterY + (mPolygonRadius * polygonRadiusRatio) * Math.sin(degreeDelay)));

        for(int polygonSideIndex = 1; polygonSideIndex < mNumberOfPolygonSides; polygonSideIndex++){
            polygonPath.lineTo(
                    (float) (mCenterX + (mPolygonRadius * polygonRadiusRatio) *
                            Math.cos(degreeDelay + mRadianValueOfPolygonSection * polygonSideIndex)),
                    (float) (mCenterY + (mPolygonRadius * polygonRadiusRatio) *
                            Math.sin(degreeDelay + mRadianValueOfPolygonSection * polygonSideIndex)));
        }
        polygonPath.close();
        canvas.drawPath(polygonPath, mProgressBarPaint);
    }

    private void drawPolygonDiagonals(Canvas canvas, float polygonRadiusRatio,  double degreeDelay) {
        for(int i = 0; i < mNumberOfPolygonSides; i++){
            canvas.drawLine(mCenterX, mCenterY,
                    (float) (mCenterX + (mPolygonRadius * polygonRadiusRatio) * Math.cos(degreeDelay + mRadianValueOfPolygonSection * i)),
                    (float) (mCenterY + (mPolygonRadius * polygonRadiusRatio) * Math.sin(degreeDelay + mRadianValueOfPolygonSection * i)),
                    mProgressBarPaint);
        }
    }

    private double getDegreeDelayForPolygon(int polygonIndex) {
        float rotationDelay = DEFAULT_POLYGON_ANIMATION_DEGREE_DELAY * (polygonIndex-1);
        double degreeDelay;
        if (rotationDelay <= mAnimationProgressValue && mAnimationProgressValue <= 360 + rotationDelay) {
            degreeDelay = Math.PI * ((double)(mAnimationProgressValue - rotationDelay) / 180 + 1.5);
        } else {
            degreeDelay = Math.PI * 1.5; // 270 degree
        }
        return degreeDelay;
    }

    private void setupAnimation() {
        mProgressAnimation = ValueAnimator.ofFloat(0, 360 + mPolygonAnimationDegreeDelay * (mNumberOfPolygons - 1));
        mProgressAnimation.setDuration(mAnimationDuration);
        mProgressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mProgressAnimation.setRepeatCount(ValueAnimator.INFINITE);
        mProgressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimationProgressValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    public void setAnimationInterpolator(Interpolator interpolator) {
        mProgressAnimation.setInterpolator(interpolator);
    }

    public void toggleAnimation() {
        if(mProgressAnimation.isRunning()) {
            mProgressAnimation.end();
        }
        else {
            mProgressAnimation.start();
        }
    }
}



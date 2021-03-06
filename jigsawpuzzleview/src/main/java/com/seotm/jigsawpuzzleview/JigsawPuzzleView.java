package com.seotm.jigsawpuzzleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.seotm.jigsawpuzzleview.motion.SegmentMotion;
import com.seotm.jigsawpuzzleview.motion.SegmentMotionImpl;
import com.seotm.jigsawpuzzleview.pattern.SegmentsPattern;

/**
 * Created by seotm on 13.06.17.
 */

public class JigsawPuzzleView extends View {

    private static final String SUPER_STATE = "JigsawPuzzleView_superState";

    private SegmentsPattern segmentsPattern;
    private SegmentMotion segmentMotion;
    private final PuzzleGatherListenerProxy gatherListenerProxy = new PuzzleGatherListenerProxy();

    public JigsawPuzzleView(Context context) {
        super(context);
    }

    public JigsawPuzzleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JigsawPuzzleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSegments(@NonNull @DrawableRes int [] segments) {
        SegmentsPatternFactory patternFct = new SegmentsPatternFactory(getContext(), gatherListenerProxy);
        segmentsPattern = patternFct.createPattern(segments);
        segmentMotion = new SegmentMotionImpl(segmentsPattern, this);
    }

    public void setSegments(@NonNull Bitmap [] bitmaps) {
        SegmentsPatternFactory patternFct = new SegmentsPatternFactory(getContext(), gatherListenerProxy);
        segmentsPattern = patternFct.createPattern(bitmaps);
        segmentMotion = new SegmentMotionImpl(segmentsPattern, this);
    }

    public void setGatherListener(PuzzleGatherListener listener) {
        this.gatherListenerProxy.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (segmentsPattern != null) {
            segmentsPattern.updateSize(w, h, oldw, oldh);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (segmentsPattern != null) {
            segmentsPattern.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (segmentMotion != null) {
            segmentMotion.onTouchEvent(event);
        }
        return true;
    }

    public void blendSegments() {
        if (segmentsPattern != null) {
            segmentsPattern.blendSegments();
            invalidate();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState());
        if (segmentsPattern != null) segmentsPattern.saveState(bundle);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            if (segmentsPattern != null) {
                segmentsPattern.restoreState(bundle);
                invalidate();
            }
            state = bundle.getParcelable(SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }
}

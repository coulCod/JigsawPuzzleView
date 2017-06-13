package com.seotm.jigsawpuzzleview.pattern.p12;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.seotm.jigsawpuzzleview.pattern.Position;
import com.seotm.jigsawpuzzleview.pattern.Segment;
import com.seotm.jigsawpuzzleview.pattern.SegmentPositions;
import com.seotm.jigsawpuzzleview.pattern.SegmentSize;
import com.seotm.jigsawpuzzleview.pattern.Segments;
import com.seotm.jigsawpuzzleview.pattern.SegmentsDrawer;
import com.seotm.jigsawpuzzleview.pattern.SegmentsPattern;

/**
 * Created by seotm on 13.06.17.
 */

public class SegmentPattern12 implements SegmentsPattern {

    private static final SegmentSize SEGMENT_SIZE = new SegmentSize(6f/16f, 5f/9f);
    private static final float LEDGE_PART_RATIO = 1f/4f;

    private final Segments segments;
    private final SegmentsDrawer segmentsDrawer;
    private final SegmentPositions segmentPositions = new SegmentPositions12();

    public SegmentPattern12(@NonNull @DrawableRes int [] segments, @NonNull Context context) {
        this.segments = new Segments(segments, context, SEGMENT_SIZE);
        segmentsDrawer = new SegmentsDrawer(this.segments, segmentPositions);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        segmentsDrawer.draw(canvas);
    }

    @Override
    public void updateSize(int w, int h, int oldw, int oldh) {
        segments.updateSize(w, h);
    }

    @Override
    public void setSegmentMovableAt(int x, int y) {
        if (!segments.isSized()) return;
        for (Segment segment : segments.getSegments()) {
            int centerX = segment.centerPosition.getX();
            int centerY = segment.centerPosition.getY();
            int segmentWidth = segment.getBitmap().getWidth();
            int segmentHeight = segment.getBitmap().getHeight();
            int ledgeSize = (int) (LEDGE_PART_RATIO*segmentWidth);
            int segmentAbsoluteWidth = segmentWidth - 2*ledgeSize;
            int segmentAbsoluteHeight = segmentHeight - 2*ledgeSize;
            int left = centerX - segmentAbsoluteWidth/2;
            int top = centerY - segmentAbsoluteHeight/2;
            int right = centerX + segmentAbsoluteWidth/2;
            int bottom = centerY + segmentAbsoluteHeight/2;
            if (x >= left && x <= right && y >= top && y <= bottom) {
                segment.setMotionPosition(x, y);
                break;
            }
        }
    }

    @Override
    public void moveMovableSegmentTo(int x, int y) {
        Segment segment = getMovableSegment();
        if (segment != null) {
            segment.setMotionPosition(x, y);
        }
    }

    private Segment getMovableSegment() {
        for (Segment segment : segments.getSegments()) {
            if (segment.isMovable()) {
                return segment;
            }
        }
        return null;
    }

    @Override
    public void updatePositions() {
        Segment segment = getMovableSegment();
        if (segment == null) return;
        Position motionPos = segment.getMotionPosition();
        int [] distanceToVertexes = getDistanceToVertexesFromPosition(motionPos);
        int minDistanceIndex = getMinDistanceIndex(distanceToVertexes);
        segments.swapSegments(minDistanceIndex, segment);
        segment.stopMoving();
    }

    private int[] getDistanceToVertexesFromPosition(Position position) {
        Position[] segmentVertexes = getAllSegmentVertexes();
        int [] distanceToVertexes = new int[segmentVertexes.length];
        for (int i=0; i<distanceToVertexes.length; i++) {
            Position vertex = segmentVertexes[i];
            distanceToVertexes[i] = getDistanceBeetwenPoints(vertex, position);
        }
        return distanceToVertexes;
    }

    private int getDistanceBeetwenPoints(Position vertex, Position motionPos) {
        int x1 = vertex.getX();
        int y1 = vertex.getY();
        int x2 = motionPos.getX();
        int y2 = motionPos.getY();
        return (int) Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    private int getMinDistanceIndex(int [] distances) {
        int minDistanceIndex = 0;
        for (int i=1; i<distances.length; i++) {
            int distance = distances[i];
            int minDistance = distances[minDistanceIndex];
            if (distance < minDistance) {
                minDistanceIndex = i;
            }
        }
        return minDistanceIndex;
    }

    private Position[] getAllSegmentVertexes() {
        Position[] vertexes = new Position[12];
        int width = segments.getViewWidth();
        int height = segments.getViewHeight();
        for (int i=0; i<vertexes.length; i++) {
            vertexes[i] = segmentPositions.getSegmentCenterPositionAt(i+1, width, height);
        }
        return vertexes;
    }
}
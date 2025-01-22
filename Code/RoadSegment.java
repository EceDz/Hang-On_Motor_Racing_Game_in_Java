package Project;

public enum RoadSegment {
    STRAIGHT, RIGHT_CURVE, LEFT_CURVE;

    private static RoadSegment lastCurveDirection = null;

    public RoadSegment getNextSegment() {
        switch (this) {
            case STRAIGHT:
                if (lastCurveDirection == RIGHT_CURVE) {
                    lastCurveDirection = LEFT_CURVE;
                    return LEFT_CURVE;
                } else if (lastCurveDirection == LEFT_CURVE) {
                    lastCurveDirection = RIGHT_CURVE;
                    return RIGHT_CURVE;
                } else {
                    boolean goDirection = Math.random() < 0.5;
                    lastCurveDirection = (goDirection ? RIGHT_CURVE : LEFT_CURVE);
                    return lastCurveDirection;
                }

            case RIGHT_CURVE:
            case LEFT_CURVE:
                return STRAIGHT;

            default:
                throw new IllegalStateException("Unexpected road segment type: " + this);
        }
    }
}

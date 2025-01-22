package Project;

import java.awt.*;

public class RoadManager {
   public static int ROAD_HEIGHT= 300;
   private double curveOffset = 0;
   private double stripOffset = 0;
   private RoadSegment currentSegment;
   private RoadSegment previousSegment = RoadSegment.STRAIGHT;
   private int segmentProgress = 0;
   private final int SEGMENT_LENGTH = 300;
   private boolean isMoving = false;
   private static final double BASE_CURVE_SPEED = 0.005;
   private static final double MAX_CURVE_OFFSET = 1.0;
   private double targetCurveOffset = 0;
   private double centerStripOffset = 0;
   private int completedCycles = 0;
   private int segmentCount = 0;
   private boolean isAtCheckpoint = false;
   private boolean checkpointPassed = false;
   private double checkpointPosition = 1.0;
   private int cyclesSinceLastCheckpoint = 0;

   public RoadManager() {
       currentSegment = RoadSegment.STRAIGHT;
   }
   public int getCompletedCycles() {
       return completedCycles;
   }

   public RoadSegment getCurrentSegment() {
       return currentSegment;
   }

   public RoadSegment getPreviousSegment() {
       return previousSegment;
   }

   public boolean isAtCheckpoint() {
       return isAtCheckpoint;
   }

   public boolean checkpointPassed() {
       if (checkpointPassed) {
           checkpointPassed = false;
           return true;
       }
       return false;
   }

   public void setMoving(boolean moving) {
       this.isMoving = moving;
   }

   public void update(int speed) {
       if (!isMoving || speed == 0) {
           return;
       }

       double speedFactor = speed / 1000.0;
       segmentProgress += Math.max(1, speedFactor * 15);

       if (segmentProgress >= SEGMENT_LENGTH) {
           segmentProgress = 0;
           updateSegment();
           segmentCount++;

           if (segmentCount == 4) {
               segmentCount = 0;
               completedCycles++;
               cyclesSinceLastCheckpoint++;

               if (cyclesSinceLastCheckpoint % 4 == 0) {
                   cyclesSinceLastCheckpoint = 0;
                   isAtCheckpoint = true;
                   checkpointPassed = false;
                   checkpointPosition = 1.0;
               }
           }
       }

       if (isAtCheckpoint) {
           checkpointPosition -= speedFactor * 0.02;

           if (checkpointPosition <= 0.2 && !checkpointPassed) {
               checkpointPassed = true;
           }

           if (checkpointPosition <= 0) {
               isAtCheckpoint = false;
               checkpointPassed = false;
               checkpointPosition = 1.0;
           }
       }

       switch (currentSegment) {
           case RIGHT_CURVE:
               targetCurveOffset = MAX_CURVE_OFFSET;
               break;
           case LEFT_CURVE:
               targetCurveOffset = -MAX_CURVE_OFFSET;
               break;
           case STRAIGHT:
               targetCurveOffset = 0;
               break;
       }

       double speedScale = speed / 200.0;
       double curveSpeed = BASE_CURVE_SPEED * (1 + speedScale);

       if (Math.abs(curveOffset - targetCurveOffset) > curveSpeed) {
           if (curveOffset < targetCurveOffset) {
               curveOffset += curveSpeed;
           } else if (curveOffset > targetCurveOffset) {
               curveOffset -= curveSpeed;
           }
       } else {
           curveOffset = targetCurveOffset;
       }

       stripOffset = (stripOffset + speed * 0.1) % 100;
       centerStripOffset = (centerStripOffset + speed * 0.1) % 100;
   }

   private void updateSegment() {
       previousSegment = currentSegment;
       currentSegment = currentSegment.getNextSegment();
   }

   public void drawRoad(Graphics2D graphics, int width, int height, int speed) {
       int segments = 20;
       int roadWidth = 600;
       int baseY = height;

       for (int i = 0; i < segments; i++) {
           double perspective = (double) i / segments;
           double depth = 1 - perspective;

           int segmentWidth = (int)(roadWidth * depth);
           int y = baseY - (int)(perspective * 315);
           int segmentHeight = height / (segments * 2) + 1;

           int xOffset = (int)(curveOffset * width * 0.5 * perspective);
           int centerX = width/2 + xOffset;

           graphics.setColor(Color.DARK_GRAY);
           int[] xPoints = {
               centerX - segmentWidth/2,
               centerX + segmentWidth/2,
               centerX + segmentWidth/2,
               centerX - segmentWidth/2
           };
           int[] yPoints = {
               y,
               y,
               y + segmentHeight,
               y + segmentHeight
           };
           graphics.fillPolygon(xPoints, yPoints, 4);

           int stripWidth = (int)(30 * depth);
           double adjustedStripOffset = (stripOffset + i * 20) % 40;
           graphics.setColor((adjustedStripOffset < 20) ? Color.RED : Color.WHITE);

           graphics.fillRect(centerX - segmentWidth/2 - stripWidth, y, stripWidth, segmentHeight);
           graphics.fillRect(centerX + segmentWidth/2, y, stripWidth, segmentHeight);

           int centerLineWidth = (int)(8 * depth);
           double adjustedCenterOffset = (centerStripOffset + i * 20) % 40;
           graphics.setColor((adjustedCenterOffset < 20) ? Color.WHITE : Color.DARK_GRAY);
           graphics.fillRect(centerX - centerLineWidth/2, y, centerLineWidth, segmentHeight);

           if (isAtCheckpoint) {
               double checkpointDepth = checkpointPosition;
               if (Math.abs(perspective - checkpointDepth) < 0.05) {
            	   graphics.setColor(Color.WHITE);
            	   graphics.fillRect(
                       centerX - segmentWidth/2,
                       y,
                       segmentWidth,
                       segmentHeight
                   );
               }
           }
       }
   }

   public int getLeftOffset(int width) {
       return (int)(width/2 - 250 + curveOffset * width * 0.3);
   }

   public int getRightOffset(int width) {
       return (int)(width/2 + 250 + curveOffset * width * 0.3);
   }
   

   public void reset() {
       curveOffset = 0;
       stripOffset = 0;
       centerStripOffset = 0;
       segmentProgress = 0;
       segmentCount = 0;
       completedCycles = 0;
       isAtCheckpoint = false;
       checkpointPassed = false;
       checkpointPosition = 1.0;
       cyclesSinceLastCheckpoint = 0;
       currentSegment = RoadSegment.STRAIGHT;
       previousSegment = RoadSegment.STRAIGHT;
       isMoving = false;
   }

}
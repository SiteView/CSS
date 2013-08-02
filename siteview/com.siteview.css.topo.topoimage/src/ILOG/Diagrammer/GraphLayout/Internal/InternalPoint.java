package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.*;
import system.*;
import system.Math;

public class InternalPoint {
	public float X = 0f;

	public float Y = 0f;

	public InternalPoint() {
	}

	public InternalPoint(InternalPoint p) {
		this.X = p.X;
		this.Y = p.Y;
	}

	public InternalPoint(Point2D p) {
		this.X = p.get_X();
		this.Y = p.get_Y();
	}

	public InternalPoint(float x, float y) {
		this.X = x;
		this.Y = y;
	}

	@Override
	public boolean equals(java.lang.Object obj) {
		if (!(obj instanceof InternalPoint)) {

			return super.equals(obj);
		}
		InternalPoint point = (InternalPoint) obj;

		return ((this.GetX() == point.GetX()) && (this.GetY() == point.GetY()));

	}

	public void Floor() {
		if (this.X >= 0f) {
			this.X = (int) this.X;
		} else {
			this.X = (float) Math.Floor((double) this.X);
		}
		if (this.Y >= 0f) {
			this.Y = (int) this.Y;
		} else {
			this.Y = (float) Math.Floor((double) this.Y);
		}

	}

	@Override
	public int hashCode() {

		return super.hashCode();

	}

	public static InternalPoint[] GetInternalPoints(Point2D[] points2D) {
		if (points2D == null) {

			return null;
		}
		Integer length = points2D.length;
		InternalPoint[] pointArray = new InternalPoint[length];
		for (Integer i = 0; i < length; i++) {
			pointArray[i] = new InternalPoint(points2D[i]);
		}

		return pointArray;

	}

	public static Point2D[] GetPoints2D(InternalPoint[] internalPoints) {
		if (internalPoints == null) {

			return null;
		}
		Integer length = internalPoints.length;
		Point2D[] pointdArray = new Point2D[length];
		InternalPoint point = null;
		for (Integer i = 0; i < length; i++) {
			point = internalPoints[i];
			pointdArray[i] = new Point2D(point.X, point.Y);
		}

		return pointdArray;

	}

	public double GetX() {

		return (double) this.X;

	}

	public double GetY() {

		return (double) this.Y;

	}

	public void Move(float x, float y) {
		this.X = x;
		this.Y = y;

	}

	public void SetLocation(InternalPoint point) {
		this.X = point.X;
		this.Y = point.Y;

	}

	public void SetLocation(double x, double y) {
		this.X = (float) x;
		this.Y = (float) y;

	}

	public void SetLocation(float x, float y) {
		this.X = x;
		this.Y = y;

	}

	@Override
	public String toString() {

		return String.format("{{X=%d, Y=%d}}", this.X, this.Y);

	}

	public void Translate(float dx, float dy) {
		this.X += dx;
		this.Y += dy;

	}

	public Integer XFloor() {
		if (this.X < 0f) {

			return (int) Math.Floor((double) this.X);
		}

		return (int) this.X;

	}

	public Integer YFloor() {
		if (this.Y < 0f) {

			return (int) Math.Floor((double) this.Y);
		}

		return (int) this.Y;

	}

}
package ILOG.Diagrammer;

import system.*;
import system.ComponentModel.*;
import system.Drawing.*;
import system.Runtime.InteropServices.*;

public class Point2D {
	private float x;

	private float y;

	public static Point2D Empty;

	public static Point2D Invalid;

	public Point2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Point2D() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean equals(java.lang.Object obj) {
		if (obj instanceof Point2D) {
			Point2D pointd = (Point2D) obj;
			if (((pointd.x == this.x) && (pointd.y == this.y))
					|| (((pointd.x == Float.NEGATIVE_INFINITY) && (this.x == Float.NEGATIVE_INFINITY)) && ((pointd.y == Float.POSITIVE_INFINITY) && (this.y == Float.POSITIVE_INFINITY)))) {

				return true;
			}
		}

		return false;

	}

	@Override
	public int hashCode() {

		return super.hashCode();

	}

	@Override
	public String toString() {

		return String.format("{{X=%d, Y=%d}}", this.x, this.y);

	}

	public Boolean get_IsEmpty() {

		return ((this.x == 0f) && (this.y == 0f));
	}

	public float get_X() {

		return this.x;
	}

	public void set_X(float value) {
		this.x = value;
	}

	public float get_Y() {

		return this.y;
	}

	public void set_Y(float value) {
		this.y = value;
	}

	/* TODO: #TrackedVisitOperatorDeclaration# *//*
												 * TODO:
												 * #TrackedVisitOperatorDeclaration
												 * #
												 *//*
													 * TODO: #
													 * TrackedVisitOperatorDeclaration
													 * #
													 *//*
														 * TODO: #
														 * TrackedVisitOperatorDeclaration
														 * #
														 *//*
															 * TODO: #
															 * TrackedVisitOperatorDeclaration
															 * #
															 */
	public Boolean get_IsInvalid() {

		return (this.x == Float.NEGATIVE_INFINITY)
				&& (this.y == Float.POSITIVE_INFINITY);
	}

	static {
		Empty = new Point2D();
		Invalid = new Point2D(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}

}
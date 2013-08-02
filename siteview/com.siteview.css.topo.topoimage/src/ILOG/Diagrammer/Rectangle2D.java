package ILOG.Diagrammer;

import system.*;
import system.Math;
import system.ComponentModel.*;
import system.Drawing.*;
import system.Runtime.InteropServices.*;

public class Rectangle2D {
	private float _x;

	private float _y;

	private float _width;

	private float _height;

	public static Rectangle2D Empty;

	public static Rectangle2D Invalid;

	public Rectangle2D(Point2D location, Size2D size) {
		this._x = location.get_X();
		this._y = location.get_Y();
		this._width = size.get_Width();
		this._height = size.get_Height();
	}

	public Rectangle2D(float x, float y, float width, float height) {
		this._x = x;
		this._y = y;
		this._width = width;
		this._height = height;
	}

	public Rectangle2D() {
		// TODO Auto-generated constructor stub
	}

	public float get_X() {

		return this._x;
	}

	public void set_X(float value) {
		this._x = value;
	}

	public float get_Y() {

		return this._y;
	}

	public void set_Y(float value) {
		this._y = value;
	}

	public float get_Width() {

		return this._width;
	}

	public void set_Width(float value) {
		this._width = value;
	}

	public float get_Height() {

		return this._height;
	}

	public void set_Height(float value) {
		this._height = value;
	}

	public Size2D get_Size() {

		return new Size2D(this._width, this._height);
	}

	public void set_Size(Size2D value) {
		this._width = value.get_Width();
		this._height = value.get_Height();
	}

	public Point2D get_Location() {

		return new Point2D(this._x, this._y);
	}

	public void set_Location(Point2D value) {
		this._x = value.get_X();
		this._y = value.get_Y();
	}

	public Boolean Contains(Point2D pt) {

		return this.Contains(pt.get_X(), pt.get_Y());

	}

	public Boolean Contains(float x, float y) {

		return ((((this._x <= x) && (x < (this._x + this._width))) && (this._y <= y)) && (y < (this._y + this._height)));

	}

	public Boolean Contains(Rectangle2D rect) {

		return ((((this._x <= rect._x) && ((rect._x + rect._width) <= (this._x + this._width))) && (this._y <= rect._y)) && ((rect._y + rect._height) <= (this._y + this._height)));

	}

	public float get_Left() {

		return this._x;
	}

	public float get_Right() {

		return (this.get_X() + this.get_Width());
	}

	public Point2D get_TopLeft() {

		return new Point2D(this.get_Left(), this.get_Top());
	}

	public Point2D get_TopRight() {

		return new Point2D(this.get_Right(), this.get_Top());
	}

	public Point2D get_BottomLeft() {

		return new Point2D(this.get_Left(), this.get_Bottom());
	}

	public Point2D get_BottomRight() {

		return new Point2D(this.get_Right(), this.get_Bottom());
	}

	public Point2D get_Center() {

		return new Point2D(this.get_X() + (this.get_Width() / 2f), this.get_Y()
				+ (this.get_Height() / 2f));
	}

	public Boolean get_IsEmpty() {
		if (this._width > 0f) {

			return (this._height <= 0f);
		}

		return true;
	}

	public Boolean get_IsInvalid() {
		if (this._width >= 0f) {

			return (this._height < 0f);
		}

		return true;
	}

	public float get_Top() {

		return this._y;
	}

	public float get_Bottom() {

		return (this._y + this._height);
	}

	@Override
	public boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Rectangle2D)) {

			return false;
		}
		Rectangle2D rectangled = (Rectangle2D) obj;

		return ((((rectangled._x == this._x) && (rectangled._y == this._y)) && (rectangled._width == this._width)) && (rectangled._height == this._height));

	}

	@Override
	public int hashCode() {

		return (Integer) (((((int) this._x) ^ ((((int) this._y) << 13) | (((int) this._y) >> 0x13))) ^ ((((int) this._width) << 0x1a) | (((int) this._width) >> 6))) ^ ((((int) this._height) << 7) | (((int) this._height) >> 0x19)));

	}

	public void Inflate(Size2D size) {
		this.Inflate(size.get_Width(), size.get_Height());

	}

	public void Inflate(float x, float y) {
		this._x -= x;
		this._y -= y;
		this._width += x;
		this._height += y;

	}

	public static Rectangle2D Inflate(Rectangle2D rect, float x, float y) {
		Rectangle2D rectangled = rect;
		rectangled.Inflate(x, y);

		return rectangled;

	}

	public void Intersect(Rectangle2D rect) {
		Rectangle2D rectangled = Intersect(rect, this);
		this._x = rectangled._x;
		this._y = rectangled._y;
		this._width = rectangled._width;
		this._height = rectangled._height;

	}

	public static Rectangle2D Intersect(Rectangle2D a, Rectangle2D b) {
		float x = Math.Max(a.get_X(), b.get_X());
		float num2 = Math.Min((float) (a.get_X() + a.get_Width()),
				(float) (b.get_X() + b.get_Width()));
		float y = Math.Max(a.get_Y(), b.get_Y());
		float num4 = Math.Min((float) (a.get_Y() + a.get_Height()),
				(float) (b.get_Y() + b.get_Height()));
		if ((num2 >= x) && (num4 >= y)) {

			return new Rectangle2D(x, y, num2 - x, num4 - y);
		}

		return Empty;

	}

	public Boolean IntersectsWith(Rectangle2D rect) {

		return ((((rect._x < (this._x + this._width)) && (this._x < (rect._x + rect._width))) && (rect._y < (this._y + this._height))) && (this._y < (rect._y + rect._height)));

	}

	public void Offset(Point2D pos) {
		this.Offset(pos.get_X(), pos.get_Y());

	}

	public void Offset(float x, float y) {
		this._x += x;
		this._y += y;

	}

	/* TODO: #TrackedVisitOperatorDeclaration# *//*
												 * TODO:
												 * #TrackedVisitOperatorDeclaration
												 * #
												 *//*
													 * TODO: #
													 * TrackedVisitOperatorDeclaration
													 * #
													 */@Override
	public String toString() {

		return ("{X=" + this._x + ",Y=" + this._y + ",Width=" + this._width
				+ ",Height=" + this._height + "}");

	}

	public static Rectangle2D Union(Rectangle2D a, Rectangle2D b) {
		float x = Math.Min(a._x, b._x);
		float num2 = Math.Max((float) (a._x + a._width),
				(float) (b._x + b._width));
		float y = Math.Min(a._y, b._y);
		float num4 = Math.Max((float) (a._y + a._height),
				(float) (b._y + b._height));

		return new Rectangle2D(x, y, num2 - x, num4 - y);

	}

	/* TODO: #TrackedVisitOperatorDeclaration# *//*
												 * TODO:
												 * #TrackedVisitOperatorDeclaration
												 * #
												 */
	static {
		Empty = new Rectangle2D();
		Invalid = new Rectangle2D(0f, 0f, -1f, -1f);
	}

}
package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.*;
import system.*;
import system.Math;

public class InternalRect {
	public float Height = 0;

	public float Width = 0;

	public float X = 0;

	public float Y = 0;

	public InternalRect() {
	}

	public InternalRect(InternalRect rect) {
		this.X = rect.X;
		this.Y = rect.Y;
		this.Width = rect.Width;
		this.Height = rect.Height;
	}

	public InternalRect(Rectangle2D rect) {
		this.X = rect.get_X();
		this.Y = rect.get_Y();
		this.Width = rect.get_Width();
		this.Height = rect.get_Height();
	}

	public InternalRect(float x, float y, float width, float height) {
		this.X = x;
		this.Y = y;
		this.Height = height;
		this.Width = width;
	}

	public void Add(InternalPoint point) {
		this.Add(point.X, point.Y);

	}

	public void Add(InternalRect rect) {
		double x = this.X;
		double y = rect.X;
		double num3 = (x < y) ? x : y;
		x += this.Width;
		y += rect.Width;
		double num5 = (x > y) ? x : y;
		x = this.Y;
		y = rect.Y;
		double num4 = (x < y) ? x : y;
		x += this.Height;
		y += rect.Height;
		double num6 = (x > y) ? x : y;
		this.X = (float) num3;
		this.Y = (float) num4;
		this.Width = (float) (num5 - num3);
		this.Height = (float) (num6 - num4);

	}

	public void Add(float px, float py) {
		if (px < this.X) {
			this.Width += this.X - px;
			this.X = px;
		} else if (px > (this.X + this.Width)) {
			this.Width = px - this.X;
		}
		if (py < this.Y) {
			this.Height += this.Y - py;
			this.Y = py;
		} else if (py > (this.Y + this.Height)) {
			this.Height = py - this.Y;
		}

	}

	public Boolean Contains(InternalRect rect) {
		float width = rect.Width;
		float height = rect.Height;
		if (((this.Width <= 0f) || (this.Height <= 0f))
				|| ((width < 0f) || (height < 0f))) {

			return false;
		}
		float x = rect.X;
		float y = rect.Y;

		return ((((x >= this.X) && (y >= this.Y)) && ((x + width) <= (this.X + this.Width))) && ((y + height) <= (this.Y + this.Height)));

	}

	@Override
	public boolean equals(java.lang.Object obj) {
		if (!(obj instanceof InternalRect)) {

			return super.equals(obj);
		}
		InternalRect rect = (InternalRect) obj;

		return ((((this.X == rect.X) && (this.Y == rect.Y)) && (this.Width == rect.Width)) && (this.Height == rect.Height));

	}

	public void Expand(float delta) {
		this.X -= delta;
		this.Y -= delta;

		this.Width = Math.Max((float) (this.Width + (2f * delta)), (float) 0f);

		this.Height = Math
				.Max((float) (this.Height + (2f * delta)), (float) 0f);

	}

	public void Expand(float dx, float dy) {
		this.X -= dx;
		this.Y -= dy;

		this.Width = Math.Max((float) (this.Width + (2f * dx)), (float) 0f);

		this.Height = Math.Max((float) (this.Height + (2f * dy)), (float) 0f);

	}

	public void Floor() {
		float num = (this.X > 0f) ? ((float) ((int) this.X)) : ((float) Math
				.Floor((double) this.X));
		float num2 = (this.Y > 0f) ? ((float) ((int) this.Y)) : ((float) Math
				.Floor((double) this.Y));
		float num3 = this.X + this.Width;
		num3 = (num3 > 0f) ? ((float) ((int) num3)) : ((float) Math
				.Floor((double) num3));
		float num4 = this.Y + this.Height;
		num4 = (num4 > 0f) ? ((float) ((int) num4)) : ((float) Math
				.Floor((double) num4));
		this.X = num;
		this.Y = num2;
		this.Width = num3 - num;
		this.Height = num4 - num2;

	}

	@Override
	public int hashCode() {

		return super.hashCode();

	}

	public Integer HeightFloor() {
		float num = (this.Y > 0f) ? ((float) ((int) this.Y)) : ((float) Math
				.Floor((double) this.Y));
		float num2 = this.Y + this.Height;
		num2 = (num2 > 0f) ? ((float) ((int) num2)) : ((float) Math
				.Floor((double) num2));

		return (int) (num2 - num);

	}

	public Boolean Inside(float x, float y) {
		float num = this.X;
		float num2 = this.Y;

		return ((((x >= num) && (y >= num2)) && (x <= (num + this.Width))) && (y <= (num2 + this.Height)));

	}

	public void Intersection(InternalRect rect) {
		float num = this.X + this.Width;
		float num2 = this.Y + this.Height;
		float num3 = rect.X + rect.Width;
		float num4 = rect.Y + rect.Height;
		if (rect.X > this.X) {
			this.X = rect.X;
			this.Width = (num > num3) ? rect.Width : (num - this.X);
		} else if (num > num3) {
			this.Width = num3 - this.X;
		}
		if (rect.Y > this.Y) {
			this.Y = rect.Y;
			this.Height = (num2 > num4) ? rect.Height : (num2 - this.Y);
		} else if (num2 > num4) {
			this.Height = num4 - this.Y;
		}

	}

	public Boolean Intersects(InternalRect rect) {
		if (((this.Width <= 0f) || (this.Height <= 0f))
				|| ((rect.Width < 0f) || (rect.Height < 0f))) {

			return false;
		}

		return ((((this.X <= (rect.X + rect.Width)) && ((this.X + this.Width) >= rect.X)) && (this.Y <= (rect.Y + rect.Height))) && ((this.Y + this.Height) >= rect.Y));

	}

	public Boolean IsEmpty() {
		if (this.Width > 0f) {

			return (this.Height <= 0f);
		}

		return true;

	}

	public void Move(float x, float y) {
		this.X = x;
		this.Y = y;

	}

	public void Reshape(float x, float y, float width, float height) {
		this.X = x;
		this.Y = y;
		this.Width = width;
		this.Height = height;

	}

	public void Resize(float width, float height) {
		this.Width = width;
		this.Height = height;

	}

	public void SetRect(InternalRect rect) {
		this.X = rect.X;
		this.Y = rect.Y;
		this.Width = rect.Width;
		this.Height = rect.Height;

	}

	@Override
	public String toString() {

		return String.format("{{X=%d, Y=%d, Width=%d, Height=%d}}",
				new java.lang.Object[] { this.X, this.Y, this.Width,
						this.Height });

	}

	public void Translate(float dx, float dy) {
		this.X += dx;
		this.Y += dy;

	}

	public Integer WidthFloor() {
		float num = (this.X > 0f) ? ((float) ((int) this.X)) : ((float) Math
				.Floor((double) this.X));
		float num2 = this.X + this.Width;
		num2 = (num2 > 0f) ? ((float) ((int) num2)) : ((float) Math
				.Floor((double) num2));

		return (int) (num2 - num);

	}

	public Integer XFloor() {
		if (this.X <= 0f) {

			return (int) Math.Floor((double) this.X);
		}

		return (int) this.X;

	}

	public Integer YFloor() {
		if (this.Y <= 0f) {

			return (int) Math.Floor((double) this.Y);
		}

		return (int) this.Y;

	}

}
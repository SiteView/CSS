package ILOG.Diagrammer.GraphLayout.Internal.GenericQuadtree;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class IlvGenericQuadtree {
	private IlvGenericQuadtree _bottomL;

	private IlvGenericQuadtree _bottomR;

	private InternalRect _contentBBox;

	private Boolean _contentBBoxValid = false;

	private Integer _count;

	private Boolean _divided = false;

	private java.lang.Object[] _elements;

	private double _height;

	private Integer _nbelements;

	private GenericIndexedSet _set;

	private IlvGenericQuadtree _topL;

	private IlvGenericQuadtree _topR;

	private double _width;

	private double _x;

	private double _y;

	private double IlvInitialQuadtreeSize = 10000.0;

	private double IlvMinBBoxSize = 1E-05;

	private static Boolean OptimizeMode = true;

	private Integer QBottom = 8;

	private Integer QBottomL = 10;

	private Integer QBottomR = 9;

	private Integer QInside = 0xffff;

	private Integer QLeft = 2;

	private Integer QRight = 1;

	private Integer QTop = 4;

	private Integer QTopL = 6;

	private Integer QTopR = 5;

	public IlvGenericQuadtree(GenericIndexedSet s) {
		this(s, null);
	}

	private IlvGenericQuadtree(IlvGenericQuadtree q) {
		this._contentBBox = new InternalRect(0f, 0f, 0f, 0f);
		this._topL = q._topL;
		this._topR = q._topR;
		this._bottomL = q._bottomL;
		this._bottomR = q._bottomR;
		this._elements = q._elements;
		this._divided = q._divided;
		this._set = q._set;
		this._nbelements = q._nbelements;
		this._count = q._count;
		this._x = q._x;
		this._y = q._y;
		this._width = q._width;
		this._height = q._height;
		this._contentBBoxValid = false;
		this._contentBBox.Reshape(q._contentBBox.X, q._contentBBox.Y,
				q._contentBBox.Width, q._contentBBox.Height);
	}

	public IlvGenericQuadtree(GenericIndexedSet s, InternalRect rect) {
		this._contentBBox = new InternalRect(0f, 0f, 0f, 0f);
		this._set = s;
		if (rect != null) {
			this._x = rect.X;
			this._y = rect.Y;
			this._width = rect.Width;
			this._height = rect.Height;
		} else {
			this._x = -10000.0;
			this._y = -10000.0;
			this._width = 20000.0;
			this._height = 20000.0;
		}
	}

	private void Add(InternalRect rect) {
		if ((rect.Width != 0f) && (rect.Height != 0f)) {
			double num = system.Math.Min(this._x, (double) rect.X);
			double num2 = system.Math.Max(this._x + this._width,
					(double) (rect.X + rect.Width));
			double num3 = system.Math.Min(this._y, (double) rect.Y);
			double num4 = system.Math.Max(this._y + this._height,
					(double) (rect.Y + rect.Height));
			this._x = num;
			this._y = num3;
			this._width = num2 - num;
			this._height = num4 - num3;
		}

	}

	public void Add(java.lang.Object obj) {
		InternalRect rect = this._set.BoundingBox(obj);
		if (!OptimizeMode && (this.NodeInsideP(obj, rect) != null)) {
			throw (new system.Exception(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] {
									"IlvGenericQuadtree.add: ", obj, " [",
									rect, "] Already in quadtree" })));
		}

		if (!this.Contains(rect)) {
			if (!this._divided) {
				this.Add(rect);
			} else {
				this.GrowToRect(rect);
			}
		}
		this.NodeAdd(obj, rect);

	}

	public void Apply(GenericApplyObject f, java.lang.Object args) {
		for (Integer i = 0; i < this._count; i++) {
			f.Apply(this._elements[i], args);
		}
		if (this._topL != null) {
			this._topL.Apply(f, args);
		}
		if (this._topR != null) {
			this._topR.Apply(f, args);
		}
		if (this._bottomL != null) {
			this._bottomL.Apply(f, args);
		}
		if (this._bottomR != null) {
			this._bottomR.Apply(f, args);
		}

	}

	public void ApplyInside(InternalRect rect, GenericApplyObject f,
			java.lang.Object args) {

		if (this.IsContained(rect)) {
			this.Apply(f, args);
		} else if (this.Intersects(rect)) {
			for (Integer i = 0; i < this._count; i++) {
				java.lang.Object obj2 = this._elements[i];

				if (this.Inside(obj2, rect)) {
					f.Apply(obj2, args);
				}
			}
			if (this._topL != null) {
				this._topL.ApplyInside(rect, f, args);
			}
			if (this._topR != null) {
				this._topR.ApplyInside(rect, f, args);
			}
			if (this._bottomL != null) {
				this._bottomL.ApplyInside(rect, f, args);
			}
			if (this._bottomR != null) {
				this._bottomR.ApplyInside(rect, f, args);
			}
		}

	}

	public void ApplyIntersect(InternalRect rect, GenericApplyObject f,
			java.lang.Object args) {

		if (this.IsContained(rect)) {
			this.Apply(f, args);
		} else if (this.Intersects(rect)) {
			for (Integer i = 0; i < this._count; i++) {
				java.lang.Object obj2 = this._elements[i];

				if (this.Intersects(obj2, rect)) {
					f.Apply(obj2, args);
				}
			}
			if (this._topL != null) {
				this._topL.ApplyIntersect(rect, f, args);
			}
			if (this._topR != null) {
				this._topR.ApplyIntersect(rect, f, args);
			}
			if (this._bottomL != null) {
				this._bottomL.ApplyIntersect(rect, f, args);
			}
			if (this._bottomR != null) {
				this._bottomR.ApplyIntersect(rect, f, args);
			}
		}

	}

	public void ComputeBBox(InternalRect bbox) {
		if (((this._x <= bbox.X) || (this._y <= bbox.Y))
				|| (((this._x + this._width) >= (bbox.X + bbox.Width)) || ((this._y + this._height) >= (bbox.Y + bbox.Height)))) {
			if (!this._contentBBoxValid) {
				this._contentBBox.Reshape(0f, 0f, 0f, 0f);
				for (Integer i = 0; i < this._count; i++) {
					java.lang.Object obj2 = this._elements[i];
					InternalRect rect = this._set.BoundingBox(obj2);

					if (this._contentBBox.IsEmpty()) {
						this._contentBBox.Reshape(rect.X, rect.Y, rect.Width,
								rect.Height);
					} else {
						this._contentBBox.Add(rect);
					}
				}
				this._contentBBoxValid = true;
			}

			if (bbox.IsEmpty()) {
				bbox.Reshape(this._contentBBox.X, this._contentBBox.Y,
						this._contentBBox.Width, this._contentBBox.Height);
			} else {
				bbox.Add(this._contentBBox);
			}
			if (this._topL != null) {
				this._topL.ComputeBBox(bbox);
			}
			if (this._topR != null) {
				this._topR.ComputeBBox(bbox);
			}
			if (this._bottomL != null) {
				this._bottomL.ComputeBBox(bbox);
			}
			if (this._bottomR != null) {
				this._bottomR.ComputeBBox(bbox);
			}
		}

	}

	private Boolean Contains(InternalRect rect) {

		return ((((rect.X >= this._x) && ((rect.X + rect.Width) <= (this._x + this._width))) && (rect.Y >= this._y)) && ((rect.Y + rect.Height) <= (this._y + this._height)));

	}

	private Boolean Contains(java.lang.Object obj) {
		if (this._elements != null) {
			for (Integer i = 0; i < this._count; i++) {
				if (obj == this._elements[i]) {

					return true;
				}
			}
		}

		return false;

	}

	public Boolean Contains(java.lang.Object obj, InternalPoint p) {

		return this._set.BoundingBox(obj).Inside(p.X, p.Y);

	}

	public void DeleteAll() {
		this._elements = null;
		this._contentBBoxValid = false;
		this._count = 0;
		this._topL = this._topR = this._bottomL = (IlvGenericQuadtree) (this._bottomR = null);

	}

	private void EnsureCapacity(Integer minCapacity) {
		if (this._elements == null) {
			this._elements = new java.lang.Object[(int) (system.Math.Max(
					this._set.GetMaxInNode(), minCapacity))];
		} else {
			Integer length = this._elements.length;
			if (minCapacity > length) {
				java.lang.Object[] sourceArray = this._elements;
				Integer num2 = length * 2;
				if (num2 < minCapacity) {
					num2 = minCapacity;
				}
				this._elements = new java.lang.Object[num2];
				clr.System.ArrayStaticWrapper.Copy(sourceArray, 0,
						this._elements, 0, this._count);
			}
		}

	}

	private Integer FindPos(InternalPoint p) {
		if ((this._width <= 1E-05) || (this._height <= 1E-05)) {

			return 0xffff;
		}
		if (p.X >= (this._x + (this._width / 2.0))) {
			if (p.Y >= (this._y + (this._height / 2.0))) {

				return 9;
			}

			return 5;
		}
		if (p.Y >= (this._y + (this._height / 2.0))) {

			return 10;
		}

		return 6;

	}

	private Integer FindPos(InternalRect rect) {
		if ((this._width <= 1E-05) || (this._height <= 1E-05)) {

			return 0xffff;
		}
		double x = rect.X;
		double num2 = this._x + (this._width / 2.0);
		Integer num3 = 1;
		if (x < num2) {
			if ((x + rect.Width) > num2) {

				return 0xffff;
			}
			num3 = 2;
		}
		double y = rect.Y;
		double num5 = this._y + (this._height / 2.0);
		if (y >= num5) {

			return (num3 | 8);
		}
		if ((y + rect.Height) > num5) {

			return 0xffff;
		}

		return (num3 | 4);

	}

	private void GrowToPoint(float px, float py) {

		if (!this.Inside(px, py)) {
			IlvGenericQuadtree quadtree = null;
			Integer num = ((this._x >= px) ? 2 : 1) | ((this._y >= py) ? 4 : 8);
			java.lang.Object[] objArray = this._elements;
			Integer num2 = this._count;
			this._elements = null;
			this._contentBBoxValid = false;
			this._count = 0;
			Boolean flag = true;
			if (num == 5) {

				while (!this.Inside(px, py)) {
					quadtree = new IlvGenericQuadtree(this);
					if (flag) {
						flag = false;
						quadtree._elements = objArray;
						quadtree._count = num2;
					} else {
						quadtree._elements = null;
						quadtree._count = 0;
					}
					quadtree._x = this._x;
					quadtree._y = this._y;
					quadtree._width = this._width;
					quadtree._height = this._height;
					this._y -= this._height;
					this._width = 2.0 * this._width;
					this._height = 2.0 * this._height;
					this._bottomL = quadtree;
					this._topL = null;
					this._topR = null;
					this._bottomR = null;
				}
				// NOTICE: break ignore!!!
			} else if (num == 6) {

				while (!this.Inside(px, py)) {
					quadtree = new IlvGenericQuadtree(this);
					if (flag) {
						flag = false;
						quadtree._elements = objArray;
						quadtree._count = num2;
					} else {
						quadtree._elements = null;
						quadtree._count = 0;
					}
					quadtree._x = this._x;
					quadtree._y = this._y;
					quadtree._width = this._width;
					quadtree._height = this._height;
					this._x += -this._width;
					;
					this._y += -this._height;
					this._width = 2.0 * this._width;
					this._height = 2.0 * this._height;
					this._bottomR = quadtree;
					this._topL = null;
					this._topR = null;
					this._bottomL = null;
				}
				// NOTICE: break ignore!!!
			} else if (num == 9) {

				while (!this.Inside(px, py)) {
					quadtree = new IlvGenericQuadtree(this);
					if (flag) {
						flag = false;
						quadtree._elements = objArray;
						quadtree._count = num2;
					} else {
						quadtree._elements = null;
						quadtree._count = 0;
					}
					quadtree._x = this._x;
					quadtree._y = this._y;
					quadtree._width = this._width;
					quadtree._height = this._height;
					this._width = 2.0 * this._width;
					this._height = 2.0 * this._height;
					this._topL = quadtree;
					this._topR = null;
					this._bottomL = null;
					this._bottomR = null;
				}
				// NOTICE: break ignore!!!
			} else if (num == 10) {

				while (!this.Inside(px, py)) {
					quadtree = new IlvGenericQuadtree(this);
					if (flag) {
						flag = false;
						quadtree._elements = objArray;
						quadtree._count = num2;
					} else {
						quadtree._elements = null;
						quadtree._count = 0;
					}
					quadtree._x = this._x;
					quadtree._y = this._y;
					quadtree._width = this._width;
					quadtree._height = this._height;
					this._x -= this._width;
					this._width = 2.0 * this._width;
					this._height = 2.0 * this._height;
					this._topR = quadtree;
					this._topL = null;
					this._bottomL = null;
					this._bottomR = null;
				}
				// NOTICE: break ignore!!!
			}
			this._divided = true;
			this._elements = null;
			this._contentBBoxValid = false;
			this._count = 0;
		}

	}

	private void GrowToRect(InternalRect rect) {
		this.GrowToPoint(rect.X, rect.Y);
		this.GrowToPoint(rect.X + rect.Width, rect.Y + rect.Height);

	}

	public Boolean Inside(java.lang.Object obj, InternalRect rect) {
		InternalRect rect2 = this._set.BoundingBox(obj);

		return rect.Contains(rect2);

	}

	private Boolean Inside(float x, float y) {

		return ((((x >= this._x) && (x <= (this._x + this._width))) && (y >= this._y)) && (y <= (this._y + this._height)));

	}

	private Boolean Intersects(InternalRect rect) {

		return ((((this._x <= (rect.X + rect.Width)) && ((this._x + this._width) >= rect.X)) && (this._y <= (rect.Y + rect.Height))) && ((this._y + this._height) >= rect.Y));

	}

	public Boolean Intersects(java.lang.Object obj, InternalRect rect) {

		return this._set.BoundingBox(obj).Intersects(rect);

	}

	public void InvalidateContentBBox() {
		this._contentBBoxValid = false;

	}

	private Boolean IsContained(InternalRect rect) {

		return ((((this._x >= rect.X) && ((this._x + this._width) <= (rect.X + rect.Width))) && (this._y >= rect.Y)) && ((this._y + this._height) <= (rect.Y + rect.Height)));

	}

	public static Boolean IsOptimizeMode() {

		return OptimizeMode;

	}

	public java.lang.Object LastContains(InternalPoint p) {

		if (this.Inside(p.X, p.Y)) {
			java.lang.Object obj2 = null;
			if (this._divided) {
				Integer pos = this.FindPos(p);
				IlvGenericQuadtree quadtree = this.TreeFromPosition(pos);
				if (quadtree != null) {

					obj2 = quadtree.LastContains(p);
					if (obj2 != null) {

						return obj2;
					}
				}
			}
			if ((this._elements != null) && (this._count != 0)) {
				for (Integer i = this._count - 1; i >= 0; i--) {
					obj2 = this._elements[i];

					if (this.Contains(obj2, p)) {

						return obj2;
					}
				}
			}
		}

		return null;

	}

	private void NodeAdd(java.lang.Object obj, InternalRect rect) {
		this._nbelements++;
		Integer maxInNode = this._set.GetMaxInNode();
		if ((this._nbelements < maxInNode) && !this._divided) {
			this.EnsureCapacity(this._count + 1);
			this._elements[this._count++] = obj;
			this._contentBBoxValid = false;
		} else if (!this._divided
				&& (system.Math.Min(this._width, this._height) <= 1E-05)) {
			this.EnsureCapacity(this._count + 1);
			this._elements[this._count++] = obj;
			this._contentBBoxValid = false;
		} else if ((this._nbelements >= maxInNode) && !this._divided) {
			this._divided = true;
			if (this._elements == null) {
				this._elements = new java.lang.Object[1];
				this._count = 0;
			}
			this.EnsureCapacity(this._count + 1);
			this._elements[this._count++] = obj;
			this._contentBBoxValid = false;
			java.lang.Object[] l = this._elements;
			Integer count = this._count;
			this._elements = null;
			this._count = 0;
			this.NodeAddList(l, count);
		} else {
			if (this.FindPos(rect) == 5) {
				if (this._topR == null) {
					this._topR = new IlvGenericQuadtree(this._set);
					this._topR._x = this._x + (this._width / 2.0);
					this._topR._y = this._y;
					this._topR._width = (this._x + this._width) - this._topR._x;
					this._topR._height = this._height / 2.0;
				}
				this._topR.NodeAdd(obj, rect);

				return;
			} else if (this.FindPos(rect) == 6) {
				if (this._topL == null) {
					this._topL = new IlvGenericQuadtree(this._set);
					this._topL._x = this._x;
					this._topL._y = this._y;
					this._topL._width = this._width / 2.0;
					this._topL._height = this._height / 2.0;
				}
				this._topL.NodeAdd(obj, rect);

				return;
			} else if (this.FindPos(rect) == 7 || this.FindPos(rect) == 8) {
				// NOTICE: break ignore!!!
			} else if (this.FindPos(rect) == 9) {
				if (this._bottomR == null) {
					this._bottomR = new IlvGenericQuadtree(this._set);
					this._bottomR._x = this._x + (this._width / 2.0);
					this._bottomR._y = this._y + (this._height / 2.0);
					this._bottomR._width = (this._x + this._width)
							- this._bottomR._x;
					this._bottomR._height = (this._y + this._height)
							- this._bottomR._y;
				}
				this._bottomR.NodeAdd(obj, rect);

				return;
			} else if (this.FindPos(rect) == 10) {
				if (this._bottomL == null) {
					this._bottomL = new IlvGenericQuadtree(this._set);
					this._bottomL._x = this._x;
					this._bottomL._y = this._y + (this._height / 2.0);
					this._bottomL._width = this._width / 2.0;
					this._bottomL._height = (this._y + this._height)
							- this._bottomL._y;
				}
				this._bottomL.NodeAdd(obj, rect);

				return;
			} else if (this.FindPos(rect) == 0xffff) {
				this.EnsureCapacity(this._count + 1);
				this._elements[this._count++] = obj;
				this._contentBBoxValid = false;
				// NOTICE: break ignore!!!
			} else {

				return;
			}
		}

	}

	private void NodeAddList(java.lang.Object[] l, Integer count) {
		for (Integer i = 0; i < count; i++) {
			java.lang.Object obj2 = l[i];
			InternalRect rect = this._set.BoundingBox(obj2);
			if (this.FindPos(rect) == 5) {
				if (this._topR == null) {
					this._topR = new IlvGenericQuadtree(this._set);
					this._topR._x = this._x + (this._width / 2.0);
					this._topR._y = this._y;
					this._topR._width = (this._x + this._width) - this._topR._x;
					this._topR._height = this._height / 2.0;
				}
				this._topR.NodeAdd(obj2, rect);
				// NOTICE: break ignore!!!
			} else if (this.FindPos(rect) == 6) {
				if (this._topL == null) {
					this._topL = new IlvGenericQuadtree(this._set);
					this._topL._x = this._x;
					this._topL._y = this._y;
					this._topL._width = this._width / 2.0;
					this._topL._height = this._height / 2.0;
				}
				this._topL.NodeAdd(obj2, rect);
				// NOTICE: break ignore!!!
			} else if (this.FindPos(rect) == 9) {
				if (this._bottomR == null) {
					this._bottomR = new IlvGenericQuadtree(this._set);
					this._bottomR._x = this._x + (this._width / 2.0);
					this._bottomR._y = this._y + (this._height / 2.0);
					this._bottomR._width = (this._x + this._width)
							- this._bottomR._x;
					this._bottomR._height = (this._y + this._height)
							- this._bottomR._y;
				}
				this._bottomR.NodeAdd(obj2, rect);
				// NOTICE: break ignore!!!
			} else if (this.FindPos(rect) == 10) {
				if (this._bottomL == null) {
					this._bottomL = new IlvGenericQuadtree(this._set);
					this._bottomL._x = this._x;
					this._bottomL._y = this._y + (this._height / 2.0);
					this._bottomL._width = this._width / 2.0;
					this._bottomL._height = (this._y + this._height)
							- this._bottomL._y;
				}
				this._bottomL.NodeAdd(obj2, rect);
				// NOTICE: break ignore!!!
			} else if (this.FindPos(rect) == 0xffff) {
				this.EnsureCapacity(this._count + 1);
				this._elements[this._count++] = obj2;
				this._contentBBoxValid = false;
				// NOTICE: break ignore!!!
			}
		}

	}

	public IlvGenericQuadtree NodeInsideP(java.lang.Object obj,
			InternalRect rect) {

		if (this.Intersects(rect)) {
			if (!this._divided) {

				if (!this.Contains(obj)) {

					return null;
				}

				return this;
			}
			if (this.FindPos(rect) == 5) {
				if (this._topR != null) {

					return this._topR.NodeInsideP(obj, rect);
				}

				return null;
			} else if (this.FindPos(rect) == 6) {
				if (this._topL != null) {

					return this._topL.NodeInsideP(obj, rect);
				}

				return null;
			} else if (this.FindPos(rect) == 9) {
				if (this._bottomR != null) {

					return this._bottomR.NodeInsideP(obj, rect);
				}

				return null;
			} else if (this.FindPos(rect) == 10) {
				if (this._bottomL != null) {

					return this._bottomL.NodeInsideP(obj, rect);
				}

				return null;
			} else if (this.FindPos(rect) == 0xffff) {

				if (!this.Contains(obj)) {

					return null;
				}

				return this;
			}
		}

		return null;

	}

	private Boolean NodeRemove(java.lang.Object obj, InternalRect rect) {
		this._nbelements--;
		Boolean flag = false;
		if (!this._divided) {
			if (this._elements == null) {

				return false;
			}

			return this.RemoveElement(obj);
		}
		if (this.FindPos(rect) == 5) {
			if (this._topR != null) {

				flag = this._topR.NodeRemove(obj, rect);
				if (this._topR._nbelements == 0) {
					this._topR = null;
				}
			}
			// NOTICE: break ignore!!!
		} else if (this.FindPos(rect) == 6) {
			if (this._topL != null) {

				flag = this._topL.NodeRemove(obj, rect);
				if (this._topL._nbelements == 0) {
					this._topL = null;
				}
			}
			// NOTICE: break ignore!!!
		} else if (this.FindPos(rect) == 9) {
			if (this._bottomR != null) {

				flag = this._bottomR.NodeRemove(obj, rect);
				if (this._bottomR._nbelements == 0) {
					this._bottomR = null;
				}
			}
			// NOTICE: break ignore!!!
		} else if (this.FindPos(rect) == 10) {
			if (this._bottomL != null) {

				flag = this._bottomL.NodeRemove(obj, rect);
				if (this._bottomL._nbelements == 0) {
					this._bottomL = null;
				}
			}
			// NOTICE: break ignore!!!
		} else if (this.FindPos(rect) == 0xffff) {
			if (this._elements == null) {
				flag = false;
			} else {

				flag = this.RemoveElement(obj);
			}
			// NOTICE: break ignore!!!
		}
		if (((this._topL == null) && (this._topR == null))
				&& ((this._bottomL == null) && (this._bottomR == null))) {
			this._divided = false;
		}

		return flag;

	}

	public Boolean Remove(java.lang.Object obj) {
		InternalRect rect = this._set.BoundingBox(obj);
		if (!OptimizeMode && (this.NodeInsideP(obj, rect) == null)) {
			throw (new system.Exception(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] {
									"IlvGenericQuadtree.remove: ", obj, " [",
									rect, "] not in quadtree" })));
		}

		return this.NodeRemove(obj, rect);

	}

	private Boolean RemoveElement(java.lang.Object obj) {
		if (this._elements != null) {
			for (Integer i = 0; i < this._count; i++) {
				if (obj == this._elements[i]) {
					Integer length = (this._count - i) - 1;
					if (length > 0) {
						clr.System.ArrayStaticWrapper.Copy(this._elements,
								i + 1, this._elements, i, length);
					}
					this._count--;
					this._elements[this._count] = null;
					this._contentBBoxValid = false;

					return true;
				}
			}
		}

		return false;

	}

	public static void SetOptimizeMode(Boolean s) {
		OptimizeMode = s;

	}

	private IlvGenericQuadtree TreeFromPosition(Integer pos) {
		if (pos == 5) {

			return this._topR;
		} else if (pos == 6) {

			return this._topL;
		} else if (pos == 9) {

			return this._bottomR;
		} else if (pos == 10) {

			return this._bottomL;
		}

		return null;

	}

}
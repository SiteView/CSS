package ILOG.Diagrammer.GraphLayout.Internal.GenericQuadtree;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Collections.*;

public final class GenericIndexedSet {
	private InternalRect _bbox;

	private Boolean _bboxInvalid = false;

	public ArrayList _list;

	private Integer _maxInList;

	private Integer _maxInNode;

	private IGenericQuadtreeObject _objectInterface;

	private IlvGenericQuadtree _quadtree;

	private Boolean _useQuadtree = false;

	public Integer MaxObjectsInList = 20;

	public GenericIndexedSet() {
		this(20, 20, null);
	}

	public GenericIndexedSet(Integer maxInNode, Integer maxInList,
			IGenericQuadtreeObject objectInterface) {
		this._maxInList = 20;
		this._maxInNode = 20;
		this._list = new ArrayList(0x3e8);
		this._bbox = new InternalRect(0f, 0f, 0f, 0f);
		this._bboxInvalid = true;
		this._maxInNode = maxInNode;
		this._maxInList = maxInList;
		if (objectInterface == null) {
			throw (new ArgumentException("object interface cannot be null"));
		}
		this._objectInterface = objectInterface;
	}

	public void AddObject(java.lang.Object obj) {
		this.BboxChanged(obj);
		this._list.Add(obj);
		if (this._useQuadtree) {
			this._quadtree.Add(obj);
		}
		if (this._list.get_Count() == this._maxInList) {
			this.Divide();
		}

	}

	private void BboxChanged(java.lang.Object obj) {
		if (!this._bboxInvalid) {
			InternalRect rect = this.BoundingBox(obj);
			if (((rect.X <= this._bbox.X) || (rect.Y <= this._bbox.Y))
					|| (((rect.X + rect.Width) >= (this._bbox.X + this._bbox.Width)) || ((rect.Y + rect.Height) >= (this._bbox.Y + this._bbox.Height)))) {
				this._bboxInvalid = true;
			}
		}

	}

	public InternalRect BoundingBox(java.lang.Object obj) {

		return this._objectInterface.BoundingBox(obj);

	}

	public InternalRect ComputeBBox() {
		if (this._bboxInvalid) {
			this._bbox.Reshape(0f, 0f, 0f, 0f);
			if (this._useQuadtree) {
				this._quadtree.ComputeBBox(this._bbox);
			} else {
				this.Map(new AnonClass_1(this), this._bbox);
			}
			this._bboxInvalid = false;
		}

		return new InternalRect(this._bbox.X, this._bbox.Y, this._bbox.Width,
				this._bbox.Height);

	}

	public Boolean Contains(java.lang.Object obj, InternalPoint p) {

		return this.BoundingBox(obj).Inside(p.X, p.Y);

	}

	public void DeleteAll() {
		this._bboxInvalid = true;
		if (this._useQuadtree) {
			this._quadtree.DeleteAll();
		}
		this._useQuadtree = false;
		this._quadtree = null;
		this._list.Clear();

	}

	private void Divide() {
		this._quadtree = new IlvGenericQuadtree(this, null);
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this._list);

		while (enumerator.HasMoreElements()) {
			this._quadtree.Add(enumerator.NextElement());
		}
		this._useQuadtree = true;

	}

	public Integer GetCardinal() {

		return this._list.get_Count();

	}

	public IJavaStyleEnumerator GetElements() {

		return TranslateUtil.Collection2JavaStyleEnum(this._list);

	}

	public Integer GetMaxInNode() {

		return this._maxInNode;

	}

	public java.lang.Object GetObject(InternalPoint p) {
		if (this._useQuadtree) {

			return this._quadtree.LastContains(p);
		}
		if (this._list.get_Count() != 0) {
			for (Integer i = this._list.get_Count() - 1; i >= 0; i--) {
				java.lang.Object obj2 = this._list.get_Item(i);

				if (this.Contains(obj2, p)) {

					return obj2;
				}
			}
		}

		return null;

	}

	public Boolean Inside(java.lang.Object obj, InternalRect rect) {
		InternalRect rect2 = this.BoundingBox(obj);

		return rect.Contains(rect2);

	}

	public Boolean Intersects(java.lang.Object obj, InternalRect rect) {

		return this.BoundingBox(obj).Intersects(rect);

	}

	public void Map(GenericApplyObject f, java.lang.Object arg) {
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this._list);

		while (enumerator.HasMoreElements()) {
			f.Apply(enumerator.NextElement(), arg);
		}

	}

	public void MapInside(InternalRect rect, GenericApplyObject f,
			java.lang.Object arg) {
		if (this._useQuadtree) {
			this._quadtree.ApplyInside(rect, f, arg);
		} else {
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(this._list);

			while (enumerator.HasMoreElements()) {
				java.lang.Object obj2 = enumerator.NextElement();

				if (this.Inside(obj2, rect)) {
					f.Apply(obj2, arg);
				}
			}
		}

	}

	public void MapIntersects(InternalRect rect, GenericApplyObject f,
			java.lang.Object arg) {
		if (this._useQuadtree) {
			this._quadtree.ApplyIntersect(rect, f, arg);
		} else {
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(this._list);

			while (enumerator.HasMoreElements()) {
				java.lang.Object obj2 = enumerator.NextElement();

				if (this.Intersects(obj2, rect)) {
					f.Apply(obj2, arg);
				}
			}
		}

	}

	public void QuadtreeAdd(java.lang.Object obj) {
		if (this._useQuadtree) {
			this._quadtree.Add(obj);
		}
		this.BboxChanged(obj);

	}

	public void QuadtreeRemove(java.lang.Object obj) {
		if (this._useQuadtree) {
			this._quadtree.Remove(obj);
		}
		this.BboxChanged(obj);

	}

	public void RemoveObject(java.lang.Object obj) {
		Boolean flag = true;
		this.BboxChanged(obj);
		if (this._useQuadtree) {

			flag = this._quadtree.Remove(obj);
		}
		if (flag) {

			flag = TranslateUtil.Remove(this._list, obj);
		}
		if (!flag) {
			throw (new ArgumentException("Object not removed"));
		}

	}

	private class AnonClass_1 implements GenericApplyObject {
		private GenericIndexedSet __outerThis;

		public AnonClass_1(GenericIndexedSet input__outerThis) {
			this.__outerThis = input__outerThis;
		}

		public void Apply(java.lang.Object obj, java.lang.Object arg) {
			InternalRect rect = (InternalRect) arg;
			InternalRect rect2 = this.__outerThis.BoundingBox(obj);

			if (rect.IsEmpty()) {
				rect.Reshape(rect2.X, rect2.Y, rect2.Width, rect2.Height);
			} else {
				rect.Add(rect2);
			}

		}

	}
}
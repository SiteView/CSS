package ILOG.Diagrammer.GraphLayout.Internal;

import system.*;

public class ListOfIntegers {
	public Integer begin;

	public Integer beginIntersBigSmall;

	public Integer beginIntersSmallBig;

	public Integer end = -1;

	public Integer endIntersBigSmall;

	public Integer endIntersSmallBig;

	public Integer NOT_PRESENT = -317;

	public Integer[] vect;

	public ListOfIntegers(Integer n) {
		this.vect = new Integer[n];
	}

	public void Append(Integer n) {
		this.vect[++this.end] = n;

	}

	public void Clear() {
		this.begin = 0;
		this.end = -1;

	}

	public void DeleteAll() {
		this.begin = 0;
		this.end = -1;

	}

	public ListOfIntegers Duplicate() {
		ListOfIntegers integers = new ListOfIntegers(this.vect.length);
		integers.begin = this.begin;
		integers.end = this.end;
		clr.System.ArrayStaticWrapper.Copy(this.vect, 0, integers.vect, 0,
				this.vect.length);

		return integers;

	}

	public Integer Get(Integer i) {
		if (i < this.vect.length) {

			return this.vect[i];
		}

		return -317;

	}

	public Integer GetBegin() {

		return this.begin;

	}

	public Integer GetBeginIntersBigSmall() {

		return this.beginIntersBigSmall;

	}

	public Integer GetBeginIntersSmallBig() {

		return this.beginIntersSmallBig;

	}

	public Integer GetEnd() {

		return this.end;

	}

	public Integer GetEndIntersBigSmall() {

		return this.endIntersBigSmall;

	}

	public Integer GetEndIntersSmallBig() {

		return this.endIntersSmallBig;

	}

	public Integer GetFirst() {

		if (this.IsEmpty()) {
			throw (new system.Exception("empty path"));
		}

		return this.vect[this.begin];

	}

	public Integer GetFirstIndex(Integer n) {
		for (Integer i = this.begin; i <= this.end; i++) {
			if (this.vect[i] == n) {

				return i;
			}
		}

		return -317;

	}

	public Boolean IsEmpty() {
		if (this.end >= 0) {

			return ((this.end - this.begin) < 0);
		}

		return true;

	}

	public Integer Length() {

		return ((this.end - this.begin) + 1);

	}

	public void Set(Integer i, Integer n) {
		this.vect[i] = n;

	}

	public void SetBeginIntersBigSmall(Integer beg) {
		this.beginIntersBigSmall = beg;

	}

	public void SetBeginIntersSmallBig(Integer beg) {
		this.beginIntersSmallBig = beg;

	}

	public void SetEndIntersBigSmall(Integer end) {
		this.endIntersBigSmall = end;

	}

	public void SetEndIntersSmallBig(Integer end) {
		this.endIntersSmallBig = end;

	}

}
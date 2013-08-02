package ILOG.Diagrammer.GraphLayout.Internal;

import system.*;

public final class FixedPoint extends InternalPoint {
	public FixedPoint() {
		super(0f, 0f);
	}

	public FixedPoint(InternalPoint p) {
		super(p.X, p.Y);
	}

	public FixedPoint(float x, float y) {
		super(x, y);
	}

}
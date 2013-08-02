package ILOG.Diagrammer;

public class Size2D {
	public static Size2D Empty;

	private float width = 0;

	private float height = 0;

	public Size2D(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public Size2D(Point2D pt) {
		this.width = pt.get_X();
		this.height = pt.get_Y();
	}

	public Size2D(Size2D size) {
		this.width = size.width;
		this.height = size.height;
	}

	public Size2D() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Size2D)) {

			return false;
		}
		Size2D sized = (Size2D) obj;

		return ((sized.get_Width() == this.get_Width()) && (sized.get_Height() == this
				.get_Height()));

	}

	@Override
	public int hashCode() {

		return super.hashCode();

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
															 *//*
																 * TODO: #
																 * TrackedVisitOperatorDeclaration
																 * #
																 *//*
																	 * TODO: #
																	 * TrackedVisitOperatorDeclaration
																	 * #
																	 *//*
																		 * TODO:
																		 * #
																		 * TrackedVisitOperatorDeclaration
																		 * #
																		 */
	public Point2D ToPoint2D() {

		Point2D pd = new Point2D(this.width, this.height);
		return pd;

	}

	@Override
	public String toString() {

		return "{Width=" + this.width + ", Height=" + this.height + "}";

	}

	public float get_Height() {

		return this.height;
	}

	public void set_Height(float value) {
		this.height = value;
	}

	public Boolean get_IsEmpty() {

		return ((this.width == 0f) && (this.height == 0f));
	}

	public float get_Width() {

		return this.width;
	}

	public void set_Width(float value) {
		this.width = value;
	}

	static {
		Empty = new Size2D();
	}

}
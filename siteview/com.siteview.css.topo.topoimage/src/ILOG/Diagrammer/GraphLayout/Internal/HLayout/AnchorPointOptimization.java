package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class AnchorPointOptimization extends HGraphAlgorithm {
	private float[] _actAnchors;

	private float _epsilon = 1E-05f;

	private float[] _minDist;

	private Integer _numberOfSegments;

	private Integer[] _portNumbers;

	private HSegment[] _segments;

	private float[] _wantedAnchors;

	public AnchorPointOptimization(HGraph graph) {
		this.Init(graph);
	}

	@Override
	public void Clean() {
		this._segments = null;
		this._actAnchors = null;
		this._wantedAnchors = null;
		this._minDist = null;

	}

	public void Init(HSegmentIterator iter, Integer numberOfSegments,
			Boolean fromSide) {
		this._numberOfSegments = numberOfSegments;
		if (this._numberOfSegments != 0) {
			Integer index = 0;
			Integer levelFlow = super.GetGraph().GetLevelFlow();
			float minDistBetweenLinks = super.GetGraph()
					.GetMinDistBetweenLinks(levelFlow);
			if (minDistBetweenLinks <= 0f) {
				this._epsilon = 0f;
			}
			this._segments = new HSegment[numberOfSegments];
			this._actAnchors = new float[numberOfSegments];
			this._wantedAnchors = new float[numberOfSegments];
			this._minDist = new float[numberOfSegments];
			this._portNumbers = new Integer[numberOfSegments];

			while (iter.HasNext()) {
				HSegment segment = iter.Next();
				float thickness = segment.GetOwnerLink().GetThickness();
				this._segments[index] = segment;
				this._portNumbers[index] = 0;
				if (fromSide) {

					this._actAnchors[index] = segment.GetFromCoord(levelFlow);
				} else {

					this._actAnchors[index] = segment.GetToCoord(levelFlow);
				}

				this._wantedAnchors[index] = segment.GetWantedAnchorCoord();
				this._minDist[index] = 0.5f * (thickness + minDistBetweenLinks);
				index++;
			}
		}

	}

	public void Init(HSegmentIterator fromSegIter, HSegmentIterator toSegIter,
			Integer numberOfSegments, Boolean fromSide) {
		this._numberOfSegments = numberOfSegments;
		if (this._numberOfSegments != 0) {
			HSegment segment2 = null;
			HSegment segment3 = null;
			Integer index = 0;
			Integer levelFlow = super.GetGraph().GetLevelFlow();
			float minDistBetweenLinks = super.GetGraph()
					.GetMinDistBetweenLinks(levelFlow);
			if (minDistBetweenLinks <= 0f) {
				this._epsilon = 0f;
			}
			this._segments = new HSegment[numberOfSegments];
			this._portNumbers = new Integer[numberOfSegments];
			this._actAnchors = new float[numberOfSegments];
			this._wantedAnchors = new float[numberOfSegments];
			this._minDist = new float[numberOfSegments];

			if (fromSegIter.HasNext()) {

				segment2 = fromSegIter.Next();
			} else {
				segment2 = null;
			}

			if (toSegIter.HasNext()) {

				segment3 = toSegIter.Next();
			} else {
				segment3 = null;
			}
			while ((segment2 != null) || (segment3 != null)) {
				HSegment segment = null;
				float fromCoord = 0;
				Integer fromPortNumber = null;
				if (segment2 == null) {
					segment = segment3;
				} else if (segment3 == null) {
					segment = segment2;
				} else if (segment2.GetFromCoord(levelFlow) < segment3
						.GetToCoord(levelFlow)) {
					segment = segment2;
				} else {
					segment = segment3;
				}
				if (segment == segment2) {

					fromCoord = segment.GetFromCoord(levelFlow);

					if (fromSegIter.HasNext()) {

						segment2 = fromSegIter.Next();
					} else {
						segment2 = null;
					}

					fromPortNumber = segment.GetFromPortNumber();
					if ((fromPortNumber == -1) && !fromSide) {
						continue;
					}
					this._portNumbers[index] = fromPortNumber - -1;
				} else {

					fromCoord = segment.GetToCoord(levelFlow);

					if (toSegIter.HasNext()) {

						segment3 = toSegIter.Next();
					} else {
						segment3 = null;
					}

					fromPortNumber = segment.GetToPortNumber();
					if ((fromPortNumber == -1) && fromSide) {
						continue;
					}
					this._portNumbers[index] = -fromPortNumber + -1;
				}
				float thickness = segment.GetOwnerLink().GetThickness();
				this._segments[index] = segment;
				this._actAnchors[index] = fromCoord;

				this._wantedAnchors[index] = segment.GetWantedAnchorCoord();
				this._minDist[index] = 0.5f * (thickness + minDistBetweenLinks);
				index++;
			}
			this._numberOfSegments = index;
		}

	}

	public Boolean IsBlocked(Integer i, Integer j) {
		if ((this._portNumbers[i] * this._portNumbers[j]) >= 0) {

			return true;
		}
		if (this._portNumbers[i] < 0) {
			if (this._portNumbers[j] <= -this._portNumbers[i]) {

				return true;
			}
		} else if (this._portNumbers[i] <= -this._portNumbers[j]) {

			return true;
		}

		return false;

	}

	@Override
	public void Run() {
		if (this._numberOfSegments != 0) {
			Integer num = null;
			Integer index = this._numberOfSegments - 1;
			Boolean flag = true;
			float num4 = 1f - this._epsilon;
			if (this._wantedAnchors[0] < this._actAnchors[0]) {
				this._actAnchors[0] = this._wantedAnchors[0];
			}
			if (this._wantedAnchors[index] > this._actAnchors[index]) {
				this._actAnchors[index] = this._wantedAnchors[index];
			}
			while (flag) {
				Integer num2 = null;
				flag = false;
				num = 1;
				while (num <= index) {
					if (this._wantedAnchors[num] < this._actAnchors[num]) {
						num2 = num - 1;

						while ((num2 >= 0) && !this.IsBlocked(num, num2)) {
							num2--;
						}
						if ((num2 < 0)
								|| ((this._wantedAnchors[num] - this._actAnchors[num2]) >= (num4 * (this._minDist[num] + this._minDist[num2])))) {
							this._actAnchors[num] = this._wantedAnchors[num];
							flag = true;
						}
					}
					num++;
				}
				num = index - 1;
				while (num >= 0) {
					if (this._wantedAnchors[num] > this._actAnchors[num]) {
						num2 = num + 1;

						while ((num2 < index) && !this.IsBlocked(num, num2)) {
							num2++;
						}
						if ((num2 > index)
								|| ((this._actAnchors[num2] - this._wantedAnchors[num]) >= (num4 * (this._minDist[num] + this._minDist[num2])))) {
							this._actAnchors[num] = this._wantedAnchors[num];
							flag = true;
						}
					}
					num--;
				}
			}
			for (num = 0; num < this._numberOfSegments; num++) {
				this._segments[num].SetWantedAnchorCoord(this._actAnchors[num]);
			}
		}

	}

}
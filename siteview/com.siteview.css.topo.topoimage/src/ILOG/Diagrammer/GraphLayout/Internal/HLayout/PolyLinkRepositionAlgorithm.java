package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class PolyLinkRepositionAlgorithm extends HLevelAlgorithm {
	private float _middleBetweenLevels;

	private float _minShiftOffset;

	private Boolean[] _obstacleIsSegmentLowerLevel;

	private Boolean[] _obstacleIsSegmentUpperLevel;

	private float[] _obstacleXCoordLowerLevel;

	private float[] _obstacleXCoordUpperLevel;

	private float[] _obstacleYCoordLowerLevel;

	private float[] _obstacleYCoordUpperLevel;

	private HSegment[] _segments;

	private Integer[] _segmentsLowerLevelIndex;

	private Integer[] _segmentsUpperLevelIndex;

	public PolyLinkRepositionAlgorithm() {
	}

	@Override
	public void Clean() {
		super.Clean();
		this._segments = null;
		this._obstacleXCoordUpperLevel = null;
		this._obstacleYCoordUpperLevel = null;
		this._obstacleXCoordLowerLevel = null;
		this._obstacleYCoordLowerLevel = null;
		this._obstacleIsSegmentUpperLevel = null;
		this._obstacleIsSegmentLowerLevel = null;
		this._segmentsUpperLevelIndex = null;
		this._segmentsLowerLevelIndex = null;

	}

	private Boolean CorrectForkLinks() {
		Boolean flag = false;

		flag |= this.CorrectForkLinks(this._obstacleXCoordUpperLevel,
				this._obstacleYCoordUpperLevel,
				this._obstacleIsSegmentUpperLevel, true);

		return (flag | this.CorrectForkLinks(this._obstacleXCoordLowerLevel,
				this._obstacleYCoordLowerLevel,
				this._obstacleIsSegmentLowerLevel, false));

	}

	private Boolean CorrectForkLinks(float[] xarray, float[] yarray,
			Boolean[] isSegment, Boolean increase) {
		if (xarray.length == 0) {

			return false;
		}
		Boolean flag = false;
		float num = xarray[0] - 1f;
		Integer num3 = 0;
		Integer num4 = 0;
		for (Integer i = 0; i < xarray.length; i++) {
			if (xarray[i] == num) {
				num4 = i;
			} else {
				if (num4 > num3) {
					float minValue = 0;
					if (increase) {
						minValue = Float.MIN_VALUE;
					} else {
						minValue = Float.MAX_VALUE;
					}
					Integer index = num3;
					while (index <= num4) {
						if (isSegment[index]) {
							if ((minValue < yarray[index]) && increase) {
								minValue = yarray[index];
							} else if ((minValue > yarray[index]) && !increase) {
								minValue = yarray[index];
							}
						}
						index++;
					}
					for (index = num3; index <= num4; index++) {
						if (isSegment[index]) {
							flag |= yarray[index] != minValue;
							yarray[index] = minValue;
						}
					}
				}
				num3 = num4 = i;
				num = xarray[i];
			}
		}

		return flag;

	}

	private void CreateAllBends() {
		for (Integer i = 0; i < this._segments.length; i++) {
			this.CreateBends(this._segments[i]);
		}

	}

	private void CreateBends(HSegment segment) {

		if (!segment.IsGradientZeroInFlow()) {
			Boolean flag = false;
			Integer upperLevelIndex = this.GetUpperLevelIndex(segment);
			Integer lowerLevelIndex = this.GetLowerLevelIndex(segment);
			Integer edgeFlow = super.GetGraph().GetEdgeFlow();
			Integer index = 1 - edgeFlow;
			HLink ownerLink = segment.GetOwnerLink();
			HNode node = null;
			float upperLevelYCoord = this.GetUpperLevelYCoord(segment);
			float lowerLevelYCoord = this.GetLowerLevelYCoord(segment);

			if (((upperLevelIndex >= 0) && (upperLevelYCoord > segment
					.GetFromCoord(edgeFlow)))
					&& !segment.GetFrom().IsUpperBypassNode()) {

				if (!segment.GetFrom().IsDummyNode()) {
					flag = true;
				}

				node = ownerLink.AddDummyNode(segment);
				node.SetCoord(index,
						this._obstacleXCoordUpperLevel[upperLevelIndex]);
				node.SetCoord(edgeFlow,
						this._obstacleYCoordUpperLevel[upperLevelIndex]);

				if (!segment.IsReversed()) {

					segment = node.GetFirstSegmentFrom();
				}
			}

			if (((lowerLevelIndex >= 0) && (lowerLevelYCoord < segment
					.GetToCoord(edgeFlow)))
					&& !segment.GetTo().IsLowerBypassNode()) {

				if (!segment.GetTo().IsDummyNode()) {
					flag = true;
				}

				node = ownerLink.AddDummyNode(segment);
				node.SetCoord(index,
						this._obstacleXCoordLowerLevel[lowerLevelIndex]);
				node.SetCoord(edgeFlow,
						this._obstacleYCoordLowerLevel[lowerLevelIndex]);
			}
			if ((super.GetGraph().GetConnectorStyle() == 100) && flag) {
				CalcConnectors connectorAlgorithm = super.GetGraph()
						.GetConnectorAlgorithm();
				connectorAlgorithm.CalcFromClippedConnector(ownerLink);
				connectorAlgorithm.CalcToClippedConnector(ownerLink);
			}
		}

	}

	private void FillLowerLevelObstacleTable() {
		Integer edgeFlow = super.GetGraph().GetEdgeFlow();
		Integer index = 1 - edgeFlow;
		HNodeIterator nodes = super.GetLowerLevel().GetNodes();
		Integer num4 = 0;

		while (nodes.HasNext()) {
			HNode node = nodes.Next();

			if (!node.IsEastWestPortAuxNode()) {

				this._obstacleXCoordLowerLevel[num4] = node.GetCoord(index);

				this._obstacleYCoordLowerLevel[num4] = node.GetCoord(edgeFlow);
				this._obstacleIsSegmentLowerLevel[num4] = false;
				num4++;
			}
			HSegmentIterator segmentsTo = node.GetSegmentsTo();

			while (segmentsTo.HasNext()) {
				HSegment segment = segmentsTo.Next();
				Integer sortValue = (int) segment.GetSortValue();
				if (sortValue >= 0) {
					this._segmentsLowerLevelIndex[sortValue] = num4;

					this._obstacleXCoordLowerLevel[num4] = segment
							.GetToCoord(index);

					this._obstacleYCoordLowerLevel[num4] = segment
							.GetToCoord(edgeFlow);
					this._obstacleIsSegmentLowerLevel[num4] = true;
					num4++;
				}
			}

			if (!node.IsEastWestPortAuxNode()) {
				this._obstacleXCoordLowerLevel[num4] = node.GetCoord(index)
						+ node.GetSize(index);

				this._obstacleYCoordLowerLevel[num4] = node.GetCoord(edgeFlow);
				this._obstacleIsSegmentLowerLevel[num4] = false;
				num4++;
			}
		}

	}

	private void FillUpperLevelObstacleTable() {
		Integer edgeFlow = super.GetGraph().GetEdgeFlow();
		Integer index = 1 - edgeFlow;
		HNodeIterator nodes = super.GetUpperLevel().GetNodes();
		Integer num4 = 0;

		while (nodes.HasNext()) {
			HNode node = nodes.Next();

			if (!node.IsEastWestPortAuxNode()) {

				this._obstacleXCoordUpperLevel[num4] = node.GetCoord(index);
				this._obstacleYCoordUpperLevel[num4] = node.GetCoord(edgeFlow)
						+ node.GetSize(edgeFlow);
				this._obstacleIsSegmentUpperLevel[num4] = false;
				num4++;
			}
			HSegmentIterator segmentsFrom = node.GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {
				HSegment segment = segmentsFrom.Next();
				Integer sortValue = (int) segment.GetSortValue();
				if (sortValue >= 0) {
					this._segmentsUpperLevelIndex[sortValue] = num4;

					this._obstacleXCoordUpperLevel[num4] = segment
							.GetFromCoord(index);

					this._obstacleYCoordUpperLevel[num4] = segment
							.GetFromCoord(edgeFlow);
					this._obstacleIsSegmentUpperLevel[num4] = true;
					num4++;
				}
			}

			if (!node.IsEastWestPortAuxNode()) {
				this._obstacleXCoordUpperLevel[num4] = node.GetCoord(index)
						+ node.GetSize(index);
				this._obstacleYCoordUpperLevel[num4] = node.GetCoord(edgeFlow)
						+ node.GetSize(edgeFlow);
				this._obstacleIsSegmentUpperLevel[num4] = false;
				num4++;
			}
		}

	}

	private void GenerateLowerLevelObstacleTable() {
		Integer num = 2 * super.GetLowerLevel().GetNodesCount();
		HSegmentIterator segmentsTo = super.GetLowerLevel().GetSegmentsTo();

		while (segmentsTo.HasNext()) {
			if (segmentsTo.Next().GetSortValue() >= 0f) {
				num++;
			}
		}
		this._obstacleXCoordLowerLevel = new float[num];
		this._obstacleYCoordLowerLevel = new float[num];
		this._obstacleIsSegmentLowerLevel = new Boolean[num];
		this.FillLowerLevelObstacleTable();

	}

	public void GenerateSegmentIndexe() {
		HSegment segment = null;
		HSegmentIterator segmentsFrom = super.GetUpperLevel().GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();

			if (segment.IsGradientZeroInFlow()) {
				segment.SetSortValue(-1f);
			} else {
				segment.SetSortValue(1f);
			}
		}

		segmentsFrom = super.GetLowerLevel().GetSegmentsTo();

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();

			if (segment.IsGradientZeroInFlow()) {
				segment.SetSortValue(-1f);
			} else {
				segment.SetSortValue(1f);
			}
		}
		Integer index = 0;

		segmentsFrom = super.GetUpperLevel().GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();
			if (segment.GetSortValue() >= 0f) {
				index++;
				segment.SetSortValue(-1f);
			}
		}

		segmentsFrom = super.GetLowerLevel().GetSegmentsTo();

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();
			if (segment.GetSortValue() >= 0f) {
				index++;
				segment.SetSortValue(-1f);
			}
		}
		this._segmentsUpperLevelIndex = new Integer[index];
		this._segmentsLowerLevelIndex = new Integer[index];
		this._segments = new HSegment[index];
		for (index = 0; index < this._segmentsUpperLevelIndex.length; index++) {
			this._segmentsUpperLevelIndex[index] = this._segmentsLowerLevelIndex[index] = -1;
		}
		index = 0;

		segmentsFrom = super.GetUpperLevel().GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();

			if ((segment.GetSortValue() < 0f)
					&& !segment.IsGradientZeroInFlow()) {
				segment.SetSortValue((float) index);
				this._segments[index] = segment;
				index++;
			}
		}

		segmentsFrom = super.GetLowerLevel().GetSegmentsTo();

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();

			if ((segment.GetSortValue() < 0f)
					&& !segment.IsGradientZeroInFlow()) {
				segment.SetSortValue((float) index);
				this._segments[index] = segment;
				index++;
			}
		}

	}

	private void GenerateUpperLevelObstacleTable() {
		Integer num = 2 * super.GetUpperLevel().GetNodesCount();
		HSegmentIterator segmentsFrom = super.GetUpperLevel().GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {
			if (segmentsFrom.Next().GetSortValue() >= 0f) {
				num++;
			}
		}
		this._obstacleXCoordUpperLevel = new float[num];
		this._obstacleYCoordUpperLevel = new float[num];
		this._obstacleIsSegmentUpperLevel = new Boolean[num];
		this.FillUpperLevelObstacleTable();

	}

	private Integer GetLowerLevelIndex(HSegment segment) {
		Integer sortValue = (int) segment.GetSortValue();
		if ((sortValue >= 0)
				&& (sortValue <= this._segmentsLowerLevelIndex.length)) {

			return this._segmentsLowerLevelIndex[sortValue];
		}

		return -1;

	}

	private float GetLowerLevelYCoord(HSegment segment) {

		if (segment.IsGradientZeroInFlow()) {

			return 0f;
		}
		Integer lowerLevelIndex = this.GetLowerLevelIndex(segment);
		if (lowerLevelIndex < 0) {

			return 0f;
		}

		return this._obstacleYCoordLowerLevel[lowerLevelIndex];

	}

	private Integer GetUpperLevelIndex(HSegment segment) {
		Integer sortValue = (int) segment.GetSortValue();
		if ((sortValue >= 0)
				&& (sortValue <= this._segmentsUpperLevelIndex.length)) {

			return this._segmentsUpperLevelIndex[sortValue];
		}

		return -1;

	}

	private float GetUpperLevelYCoord(HSegment segment) {

		if (segment.IsGradientZeroInFlow()) {

			return 0f;
		}
		Integer upperLevelIndex = this.GetUpperLevelIndex(segment);
		if (upperLevelIndex < 0) {

			return 0f;
		}

		return this._obstacleYCoordUpperLevel[upperLevelIndex];

	}

	@Override
	public void Init(HLevel upperLevel, HLevel lowerLevel) {
		super.Init(upperLevel, lowerLevel);
		Integer edgeFlow = super.GetGraph().GetEdgeFlow();
		this._minShiftOffset = 2f;
		this._middleBetweenLevels = 0.5f * ((upperLevel.GetCoord(edgeFlow) + upperLevel
				.GetSize(edgeFlow)) + lowerLevel.GetCoord(edgeFlow));

	}

	private Boolean ResolveConflicts() {
		Boolean flag = false;
		float num = this._minShiftOffset;
		for (Integer i = 0; i < this._segments.length; i++) {

			flag |= this.ResolveConflictsAtUpperLevel(this._segments[i]);

			flag |= this.ResolveConflictsAtLowerLevel(this._segments[i]);
			if (this._minShiftOffset < num) {

				return flag;
			}
		}

		return flag;

	}

	private Boolean ResolveConflictsAtLowerLevel(HSegment segment) {

		if (segment.IsGradientZeroInFlow()) {

			return false;
		}
		Integer lowerLevelIndex = this.GetLowerLevelIndex(segment);
		if (lowerLevelIndex < 0) {

			return false;
		}
		Integer edgeFlow = super.GetGraph().GetEdgeFlow();
		Integer index = 1 - edgeFlow;
		float num4 = this._obstacleYCoordLowerLevel[lowerLevelIndex];
		float toCoord = segment.GetToCoord(index);
		float num6 = segment.GetToCoord(edgeFlow);
		float fromCoord = segment.GetFromCoord(index);
		float num8 = segment.GetFromCoord(edgeFlow);
		Integer upperLevelIndex = this.GetUpperLevelIndex(segment);
		if (upperLevelIndex >= 0) {
			num8 = this._obstacleYCoordUpperLevel[upperLevelIndex];
		}
		Boolean flag = fromCoord > toCoord;
		Integer num10 = lowerLevelIndex;

		do {
			if (flag) {
				num10++;
				if ((num10 < this._obstacleYCoordLowerLevel.length)
						&& (this._obstacleXCoordLowerLevel[num10] < fromCoord)) {

				} else
					return (this._obstacleYCoordLowerLevel[lowerLevelIndex] != num4);
			} else {
				num10--;
				if ((num10 < 0)
						|| (this._obstacleXCoordLowerLevel[num10] <= fromCoord)) {
					return (this._obstacleYCoordLowerLevel[lowerLevelIndex] != num4);
				}
			}

		} while (!this.ResolveLowerLevelConflict(segment, toCoord, num6,
				fromCoord, num8, num10));
		Label_00C9: return (this._obstacleYCoordLowerLevel[lowerLevelIndex] != num4);

	}

	private Boolean ResolveConflictsAtUpperLevel(HSegment segment) {

		if (segment.IsGradientZeroInFlow()) {

			return false;
		}
		Integer upperLevelIndex = this.GetUpperLevelIndex(segment);
		if (upperLevelIndex < 0) {

			return false;
		}
		Integer edgeFlow = super.GetGraph().GetEdgeFlow();
		Integer index = 1 - edgeFlow;
		float num4 = this._obstacleYCoordUpperLevel[upperLevelIndex];
		float fromCoord = segment.GetFromCoord(index);
		float num6 = segment.GetFromCoord(edgeFlow);
		float toCoord = segment.GetToCoord(index);
		float num8 = segment.GetToCoord(edgeFlow);
		Integer lowerLevelIndex = this.GetLowerLevelIndex(segment);
		if (lowerLevelIndex >= 0) {
			num8 = this._obstacleYCoordLowerLevel[lowerLevelIndex];
		}
		Boolean flag = toCoord > fromCoord;
		Integer num10 = upperLevelIndex;
		Label_0079: do {
			if (flag) {
				num10++;
				if ((num10 < this._obstacleYCoordUpperLevel.length)
						&& (this._obstacleXCoordUpperLevel[num10] < toCoord)) {

				} else
					return (this._obstacleYCoordUpperLevel[upperLevelIndex] != num4);
			} else {
				num10--;
				if ((num10 < 0)
						|| (this._obstacleXCoordUpperLevel[num10] <= toCoord)) {
					return (this._obstacleYCoordUpperLevel[upperLevelIndex] != num4);
				}
			}

		} while (!this.ResolveUpperLevelConflict(segment, fromCoord, num6,
				toCoord, num8, num10));
		Label_00C9: return (this._obstacleYCoordUpperLevel[upperLevelIndex] != num4);

	}

	private Boolean ResolveLowerLevelConflict(HSegment segment, float x1,
			float y1, float x2, float y2, Integer i) {
		float num = this._obstacleXCoordLowerLevel[i];
		float num2 = this._obstacleYCoordLowerLevel[i] - this._minShiftOffset;
		if ((num < x1) || (num < x2)) {
			if ((num <= x1) && (num <= x2)) {

				return false;
			}
			Integer levelFlow = super.GetGraph().GetLevelFlow();
			Integer index = 1 - levelFlow;
			HNode to = segment.GetTo();
			float coord = to.GetCoord(levelFlow);
			float num6 = coord + to.GetSize(levelFlow);
			float num7 = to.GetCoord(index);
			if (this._obstacleIsSegmentLowerLevel[i]) {
				if (((coord <= num) && (num <= num6))
						&& (this._obstacleYCoordLowerLevel[i] >= num7)) {

					return false;
				}
			} else if ((coord <= num) && (num <= num6)) {

				return false;
			}
			float y = y2 - (((y2 - num2) * (x2 - x1)) / (x2 - num));
			if (y < this._middleBetweenLevels) {
				this._minShiftOffset = 0.5f * this._minShiftOffset;

				return true;
			}
			this.UpdateLowerLevelYCoord(segment, y);
		}

		return false;

	}

	private Boolean ResolveUpperLevelConflict(HSegment segment, float x1,
			float y1, float x2, float y2, Integer i) {
		float num = this._obstacleXCoordUpperLevel[i];
		float num2 = this._obstacleYCoordUpperLevel[i] + this._minShiftOffset;
		if ((num < x1) || (num < x2)) {
			if ((num <= x1) && (num <= x2)) {

				return false;
			}
			Integer levelFlow = super.GetGraph().GetLevelFlow();
			Integer index = 1 - levelFlow;
			HNode from = segment.GetFrom();
			float coord = from.GetCoord(levelFlow);
			float num6 = coord + from.GetSize(levelFlow);
			float num7 = from.GetCoord(index) + from.GetSize(index);
			if (this._obstacleIsSegmentUpperLevel[i]) {
				if (((coord <= num) && (num <= num6))
						&& (this._obstacleYCoordUpperLevel[i] <= num7)) {

					return false;
				}
			} else if ((coord <= num) && (num <= num6)) {

				return false;
			}
			float y = y2 - (((y2 - num2) * (x2 - x1)) / (x2 - num));
			if (y > this._middleBetweenLevels) {
				this._minShiftOffset = 0.5f * this._minShiftOffset;

				return true;
			}
			this.UpdateUpperLevelYCoord(segment, y);
		}

		return false;

	}

	@Override
	public void Run() {

		this._minShiftOffset = super.GetGraph().GetMinDistBetweenNodeAndLink(
				super.GetGraph().GetEdgeFlow());
		this.GenerateSegmentIndexe();
		this.GenerateUpperLevelObstacleTable();
		this.GenerateLowerLevelObstacleTable();
		Boolean flag = true;
		Integer num2 = 0;
		Integer num3 = 0;
		while (flag) {
			num3++;
			if (num3 > 100) {
				break;
			}
			if (num3 > 3) {
				this._minShiftOffset = 0.5f * this._minShiftOffset;
			}
			float num = this._minShiftOffset;

			flag = this.ResolveConflicts();
			if (num > this._minShiftOffset) {
				if (num2 >= 3) {
					this._minShiftOffset = 0f;
				}
				flag = true;
				this.FillUpperLevelObstacleTable();
				this.FillLowerLevelObstacleTable();
				num2++;
				num3 = 0;
			} else {

				flag |= this.CorrectForkLinks();
			}
		}
		this.CreateAllBends();

	}

	private void UpdateLowerLevelYCoord(HSegment segment, float y) {

		if (!segment.IsGradientZeroInFlow()) {
			Integer lowerLevelIndex = this.GetLowerLevelIndex(segment);
			if ((lowerLevelIndex >= 0)
					&& (this._obstacleYCoordLowerLevel[lowerLevelIndex] > y)) {
				this._obstacleYCoordLowerLevel[lowerLevelIndex] = y;
			}
		}

	}

	private void UpdateUpperLevelYCoord(HSegment segment, float y) {

		if (!segment.IsGradientZeroInFlow()) {
			Integer upperLevelIndex = this.GetUpperLevelIndex(segment);
			if ((upperLevelIndex >= 0)
					&& (this._obstacleYCoordUpperLevel[upperLevelIndex] < y)) {
				this._obstacleYCoordUpperLevel[upperLevelIndex] = y;
			}
		}

	}

}
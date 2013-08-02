package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import system.*;

public final class HSwimLane {
	private float _avgPositionActLevel;

	private float _avgPositionPrevLevel;

	private HNode[] _bordernodes;

	private HierarchicalSwimLaneConstraint _constraint;

	private float[] _coord;

	private Boolean _freeToLeft = false;

	private Boolean _freeToRight = false;

	private Integer _marker;

	private float _maxFixed;

	private float _maxInfluencedFromFixed;

	private float _maxNodePosition;

	private float _minFixed;

	private float _minInfluencedFromFixed;

	private float _minNodePosition;

	private HSwimLane _next;

	private HNode[] _nodes;

	private HMANode _orderingNode;

	private Integer _orderingNumber;

	private float _shiftValue;

	private float[] _size;

	public HSwimLane(HSwimLane next, HierarchicalSwimLaneConstraint constraint) {
		this._next = next;
		this._constraint = constraint;
		this._orderingNode = null;
		this._orderingNumber = 0;
		this._marker = 0;
		this._nodes = null;
		this._avgPositionPrevLevel = -1f;
		this._avgPositionActLevel = -1f;
		this._coord = new float[2];
		this._size = new float[2];
	}

	public void AddAvgPositionActLevel(float pos) {
		this._avgPositionActLevel += pos;

	}

	public void AddBounds(Integer index, float coord) {
		if (this._size[index] < 0f) {
			this._coord[index] = coord;
			this._size[index] = 0f;
		} else if (this._coord[index] > coord) {
			this._size[index] += this._coord[index] - coord;
			this._coord[index] = coord;
		} else if ((this._coord[index] + this._size[index]) < coord) {
			this._size[index] = coord - this._coord[index];
		}

	}

	public void AddMarker(Integer add) {
		this._marker += add;

	}

	public void AddNode(HNode node) {
		this._nodes[this._marker++] = node;

	}

	public void AddShiftValue(float val) {
		this._shiftValue += val;

	}

	public void AllocateBorderNodes(HGraph graph, float width) {
		Integer levelFlow = graph.GetLevelFlow();
		Integer numberOfLevels = graph.GetNumberOfLevels();
		this._bordernodes = new HNode[numberOfLevels];
		for (Integer i = 0; i < numberOfLevels; i++) {

			this._bordernodes[i] = graph.NewHNode();
			this._bordernodes[i].SetLevelNumber(i);
			this._bordernodes[i].SetFixedForIncremental(levelFlow);
			this._bordernodes[i].SetSize(levelFlow, width);
			this._bordernodes[i].SetSize(1 - levelFlow, 0f);
			HLevel level = this._bordernodes[i].GetLevel();
			this._bordernodes[i].SetCoord(
					1 - levelFlow,
					level.GetCoord(1 - levelFlow)
							+ (0.5f * level.GetSize(1 - levelFlow)));
		}

	}

	public float GetAvgPositionActLevel() {

		return this._avgPositionActLevel;

	}

	public float GetAvgPositionPrevLevel() {

		return this._avgPositionPrevLevel;

	}

	public HNode GetBorderNode(Integer levelNumber) {

		return this._bordernodes[levelNumber];

	}

	public float GetCoord(Integer index) {

		return this._coord[index];

	}

	public Integer GetMarker() {

		return this._marker;

	}

	public float GetMaxFixed() {

		return this._maxFixed;

	}

	public float GetMaxInfluencedFromFixed() {

		return this._maxInfluencedFromFixed;

	}

	public float GetMaxNodePosition() {

		return this._maxNodePosition;

	}

	public float GetMinFixed() {

		return this._minFixed;

	}

	public float GetMinInfluencedFromFixed() {

		return this._minInfluencedFromFixed;

	}

	public float GetMinMargin() {
		if (this._constraint != null) {

			return this._constraint.get_MinMargin();
		}

		return 5f;

	}

	public float GetMinNodePosition() {

		return this._minNodePosition;

	}

	public HSwimLane GetNext() {

		return this._next;

	}

	public HMANode GetOrderingNode() {

		return this._orderingNode;

	}

	public Integer GetOrderingNumber() {

		return this._orderingNumber;

	}

	public float GetRelativeSize() {
		if (this._constraint != null) {

			return this._constraint.get_RelativeSize();
		}

		return 0f;

	}

	public float GetShiftValue() {

		return this._shiftValue;

	}

	public float GetSize(Integer index) {

		return this._size[index];

	}

	public Integer GetSpecPositionIndex() {

		return this._constraint.get_SpecPositionIndex();

	}

	public Boolean IsFreeToLeft() {

		return this._freeToLeft;

	}

	public Boolean IsFreeToRight() {

		return this._freeToRight;

	}

	public void Mirror(Integer coordinateIndex) {
		this._coord[coordinateIndex] = -this._coord[coordinateIndex]
				- this._size[coordinateIndex];

	}

	public void MoveToNextLevelDuringCrossingReduction() {
		this._avgPositionPrevLevel = this._avgPositionActLevel;

	}

	public void RemoveBorderNodes(HGraph graph) {
		for (Integer i = 0; i < this._bordernodes.length; i++) {
			this._bordernodes[i].SetLevelNumber(-1);
			graph.RemoveNode(this._bordernodes[i]);
		}

	}

	public void SetAvgPositionActLevel(float pos) {
		this._avgPositionActLevel = pos;

	}

	public void SetBorderCoord(Integer dir, float coord) {
		for (Integer i = 0; i < this._bordernodes.length; i++) {
			this._bordernodes[i].SetCoord(dir, coord);
		}

	}

	public void SetBoundingBox() {
		if (this._constraint != null) {
			InternalRect box = new InternalRect(this._coord[0], this._coord[1],
					this._size[0], this._size[1]);
			this._constraint.SetCalcBoundingBox(box);
		}

	}

	public void SetCalcPositionIndex(Integer pos) {
		if (this._constraint != null) {
			this._constraint.SetCalcPositionIndex(pos);
		}

	}

	public void SetCoord(Integer index, float coord) {
		this._coord[index] = coord;

	}

	public void SetFreeToLeft(Boolean flag) {
		this._freeToLeft = flag;

	}

	public void SetFreeToRight(Boolean flag) {
		this._freeToRight = flag;

	}

	public void SetMarker(Integer marker) {
		this._marker = marker;

	}

	public void SetMaxFixed(float pos) {
		this._maxFixed = pos;

	}

	public void SetMaxInfluencedFromFixed(float pos) {
		this._maxInfluencedFromFixed = pos;

	}

	public void SetMaxNodePosition(float pos) {
		this._maxNodePosition = pos;

	}

	public void SetMinFixed(float pos) {
		this._minFixed = pos;

	}

	public void SetMinInfluencedFromFixed(float pos) {
		this._minInfluencedFromFixed = pos;

	}

	public void SetMinNodePosition(float pos) {
		this._minNodePosition = pos;

	}

	public void SetNodesCapacity(Integer capacity) {
		this._nodes = new HNode[capacity];
		this._marker = 0;

	}

	public void SetOrderingNode(HMANode orderingNode) {
		this._orderingNode = orderingNode;

	}

	public void SetOrderingNumber(Integer number) {
		this._orderingNumber = number;

	}

	public void SetShiftValue(float val) {
		this._shiftValue = val;

	}

	public void SetSize(Integer index, float size) {
		this._size[index] = size;

	}

	public void ShiftBy(float dx, float dy) {
		this._coord[0] += dx;
		this._coord[1] += dy;

	}

	public void UpdateMaxFixed(float pos) {
		if (pos > this._maxFixed) {
			this._maxFixed = pos;
		}

	}

	public void UpdateMaxInfluencedFromFixed(float pos) {
		if (pos > this._maxInfluencedFromFixed) {
			this._maxInfluencedFromFixed = pos;
		}

	}

	public void UpdateMinFixed(float pos) {
		if (pos < this._minFixed) {
			this._minFixed = pos;
		}

	}

	public void UpdateMinInfluencedFromFixed(float pos) {
		if (pos < this._minInfluencedFromFixed) {
			this._minInfluencedFromFixed = pos;
		}

	}

}
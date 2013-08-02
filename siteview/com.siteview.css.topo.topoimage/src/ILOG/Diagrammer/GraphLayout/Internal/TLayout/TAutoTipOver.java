package ILOG.Diagrammer.GraphLayout.Internal.TLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class TAutoTipOver extends TGraphAlgorithm {
	private double _aspectRatio;

	private double _bestArea;

	private Integer _maxDepth;

	public TAutoTipOver(TGraph graph) {
		super(graph);

		this._aspectRatio = graph.GetAspectRatio();
		if ((graph.GetFlowDirection() % 2) == 0) {
			this._aspectRatio = 1.0 / this._aspectRatio;
		}
		this._bestArea = system.ClrDouble.MaxValue;
	}

	@Override
	public void DisposeIt() {
		super.DisposeIt();

	}

	private void DoLayout() {
		TGraph graph = super.GetGraph();
		graph.CalcCoords();
		InternalRect rect = graph.CalcBoundingBox();
		double width = rect.Width;
		double height = rect.Height;

		if (this.IsBetterAspectRatio(width, height)) {
			this.StoreBestAlignmentStyle();
		}
		this.RestoreOriginalAlignmentStyle();

	}

	private void FastTipOver() {
		TGraph graph = super.GetGraph();

		if (this.FromLeavesToRoot(this.GetMaxDepth() - 1)) {
			this.DoLayout();
		} else {
			graph.CorrectPercentageForTipOver();
		}

		if (super.MayContinue()) {

			if (this.FromLeavesToRoot(this.GetMaxDepth() - 2)) {
				this.DoLayout();
			} else {
				graph.CorrectPercentageForTipOver();
			}

			if (super.MayContinue()) {

				if (this.FromRootToLeaves(0)) {
					this.DoLayout();
				} else {
					graph.CorrectPercentageForTipOver();
				}

				if (super.MayContinue()) {

					if (this.FromRootToLeaves(1)) {
						this.DoLayout();
					} else {
						graph.CorrectPercentageForTipOver();
					}

					if (super.MayContinue()) {

						if (this.FromRootAndLeavesToInner(0,
								this.GetMaxDepth() - 1)) {
							this.DoLayout();
						} else {
							graph.CorrectPercentageForTipOver();
						}
						super.MayContinue();
					}
				}
			}
		}

	}

	private void FromLeavesToRoot() {
		TGraph graph = super.GetGraph();
		for (Integer i = 0; i < this.GetMaxDepth(); i++) {

			if (this.FromLeavesToRoot(i)) {
				this.DoLayout();
			} else {
				graph.CorrectPercentageForTipOver();
			}

			if (!super.MayContinue()) {

				return;
			}
		}

	}

	private Boolean FromLeavesToRoot(Integer tipOverLevel) {
		Boolean flag = false;
		TNodeIterator roots = super.GetGraph().GetRoots();

		while (roots.HasNext()) {

			flag |= this.TipOverAtDepth(roots.Next(), 0, -1, tipOverLevel);
		}

		return flag;

	}

	private void FromRootAndLeavesToInner() {
		TGraph graph = super.GetGraph();
		for (Integer i = -1; i < (this.GetMaxDepth() - 1); i++) {
			for (Integer j = i + 2; j <= this.GetMaxDepth(); j++) {

				if (this.FromRootAndLeavesToInner(i, j)) {
					this.DoLayout();
				} else {
					graph.CorrectPercentageForTipOver();
				}

				if (!super.MayContinue()) {

					return;
				}
			}
		}

	}

	private Boolean FromRootAndLeavesToInner(Integer tipOverRootLevel,
			Integer tipOverLeavesLevel) {
		Boolean flag = false;
		TNodeIterator roots = super.GetGraph().GetRoots();

		while (roots.HasNext()) {

			flag |= this.TipOverAtDepth(roots.Next(), 0, tipOverRootLevel,
					tipOverLeavesLevel);
		}

		return flag;

	}

	private void FromRootToLeaves() {
		TGraph graph = super.GetGraph();
		for (Integer i = 0; i < this.GetMaxDepth(); i++) {

			if (this.FromRootToLeaves(i)) {
				this.DoLayout();
			} else {
				graph.CorrectPercentageForTipOver();
			}

			if (!super.MayContinue()) {

				return;
			}
		}

	}

	private Boolean FromRootToLeaves(Integer tipOverLevel) {
		Boolean flag = false;
		TNodeIterator roots = super.GetGraph().GetRoots();

		while (roots.HasNext()) {

			flag |= this.TipOverAtDepth(roots.Next(), 0, tipOverLevel,
					this.GetMaxDepth() + 1);
		}

		return flag;

	}

	private double GetAspectRatio() {

		return this._aspectRatio;

	}

	private Integer GetMaxDepth() {

		return this._maxDepth;

	}

	private Boolean IsBetterAspectRatio(double width, double hight) {
		if ((width != 0.0) && (hight != 0.0)) {
			double num = 0;
			if ((width / hight) < this.GetAspectRatio()) {
				num = (hight * hight) * this.GetAspectRatio();
			} else {
				num = (width * width) / this.GetAspectRatio();
			}
			if (num < this._bestArea) {
				this._bestArea = num;

				return true;
			}
		}

		return false;

	}

	private void RestoreBestAlignmentStyle() {
		TNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().RestoreBestAlignmentStyle();
		}

	}

	private void RestoreOriginalAlignmentStyle() {
		TNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().RestoreOriginalAlignmentStyle();
		}

	}

	public void Run(Integer mode) {
		TGraph graph = super.GetGraph();

		this._maxDepth = graph.GetMaxDepth();
		this.StoreOriginalAlignmentStyle();
		Integer numberTries = 3;
		if (mode == 4 || mode == 5) {
			numberTries = this._maxDepth + 3;
			// NOTICE: break ignore!!!
		} else if (mode == 6) {
			numberTries = ((this._maxDepth * (this._maxDepth - 1)) / 2) + 3;
			// NOTICE: break ignore!!!
		} else if (mode == 10) {
			numberTries = 8;
			// NOTICE: break ignore!!!
		}
		super.StartStep(0f, 1);
		super.SetStepEstimation(6 + (numberTries * 2));
		graph.ChangePercParameterForAutoTipOverLayout(numberTries);
		this.DoLayout();

		if (super.MayContinue()) {

			if (this.TipAllOver()) {
				this.DoLayout();
			} else {
				graph.CorrectPercentageForTipOver();
			}

			if (super.MayContinue()) {
				if (mode == 4) {
					this.FromLeavesToRoot();
					// NOTICE: break ignore!!!
				} else if (mode == 5) {
					this.FromRootToLeaves();
					// NOTICE: break ignore!!!
				} else if (mode == 6) {
					this.FromRootAndLeavesToInner();
					// NOTICE: break ignore!!!
				} else if (mode == 10) {
					this.FastTipOver();
					// NOTICE: break ignore!!!
				}
				this.RestoreBestAlignmentStyle();
				graph.CalcCoords();
			}
		}

	}

	private void StoreBestAlignmentStyle() {
		TNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().StoreBestAlignmentStyle();
		}

	}

	private void StoreOriginalAlignmentStyle() {
		TNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().StoreOriginalAlignmentStyle();
		}

	}

	private Boolean TipAllOver() {
		Boolean flag = false;
		TNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			TNode node = nodes.Next();

			if (node.CanAutoTipOver()) {
				flag = true;
				node.SetAlignmentStyle(10);
			}
		}

		return flag;

	}

	private Boolean TipOverAtDepth(TNode node, Integer depth,
			Integer tipOverRootLevel, Integer tipOverLeavesLevel) {
		Boolean flag = false;
		if (node.CanAutoTipOver()
				&& ((depth >= tipOverLeavesLevel) || (depth <= tipOverRootLevel))) {
			node.SetAlignmentStyle(10);
			flag = true;
		}
		for (Integer i = 0; i < node.GetNumberOfChildren(); i++) {

			flag |= this.TipOverAtDepth(node.GetChild(i), depth + 1,
					tipOverRootLevel, tipOverLeavesLevel);
		}

		return flag;

	}

}
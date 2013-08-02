package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class HLevelAlgApplicator extends HLevelSweepAlgorithm {
	private Boolean _backward = false;

	private HLevelAlgorithm _levelAlg;

	public HLevelAlgApplicator(HGraph graph, HLevelAlgorithm levelAlg,
			Boolean backward) {
		super.Init(graph);
		this._levelAlg = levelAlg;
		this._backward = backward;
	}

	@Override
	public void Clean() {
		super.Clean();
		this._levelAlg = null;

	}

	@Override
	public void Run() {
		if (this._backward) {
			this.SweepBackward();
		} else {
			this.SweepForward();
		}

	}

	@Override
	public void TreatBackwardLevel(HLevel prevLevel, HLevel level) {
		this._levelAlg.Init(level, prevLevel);
		this._levelAlg.Run();

	}

	@Override
	public void TreatForwardLevel(HLevel prevLevel, HLevel level) {
		this._levelAlg.Init(prevLevel, level);
		this._levelAlg.Run();

	}

}
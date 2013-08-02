package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public abstract class HLevelSweepAlgorithm extends HGraphAlgorithm {
	public HLevelSweepAlgorithm() {
	}

	@Override
	public void Clean() {
		super.Clean();

	}

	@Override
	public void Init(HGraph graph) {
		super.Init(graph);

	}

	public void InitBackwardFirstLevel(HLevel firstLevel) {

	}

	public void InitForwardFirstLevel(HLevel firstLevel) {

	}

	@Override
	public abstract void Run();

	public void SweepBackward() {
		HGraph graph = super.GetGraph();
		if (graph.GetNumberOfLevels() > 1) {
			HLevelIterator levelsBackward = graph.GetLevelsBackward();
			HLevel firstLevel = levelsBackward.Prev();
			this.InitBackwardFirstLevel(firstLevel);

			while (levelsBackward.HasPrev()) {
				HLevel level = levelsBackward.Prev();
				this.TreatBackwardLevel(firstLevel, level);
				super.GetPercController().AddPoints(1);
				super.LayoutStepPerformed();
				firstLevel = level;
			}
		}

	}

	public void SweepForward() {
		HGraph graph = super.GetGraph();
		if (graph.GetNumberOfLevels() > 1) {
			HLevelIterator levels = graph.GetLevels();
			HLevel firstLevel = levels.Next();
			this.InitForwardFirstLevel(firstLevel);

			while (levels.HasNext()) {
				HLevel level = levels.Next();
				this.TreatForwardLevel(firstLevel, level);
				super.GetPercController().AddPoints(1);
				super.LayoutStepPerformed();
				firstLevel = level;
			}
		}

	}

	public abstract void TreatBackwardLevel(HLevel prevLevel, HLevel level);

	public abstract void TreatForwardLevel(HLevel prevLevel, HLevel level);

}
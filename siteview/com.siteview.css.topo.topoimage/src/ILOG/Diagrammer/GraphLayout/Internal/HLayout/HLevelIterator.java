package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public interface HLevelIterator {
	Boolean HasNext();

	Boolean HasPrev();

	void Init(HLevel level);

	HLevel Next();

	HLevel Prev();

}
package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public interface HNodeIterator {
	Boolean HasNext();

	void Init(HNode node);

	HNode Next();

}
package ILOG.Diagrammer.GraphLayout;

import java.util.List;

import ILOG.Diagrammer.GraphicObject;

public interface IGraphLayoutConstraint {
	void Add(ILOG.Diagrammer.GraphLayout.GraphLayout layout);

	void Remove(ILOG.Diagrammer.GraphLayout.GraphLayout layout);

	Boolean SupportsNodeGroups();

	List<GraphicObject> get_FirstSubject();

	float get_Priority();

	void set_Priority(float value);

	List<GraphicObject> get_SecondSubject();

	String[] get_SubjectDescriptions();

}
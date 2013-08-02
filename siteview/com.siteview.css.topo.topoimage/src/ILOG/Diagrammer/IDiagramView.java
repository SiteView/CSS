package ILOG.Diagrammer;

import java.util.ArrayList;

import system.EventHandler;

public interface IDiagramView {
	/* TODO: Event Declare */
	ArrayList<EventHandler> ContentBoundsChanged = new ArrayList<EventHandler>();
	/* TODO: Event Declare */
	ArrayList<ContentChangeEventHandler> ContentChanged = new ArrayList<ContentChangeEventHandler>();
	/* TODO: Event Declare */
	ArrayList<EventHandler> ViewSizeChanged = new ArrayList<EventHandler>();

	GraphicContainer get_Content();

	void set_Content(GraphicContainer value);

	Rectangle2D get_ContentBounds();

	Size2D get_ViewSize();

}
package ILOG.Diagrammer;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;


public class TopoEdgeEditor extends AbstractPVWidgetEditPart {

	private TopoEdgeFigure edgeFigure;
	public TopoEdgeEditor() {
		super();
	}

	@Override
	protected IFigure doCreateFigure() {
		edgeFigure = new TopoEdgeFigure();
		edgeFigure.setMax(getTopoEdgeModel().getMax());
		edgeFigure.setMin(getTopoEdgeModel().getMin());
		return edgeFigure;
	}
	public TopoEdgeModel getTopoEdgeModel(){
		return (TopoEdgeModel)getModel();
	}
	@Override
	protected void registerPropertyChangeHandlers() {
		// the handle when pv value changed;
				IWidgetPropertyChangeHandler handlerValue = new IWidgetPropertyChangeHandler() {

					public boolean handleChange(Object oldValue, Object newValue,
							IFigure figure) {
						if (newValue == null) {
							return false;
						}
						TopoEdgeFigure f = (TopoEdgeFigure)figure;
						f.changeBgColor(ValueUtil.getDouble((IValue)newValue));
						return false;
					}
				};
				setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE,
						handlerValue);
	}
	
}

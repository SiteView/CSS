package com.siteview.css.topo.editparts;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;

import com.siteview.css.topo.figure.TopologyFigure;
import com.siteview.css.topo.models.TopologyModel;

public class TopologyEdit extends AbstractPVWidgetEditPart {


	TopologyFigure topoFigure;
	@Override
	protected IFigure doCreateFigure() {
		topoFigure = new TopologyFigure();
		topoFigure.setMin(getTopoModel().getMin());
		topoFigure.setMax(getTopoModel().getMax());
		return topoFigure;
	}
	@Override
	public AbstractWidgetModel getWidgetModel() {
		return (TopologyModel)getModel();
	}
	public TopologyModel getTopoModel(){
		return (TopologyModel)getModel();
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// the handle when pv value changed;
		IWidgetPropertyChangeHandler handlerValue = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				if(newValue == null){
					return false;
				}
				String newValueClassname = newValue.getClass().getName();
				//((TopologyFigure)figure).setValue(ValueUtil.getDouble((IValue)newValue));
				topoFigure.setLabelValue(ValueUtil.getString((IValue)newValue));
				topoFigure.changeBackGroundColor(ValueUtil.getDouble((IValue)newValue));
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handlerValue);
		//the handle whem max prperty value changed;
		IWidgetPropertyChangeHandler maxHandle = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((TopologyFigure)figure).setMax(ValueUtil.getDouble((IValue)newValue));
				return false;
			}
		};
		setPropertyChangeHandler(TopologyModel.PROP_MAX, maxHandle);
		
	}
	@Override
	public Border calculateBorder() {
		return super.calculateBorder();
	}

}

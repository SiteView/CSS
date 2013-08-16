package com.siteview.itsm.nnm.widgets.editpart;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;

import com.siteview.itsm.nnm.widgets.factory.ColorFactory;
import com.siteview.itsm.nnm.widgets.figure.SwitchFigure;
import com.siteview.itsm.nnm.widgets.model.SwitchModel;


public class SwitchEdit extends AbstractPVWidgetEditPart {
	private SwitchFigure switchFigure;

	@Override
	protected IFigure doCreateFigure() {
		switchFigure = new SwitchFigure();
		switchFigure.setMin(getTopoModel().getMin());
		switchFigure.setMax(getTopoModel().getMax());
		return switchFigure;
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return (SwitchModel) getModel();
	}

	public SwitchModel getTopoModel() {
		return (SwitchModel) getModel();
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
//				switchFigure
//						.setLabelValue(ValueUtil.getString((IValue) newValue));
//				switchFigure.changeBackGroundColor(ValueUtil
//						.getDouble((IValue) newValue));
				
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE,
				handlerValue);
		// the handle whem max prperty value changed;
		IWidgetPropertyChangeHandler maxHandle = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((SwitchFigure) figure).setMax(ValueUtil
						.getDouble((IValue) newValue));
				return false;
			}
		};
		setPropertyChangeHandler(SwitchModel.PROP_MAX, maxHandle);
		setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, "sv:topo");

	}

	@Override
	public Border calculateBorder() {
		return super.calculateBorder();
	}
}

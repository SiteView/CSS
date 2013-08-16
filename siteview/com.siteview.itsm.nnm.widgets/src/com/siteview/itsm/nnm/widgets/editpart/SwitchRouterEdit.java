package com.siteview.itsm.nnm.widgets.editpart;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;

import com.siteview.itsm.nnm.widgets.figure.SwitchRouterFigure;
import com.siteview.itsm.nnm.widgets.model.SwitchRouterModel;


public class SwitchRouterEdit extends AbstractPVWidgetEditPart {
	private SwitchRouterFigure switchFigure;

	@Override
	protected IFigure doCreateFigure() {
		switchFigure = new SwitchRouterFigure();
		switchFigure.setMin(getTopoModel().getMin());
		switchFigure.setMax(getTopoModel().getMax());
		return switchFigure;
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return (SwitchRouterModel) getModel();
	}

	public SwitchRouterModel getTopoModel() {
		return (SwitchRouterModel) getModel();
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
				switchFigure
						.setLabelValue(ValueUtil.getString((IValue) newValue));
				switchFigure.changeBackGroundColor(ValueUtil
						.getDouble((IValue) newValue));
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE,
				handlerValue);
		// the handle whem max prperty value changed;
		IWidgetPropertyChangeHandler maxHandle = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((SwitchRouterFigure) figure).setMax(ValueUtil
						.getDouble((IValue) newValue));
				return false;
			}
		};
		setPropertyChangeHandler(SwitchRouterModel.PROP_MAX, maxHandle);

	}

	@Override
	public Border calculateBorder() {
		return super.calculateBorder();
	}
}

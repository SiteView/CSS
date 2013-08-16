package com.siteview.itsm.nnm.widgets.editpart;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;

import com.siteview.itsm.nnm.widgets.figure.RouterFigure;
import com.siteview.itsm.nnm.widgets.model.RouterModel;


public final class RouterEdit extends AbstractPVWidgetEditPart {
	
	private RouterFigure routerFigure;

	@Override
	protected IFigure doCreateFigure() {
		routerFigure = new RouterFigure();
		routerFigure.setMin(getTopoModel().getMin());
		routerFigure.setMax(getTopoModel().getMax());
		return routerFigure;
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return (RouterModel) getModel();
	}

	public RouterModel getTopoModel() {
		return (RouterModel) getModel();
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
				String newValueClassname = newValue.getClass().getName();
				// ((TopologyFigure)figure).setValue(ValueUtil.getDouble((IValue)newValue));
				routerFigure
						.setLabelValue(ValueUtil.getString((IValue) newValue));
				routerFigure.changeBackGroundColor(ValueUtil
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
				((RouterFigure) figure).setMax(ValueUtil
						.getDouble((IValue) newValue));
				return false;
			}
		};
		setPropertyChangeHandler(RouterModel.PROP_MAX, maxHandle);

	}

	@Override
	public Border calculateBorder() {
		return super.calculateBorder();
	}

}

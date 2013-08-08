package com.siteview.css.topo.editparts;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;

import com.siteview.css.topo.figure.RouterFigure;
import com.siteview.css.topo.models.RouterModel;

public class RouterEdit extends AbstractPVWidgetEditPart {

	RouterFigure routerFigure;

	public RouterModel getRouterModel() {
		return (RouterModel) getModel();
	}

	@Override
	protected IFigure doCreateFigure() {
		routerFigure = new RouterFigure();
		return routerFigure;
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
				RouterFigure routerFigure  = (RouterFigure)figure;
				String newValueClassname = newValue.getClass().getName();
				System.out.println(newValueClassname);
//				routerFigure.setLabelValue(ValueUtil
//						.getString((IValue) newValue));
				routerFigure.changeBackGroundColor(ValueUtil
						.getDouble((IValue) newValue));
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE,
				handlerValue);
		// the handle whem max prperty value changed;
//		IWidgetPropertyChangeHandler maxHandle = new IWidgetPropertyChangeHandler() {
//
//			public boolean handleChange(Object oldValue, Object newValue,
//					IFigure figure) {
//				((RouterFigure) figure).setMax(ValueUtil
//						.getDouble((IValue) newValue));
//				return false;
//			}
//		};
//		setPropertyChangeHandler(RouterModel.PROP_MAX, maxHandle);

	}

	public Border calculateBorder() {
		return super.calculateBorder();
	}
}

package com.siteview.itsm.nnm.widgets.editpart;

import java.util.List;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;

import com.siteview.itsm.nnm.widgets.factory.ColorFactory;
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
//				switchFigure
//						.setLabelValue(ValueUtil.getString((IValue) newValue));
//				switchFigure.changeBackGroundColor(ValueUtil
//						.getDouble((IValue) newValue));
				List<ConnectionModel> srcConns = getTopoModel().getSourceConnections();
				List<ConnectionModel> targetConns = getTopoModel().getTargetConnections();
				ConnectionModel src = null;
				if(srcConns != null && !srcConns.isEmpty()){
					src =  srcConns.get(0);
				}
				
				ConnectionModel target = null;
				if(targetConns != null && !targetConns.isEmpty()){
					src =  targetConns.get(0);
				}
				Color color = ColorFactory.RED_COLOR;
				if(ValueUtil.getDouble((IValue) newValue)<50){
					color = ColorFactory.GREEN_COLOR;
				}
				if(src!=null){
					src.setPropertyValue(ConnectionModel.PROP_LINE_COLOR, color);
					src.reconnect();
				}
				if(target!=null){
					target.setPropertyValue(ConnectionModel.PROP_LINE_COLOR, color);
					target.reconnect();
				}
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
		setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, "sv://topo");
	}

	@Override
	public Border calculateBorder() {
		return super.calculateBorder();
	}
}

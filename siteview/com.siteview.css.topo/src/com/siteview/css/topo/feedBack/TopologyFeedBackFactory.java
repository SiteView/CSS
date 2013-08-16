package com.siteview.css.topo.feedBack;

import org.csstudio.opibuilder.widgets.feedback.AbstractFixRatioSizeFeedbackFactory;

import com.siteview.itsm.nnm.widgets.model.TopologyModel;


public class TopologyFeedBackFactory extends AbstractFixRatioSizeFeedbackFactory{

	@Override
	public int getMinimumWidth() {
		return TopologyModel.MINIMUM_SIZE;
	}

}

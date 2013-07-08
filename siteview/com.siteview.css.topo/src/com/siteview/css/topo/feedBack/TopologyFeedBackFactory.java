package com.siteview.css.topo.feedBack;

import org.csstudio.opibuilder.widgets.feedback.AbstractFixRatioSizeFeedbackFactory;

import com.siteview.css.topo.models.TopologyModel;

public class TopologyFeedBackFactory extends AbstractFixRatioSizeFeedbackFactory{

	@Override
	public int getMinimumWidth() {
		return TopologyModel.MINIMUM_SIZE;
	}

}

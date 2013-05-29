//package com.siteview.css.topo.models;
//
//public abstract class AbstractConnectionModel {
//	//this connectionʹs root is connected to source 链接的头端添加到source
//	private TopologyModel source, target;
//	public void attachSource(){
//		if(!source.getModelSourceConnections().contains(this))
//		source.addSourceConnection(this);
//	}
//	//this connectionʹs tip is connected to source 链接的尾端添加到target
//	public void attachTarget(){
//		if(!target.getModelTargetConnections().contains(this))
//		target.addTargetConnection(this);
//	}
//	//this connectionʹs root is removed from source
//	public void detachSource(){
//		source.removeSourceConnection(this);
//	}
//	//this connectionʹs tip is removed from target
//	public void detachTarget(){
//		target.removeTargetConnection(this);
//	}
//	public TopologyModel getSource() {
//		return source;
//	}
//	public void setSource(TopologyModel model) {
//		source = model;
//	}
//	public TopologyModel getTarget() {
//		return target;
//	}
//	public void setTarget(TopologyModel model) {
//		target = model;
//	}
//}

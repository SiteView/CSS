//package com.siteview.css.topo.models;
//
//import java.beans.PropertyChangeListener;
//import java.beans.PropertyChangeSupport;
//
//import org.eclipse.ui.views.properties.IPropertyDescriptor;
//import org.eclipse.ui.views.properties.IPropertySource;
//
//public class AbstractModel implements IPropertySource{
//	
//	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
//	
//	public void addPropertyChangeListener(PropertyChangeListener listener){
//		listeners.addPropertyChangeListener(listener);
//	}
//	
//	public void firePropertyChange(String propName,Object oldValue,Object newValue){
//		listeners.firePropertyChange(propName, oldValue, newValue);
//	}
//	
//	public void removePropertyChangeListener(PropertyChangeListener listener){
//		listeners.removePropertyChangeListener(listener);
//	}
//	public Object getEditableValue() {
//		return this; // ����ģ��������Ϊ�ɱ༭������ֵ
//		}
//		public IPropertyDescriptor[] getPropertyDescriptors() {
//			// ����ڳ���ģ���з���null������쳣��������ﷵ��һ������Ϊ0������
//			// ���潫����IPropertyDescriptor����
//			return new IPropertyDescriptor[0];
//		}
//		public Object getPropertyValue(Object id) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		public boolean isPropertySet(Object id) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//		public void resetPropertyValue(Object id) {
//			// TODO Auto-generated method stub
//		}
//		public void setPropertyValue(Object id, Object value) {
//			// TODO Auto-generated method stub
//		}
//}

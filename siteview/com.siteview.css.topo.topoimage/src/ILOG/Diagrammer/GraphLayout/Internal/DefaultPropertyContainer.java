package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.GraphLayout.*;
import system.*;
import system.Collections.*;

public class DefaultPropertyContainer implements IPropertyContainer {
	private Hashtable props = new Hashtable();

	public java.lang.Object GetProperty(String key) {

		return this.props.get_Item(key);

	}

	public Boolean IsEmpty() {

		return (this.props.get_Count() == 0);

	}

	public java.lang.Object SetProperty(String key, java.lang.Object value) {
		java.lang.Object obj2 = this.props.get_Item(key);
		if (value == null) {
			this.props.Remove(key);

			return obj2;
		}
		this.props.set_Item(key, value);

		return obj2;

	}

}
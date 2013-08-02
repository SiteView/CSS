package ILOG.Diagrammer.GraphLayout;

import system.*;

public interface IPropertyContainer {
	java.lang.Object GetProperty(String key);

	Boolean IsEmpty();

	java.lang.Object SetProperty(String key, java.lang.Object value);

}
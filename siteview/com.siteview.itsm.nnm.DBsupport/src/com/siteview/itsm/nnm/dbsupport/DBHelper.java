package com.siteview.itsm.nnm.dbsupport;

import Siteview.Api.IBusinessObjectService;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;

public class DBHelper {

	public static void main(String[] args) {
		ISiteviewApi api = ConnectionBroker.get_SiteviewApi();
		IBusinessObjectService obService = api.get_BusObService();
	}
}

package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.StampRally.Data.StampPin;

interface IArriveWatcherService {
	void showArriveNotification(in StampPin pin);
	void removeArriveNotification(long pinId);
}
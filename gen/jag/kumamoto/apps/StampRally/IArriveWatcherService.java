/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/shiva/Documentations/workspace/jagkuma/aharisu/jagkuma/src/jag/kumamoto/apps/StampRally/IArriveWatcherService.aidl
 */
package jag.kumamoto.apps.StampRally;
public interface IArriveWatcherService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements jag.kumamoto.apps.StampRally.IArriveWatcherService
{
private static final java.lang.String DESCRIPTOR = "jag.kumamoto.apps.StampRally.IArriveWatcherService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an jag.kumamoto.apps.StampRally.IArriveWatcherService interface,
 * generating a proxy if needed.
 */
public static jag.kumamoto.apps.StampRally.IArriveWatcherService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof jag.kumamoto.apps.StampRally.IArriveWatcherService))) {
return ((jag.kumamoto.apps.StampRally.IArriveWatcherService)iin);
}
return new jag.kumamoto.apps.StampRally.IArriveWatcherService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_showArriveNotification:
{
data.enforceInterface(DESCRIPTOR);
jag.kumamoto.apps.StampRally.Data.StampPin _arg0;
if ((0!=data.readInt())) {
_arg0 = jag.kumamoto.apps.StampRally.Data.StampPin.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.showArriveNotification(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeArriveNotification:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.removeArriveNotification(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getArrivedStampPins:
{
data.enforceInterface(DESCRIPTOR);
long[] _result = this.getArrivedStampPins();
reply.writeNoException();
reply.writeLongArray(_result);
return true;
}
case TRANSACTION_changeArriveCheckInterval:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.changeArriveCheckInterval(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements jag.kumamoto.apps.StampRally.IArriveWatcherService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void showArriveNotification(jag.kumamoto.apps.StampRally.Data.StampPin pin) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((pin!=null)) {
_data.writeInt(1);
pin.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_showArriveNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void removeArriveNotification(long pinId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(pinId);
mRemote.transact(Stub.TRANSACTION_removeArriveNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public long[] getArrivedStampPins() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getArrivedStampPins, _data, _reply, 0);
_reply.readException();
_result = _reply.createLongArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void changeArriveCheckInterval(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_changeArriveCheckInterval, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_showArriveNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_removeArriveNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getArrivedStampPins = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_changeArriveCheckInterval = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void showArriveNotification(jag.kumamoto.apps.StampRally.Data.StampPin pin) throws android.os.RemoteException;
public void removeArriveNotification(long pinId) throws android.os.RemoteException;
public long[] getArrivedStampPins() throws android.os.RemoteException;
public void changeArriveCheckInterval(int type) throws android.os.RemoteException;
}

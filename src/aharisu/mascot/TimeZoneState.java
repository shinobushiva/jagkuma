package aharisu.mascot;

public abstract class TimeZoneState extends MascotState{
	public enum Type {
		Morning,
		Daytime,
		Evening,
		Night,
	};
	
	private Type mType;
	
	public TimeZoneState(IMascot mascot, Type timeZone) {
		super(mascot);
		
		mType = timeZone;
	}
	
	public final Type getTimeZoneType() {
		return mType;
	}
	
}

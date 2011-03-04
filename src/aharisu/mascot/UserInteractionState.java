package aharisu.mascot;

/**
 * 
 * ユーザインタラクションに対応する抽象ステートクラス
 * 
 * @author aharisu
 *
 */
public abstract class UserInteractionState extends MascotState{
	public static enum Type{
		LongPress,
		SingleTap,
		DoubleTap,
		Fling,
	}
	
	private final Type mActionType;
	
	
	public UserInteractionState(IMascot mascot, Type actionType) {
		super(mascot);
		
		mActionType = actionType;
	}
	
	public Type getActionType() {
		return mActionType;
	}
	
	@Override public boolean isAllowExist() {
		return false;
	}
	
	@Override public boolean isAllowInterrupt() {
		return false;
	}

}

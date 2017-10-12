package AgentModules;
import org.jLOAF.action.AtomicAction;


/*
 * OpenAIAction Object
 * Represents a OpenAI Action, where it could one of the following {stand,up,right,left,down}
 * @author Chad Peters
 * @since 2017 September
 */
public class OpenAIAction extends AtomicAction {
	public enum Actions {STAND("STAND"),UP("UP"),RIGHT("RIGHT"),LEFT("LEFT"),DOWN ("DOWN");
		private String action;
		 Actions(String a){
			action=a;
		}
		 
		 public String getAction(){
			 return action;
		 }
	
	
	};
	
	private static final long serialVersionUID = 1L;
	/*
	 * Constructor
	 * @param name the name associated to the action
	 */
	public OpenAIAction(String name) {
		
		super(name);
		
	}
	

}

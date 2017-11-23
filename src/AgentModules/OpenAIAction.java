package AgentModules;
import org.jLOAF.action.AtomicAction;


/*
 * OpenAIAction Object
 * Represents a OpenAI Action, a list of actions performed on the environment
 * @author Chad Peters
 * @since 2017 September
 */
public class OpenAIAction extends AtomicAction
{
	public enum Actions
	{
		LEFT("LEFT"),RIGHT("RIGHT");
		
		//TODO: abstract this to Gym environment, matched to MotorControl.
		
		private String action;
		
		Actions(String a)
		{
			action=a;
		}
		 public String getAction()
		 {
			 return action;
		 }
	};//enum Actions
	
	private static final long serialVersionUID = 1L;
	/*
	 * Constructor
	 * @param name the name associated to the action
	 */
	public OpenAIAction(String name)
	{
		super(name);		
	}
	
}//class OpenAIAction

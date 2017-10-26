package AgentModules;

import org.jLOAF.Agent;
import org.jLOAF.MotorControl;
import org.jLOAF.Perception;
import org.jLOAF.Reasoning;
import org.jLOAF.action.Action;
import org.jLOAF.action.AtomicAction;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.inputs.AtomicInput;
import org.jLOAF.inputs.ComplexInput;
import org.jLOAF.inputs.Input;
import org.jLOAF.reasoning.BayesianReasoner;
import org.jLOAF.reasoning.DynamicBayesianReasoner;
import org.jLOAF.reasoning.SimpleKNN;
import org.jLOAF.reasoning.TBReasoning;
import org.jLOAF.sim.SimilarityMetricStrategy;
import org.jLOAF.sim.atomic.Equality;
import org.jLOAF.sim.complex.GreedyMunkrezMatching;
import org.jLOAF.sim.complex.Mean;
import org.jLOAF.sim.complex.WeightedMean;
import org.jLOAF.weights.SimilarityWeights;

/*
 * OpenAIAgent Object
 *
 *
 * @author Chad Peters
 * @since 2017 September
 */
public class OpenAIAgent extends Agent
{
	// constructor
	public OpenAIAgent()
	{
		super(null,null,null,null);

		this.mc = new OpenAIMotorControl();
		this.p = new OpenAIPerception();

	}

	 //@Override
	 /*
	  *Run the agent using provided inputs
	 * @param input the inputs that the agent is observing
	*/
	@Override
	public Action run(Input input)
	{
		AtomicAction a = (AtomicAction) this.r.selectAction(input); //still using this??
		this.r.selectAction(input);
		
		return (OpenAIAction) a;
	}

	//@Override
	 /*
	  *Train the agent using the provided casebase
	 * @param casebase the casebase with all the cases the agent has observed.
	*/
	public void train(CaseBase casebase)
	{
		this.cb = casebase;

		if( this.r == null )
		{
			this.r = new TBReasoning(casebase);
		}

	}



}

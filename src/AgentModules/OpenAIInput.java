package AgentModules;

import org.jLOAF.inputs.AtomicInput;
import org.jLOAF.inputs.ComplexInput;
import org.jLOAF.inputs.Feature;
import org.jLOAF.inputs.Input;
import org.jLOAF.sim.SimilarityMetricStrategy;
import org.jLOAF.sim.*;

/*
 * OpenAIInput Object
 * represents the high level input that the agent will receive
 * @author Chad Peters
 * @since 2017 September
 */

public class OpenAIInput extends ComplexInput {

	private static final long serialVersionUID = 1L;
	public static final String NAME="Observation";
	private static SimilarityMetricStrategy simMet;
	/*
	 * Constructor
	 * @param name the name associated with the input
	 *
	 */
	public OpenAIInput(String name,ComplexSimilarityMetricStrategy sim) {
		super(name,sim);

	}


}

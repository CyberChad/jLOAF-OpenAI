import java.io.IOException;
import java.io.*;

import org.jLOAF.Agent;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.performance.PerformanceEvaluator;
import org.jLOAF.preprocessing.filter.CaseBaseFilter;
import org.jLOAF.preprocessing.filter.featureSelection.HillClimbingFeatureSelection;

import AgentModules.OpenAIAgent;
import CaseBaseCreation.LogFile2CaseBase;

/**
 * Test function for tutorial
 **/

class HelloJep
{
	public static void main(String[] args)
	{
		System.out.println("Hello jLOAF");
		
		//make instance of new agent
		OpenAIAgent myAgent = new OpenAIAgent();
		
		//generate training data casebase
		/* a caseBase is simply a list of cases, which is a full experiment done by an expert. */
		CaseBase myCaseBase = new CaseBase();
		
		
		//a list of simple features
		/* this class is just a representation of a double value
		that is taken by an atomic ation or an atomic input*/
		
		Feature f0 = new Feature(0);
		Feature f1 = new Feature(1);
		Feature f2 = new Feature(2);
		Feature f3 = new Feature(3);
		
		//the list of simple output actions the agent is capable of performing
		AtomicAction left = new AtomicAction("0");
		AtomicAction right = new AtomicAction("1");
		
		/* these strategies are used to find the similarity between either:
		 * atomic input: computes the inverse of the difference
		 * state input: Kth element in a trace, bound by minimum trace size
		 * */
		
		StateBasedSimilarity stateStrategy = new KOrderedSimilarity(1); //similarity(Input i1, Input i2)
		AtomicSimilarityMetricsStrategy atomicStrategy = new Equality(); //similarity(Input i1, Input i2)
		
		
		//the list of input-to-strategy the casebase is made of
		AtomicInput see0 = new AtomicInput("0", f0, atomicStrategy);
		AtomicInput see1 = new AtomicInput("1", f1, atomicStrategy);
		//AtomicInput see2 = new AtomicInput("2", f2, atomicStrategy);
		
		//populate the casebase
		myCaseBase.createThenAdd(see0, left, stateStrategy);
		myCaseBase.createThenAdd(see1, right, stateStrategy);
		
		// provide agent with training data
		//myAgent.train(new TBReasoning(myCaseBase));
	
	}//main
	
}



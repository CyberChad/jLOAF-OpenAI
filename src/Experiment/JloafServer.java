package Experiment;

import py4j.GatewayServer;

import java.util.Set;

import org.jLOAF.Agent;
import org.jLOAF.action.Action;
import org.jLOAF.agents.GenericAgent;
import org.jLOAF.casebase.Case;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.inputs.AtomicInput;
import org.jLOAF.inputs.ComplexInput;
import org.jLOAF.inputs.Feature;
import org.jLOAF.inputs.Input;

import org.jLOAF.reasoning.SimpleKNN;
import org.jLOAF.reasoning.WeightedKNN;
import org.jLOAF.reasoning.BayesianReasoner;
import org.jLOAF.reasoning.DynamicBayesianReasoner;
import org.jLOAF.reasoning.SimpleKNN;
import org.jLOAF.reasoning.TBReasoning;

import org.jLOAF.sim.AtomicSimilarityMetricStrategy;
import org.jLOAF.sim.atomic.Equality;
import org.jLOAF.sim.atomic.EuclideanDistance;
import org.jLOAF.sim.atomic.PercentDifference;
import org.jLOAF.sim.SimilarityMetricStrategy;
import org.jLOAF.sim.ComplexSimilarityMetricStrategy;
import org.jLOAF.sim.complex.GreedyMunkrezMatching;
import org.jLOAF.sim.complex.Mean;
import org.jLOAF.sim.complex.WeightedMean;


import AgentModules.OpenAIAgent;
import AgentModules.OpenAIAction;
import AgentModules.OpenAIInput;


public class JloafServer
{
	/**
	 * Use a jLOAF Agent to play with Open Gym
	 * @param args
	 */
	
	protected Agent myAgent;
	protected String src_file = "subsample.cb";
	protected CaseBase cb;
	
	ComplexSimilarityMetricStrategy complexStrat;
	
	public void JloafServer() //default constructor
	{
		//agent = new OpenAgent();
		System.out.println("** Instantiating JloafServer **");
		
		myAgent = new OpenAIAgent();
		cb = CaseBase.load(src_file);
		complexStrat = new Mean();
		
		System.out.println("** Training Agent **");
		//k =3;
		myAgent.train(new TBReasoning(cb));
	}
	
	public double nextAction(double[] obs)
	{
		System.out.println("** nextAction called from client **");
		
		int size = obs.length;
		
		for(int i=0; i < size; i++)
		{
			System.out.println("Index: "+i+" Feature: "+obs[i]);
		}
		
		//Input input = new ComplexInput("Observation", complexStrat);
		
		//Action act = myAgent.run(input);
		
			
		//return act.getName(); //for now
		return 0;
	}
	
	
	public static void main(String [] args)
	{
		System.out.println("-- Initialize JLOAF Server --");
		
		GatewayServer server = new GatewayServer(new JloafServer());
		server.start();
		System.out.println("Py4J Gateway Server Started");
		
		//a.setR(new SimpleKNN(k,cb));
		
		//Action predicted = a.getR().selectAction(c6.getInput());
		//System.out.println("Action Predicted: " + predicted.getName() +", and the correct action is " + a6.getName() + ".");
		
	}//main
}//class


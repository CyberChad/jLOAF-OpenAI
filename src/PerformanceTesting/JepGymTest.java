package PerformanceTesting;

import java.io.IOException;
import java.io.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.jLOAF.casebase.Case;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.inputs.AtomicInput;
import org.jLOAF.inputs.ComplexInput;
import org.jLOAF.inputs.Feature;
import org.jLOAF.sim.SimilarityMetricStrategy;
import org.jLOAF.sim.AtomicSimilarityMetricStrategy;
import org.jLOAF.sim.ComplexSimilarityMetricStrategy;
import org.jLOAF.sim.StateBasedSimilarity;
import org.jLOAF.sim.StateBased.KOrderedSimilarity;
import org.jLOAF.sim.StateBased.OrderedSimilarity;
import org.jLOAF.sim.atomic.EuclideanDistance;
import org.jLOAF.sim.complex.Mean;
import org.jLOAF.sim.complex.WeightedMean;
import org.jLOAF.weights.SimilarityWeights;

import AgentModules.OpenAIAction;
import AgentModules.OpenAIAction.Actions;
import AgentModules.OpenAIInput;

import org.jLOAF.*;
import org.jLOAF.Agent;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.performance.PerformanceEvaluator;
import org.jLOAF.preprocessing.filter.CaseBaseFilter;
import org.jLOAF.preprocessing.filter.featureSelection.HillClimbingFeatureSelection;

import org.jLOAF.reasoning.*;

import org.jLOAF.action.AtomicAction;
import org.jLOAF.sim.atomic.Equality;
import org.jLOAF.sim.AtomicSimilarityMetricStrategy;

import AgentModules.OpenAIAgent;
import CaseBaseCreation.LogFile2CaseBase;



/**
 * Test function for tutorial
 **/

class JepGymTest
{

	private static boolean DEBUG = true;	
	
	protected static AtomicSimilarityMetricStrategy atomicStrategy = new EuclideanDistance();
	protected static ComplexSimilarityMetricStrategy complexStrategy = new Mean();
	//protected SimilarityMetricStrategy gymStrategy = new WeightedMean(new SimilarityWeights());
	//protected StateBasedSimilarity stateBasedStrategy = new KOrderedSimilarity(1);
	protected static StateBasedSimilarity stateBasedStrategy = new OrderedSimilarity();


	/*
	 * outputs the casebase passed to it in a .cb file with the name also passed to it
	 * @param cb the casebase to be saved
	 * @param outputFile the file in which the casebase will be saved
	 */
	private static void saveCaseBase(CaseBase cb, String outputFile)
	{
		CaseBase.save(cb, outputFile);
	}


	/*
	 * creates a casebase from a logfile passed to it.
	 * @param file a file to be parsed to a casebase
	 * @return a casebase created from the logfile.
	 */
	private static String parseLogFile(String file1,String file2)
	{
		File file= new File(file1);
		CaseBase cb = new CaseBase();
		
		System.out.println("***parsing log file***");

		int counter=0, index=0, row_length = 4;
		double[] entry = new double[row_length];		
		double next_double=0;
		
		
		try
		{
			Scanner sc = new Scanner(file);

			while(sc.hasNextLine())
			{
				
				try
				{
					index = counter % row_length;
					next_double = sc.nextDouble();
										
					if (DEBUG) System.out.println("Index: "+index+" Value: "+next_double);
	
					entry[index] = next_double;
	
					if( index == row_length )
					{						
						createCase( cb, entry);
						if (DEBUG) System.out.println("Line: "+counter+" ");
					}
					
					counter++;					
				}
				catch (NoSuchElementException e)
				{
					break;
				}

			}//while
			sc.close();
			
		}//try
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		saveCaseBase(cb, file2);
		System.out.println("done with creating one caseBase");
		return file2;
	
	}//parseLogFile


	/*
	 * creates a case from the double values passed to it and then adds it to the casebase
	 * @param cb2 the casebase of the observed expert
	 * @param entry an array of double values represent the parameters of the actions and the inputs
	 */
	private static void createCase(CaseBase cb2, double[] entry)
	{
		int entry_len = entry.length;
		
		OpenAIAction action= new OpenAIAction(Actions.values()[(int)entry[entry_len]-1].getAction());
		OpenAIInput input = new OpenAIInput(OpenAIInput.NAME,complexStrategy);
		
		Feature f0 = new Feature(entry[0]);
		Feature f1 = new Feature(entry[1]);
		Feature f2 = new Feature(entry[2]);
		Feature f3 = new Feature(entry[3]);
		
		AtomicInput input0 = new AtomicInput("input0",f0,atomicStrategy);
		AtomicInput input1 = new AtomicInput("input1",f1,atomicStrategy);
		AtomicInput input2 = new AtomicInput("input2",f2,atomicStrategy);
		AtomicInput input3 = new AtomicInput("input3",f3,atomicStrategy);

		input.add(input0);
		input.add(input1);
		input.add(input2);
		input.add(input3);
		
		//AtomicAction action = new AtomicAction(""+entry[entry_len-1]);

		
		//System.out.println(vci.getChildNames().size());
		
		Case thisCase = new Case(input,action);
		
		cb2.createThenAdd(input,action,stateBasedStrategy);

	}//createCase

	public static void main(String[] args)
	{
		System.out.println("Hello jLOAF");
		
		//make instance of new agent
		OpenAIAgent myAgent = new OpenAIAgent();
		
		//generate training data casebase
		/* a caseBase is simply a list of cases, which is a full experiment done by an expert. */
		CaseBase myCaseBase = new CaseBase();
		
		/* import log file
		 * */
			
		String src_file = "subsample.log";
		String dst_file = "subsample.cb";
		
		parseLogFile(src_file,dst_file);
		
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
		AtomicSimilarityMetricStrategy atomicStrategy = new Equality();
		
		
		//the list of input-to-strategy the casebase is made of
		AtomicInput see0 = new AtomicInput("0", f0, atomicStrategy);
		AtomicInput see1 = new AtomicInput("1", f1, atomicStrategy);
		//AtomicInput see2 = new AtomicInput("2", f2, atomicStrategy);
		
		//populate the casebase
		myCaseBase.createThenAdd(see0, left, stateStrategy);
		myCaseBase.createThenAdd(see1, right, stateStrategy);
		
		// provide agent with training data
		
		//myAgent.train(new TBReasoning(myCaseBase));
		
		myAgent.train(myCaseBase);
	
	}//main
	
}



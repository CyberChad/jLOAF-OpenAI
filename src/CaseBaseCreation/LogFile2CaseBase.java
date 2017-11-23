package CaseBaseCreation;


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


/*
 * LogFile2CaseBase Object
 * used to convert logfile to .cb files
 * @author Chad Peters
 * @since 2017 September
 */
public class LogFile2CaseBase
{

	protected static AtomicSimilarityMetricStrategy atomicStrategy = new EuclideanDistance();
	protected static ComplexSimilarityMetricStrategy complexStrategy = new Mean();
	//protected SimilarityMetricStrategy vacumStrategy = new WeightedMean(new SimilarityWeights());
	protected static StateBasedSimilarity stateBasedStrategy = new KOrderedSimilarity(1);


	private static boolean DEBUG = true;
	
	/*
	 * outputs the casebase passed to it in a .cb file with the name also passed to it
	 * @param cb the casebase to be saved
	 * @param outputFile the file in which the casebase will be saved
	 */
	private void saveCaseBase(CaseBase cb, String outputFile) {

		CaseBase.save(cb, outputFile);
	}


	/*
	 * creates a casebase from a logfile passed to it.
	 * @param file a file to be parsed to a casebase
	 * @return a casebase created from the logfile.
	 */
	public String parseLogFile(String file1,String file2)
	{
		File file= new File(file1);
		CaseBase cb = new CaseBase();
		
		System.out.println("***parsing log file***");

		int counter=0, index=0, row_length = 5;
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
	
					if( index == row_length-1 )
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
		if (DEBUG) System.out.println("*** creating Case ***");
		
		int entry_len = entry.length;
		
		if (DEBUG) System.out.println("Entry Length: "+entry_len);
		
		//OpenAIAction action= new OpenAIAction(Actions.values()[(int)entry[entry_len]-1].getAction());
		//OpenAIInput input = new OpenAIInput(OpenAIInput.NAME,complexStrategy);
		
		if (DEBUG) System.out.println("..Creating Features...");
		//static size, need to loop through variable size feature space
		Feature f0 = new Feature(entry[0]);
		Feature f1 = new Feature(entry[1]);
		Feature f2 = new Feature(entry[2]);
		Feature f3 = new Feature(entry[3]);
		
		if (DEBUG) System.out.println("..Creating Atomic Inputs...");
		
		AtomicInput input0 = new AtomicInput("input0",f0,atomicStrategy);
		AtomicInput input1 = new AtomicInput("input1",f1,atomicStrategy);
		AtomicInput input2 = new AtomicInput("input2",f2,atomicStrategy);
		AtomicInput input3 = new AtomicInput("input3",f3,atomicStrategy);

		if (DEBUG) System.out.println("..Creating Complex Input...");
		
		OpenAIInput input = new OpenAIInput("observation",complexStrategy);
		
		input.add(input0);
		input.add(input1);
		input.add(input2);
		input.add(input3);
		
		//AtomicAction action = new AtomicAction(""+entry[entry_len-1]);
		
		
		if (DEBUG) System.out.println("..Creating Atomic Action...");
		
		String move = "";		
		
		if(entry[4]==1)
		{
			move = "RIGHT";
		}
		else
		{
			move = "LEFT";
		}
		
		if (DEBUG) System.out.println("Action Observed: "+move);
		
		OpenAIAction action = new OpenAIAction(move);
		
		//Feature f4 = new Feature(entry[4]);
		
		//action.setFeature(f4);		
		//entry[entry_len-1]);
		
		//System.out.println(vci.getChildNames().size());
		
		//Case thisCase = new Case(input,action);
		
		cb2.createThenAdd(input,action,stateBasedStrategy);

	}//createCase

}

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


	private static boolean DEBUG = false;
	
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
		
		System.out.println("***parsing log file: "+file1);

		int counter=0;
		double next_double=0;
		
		String line = null;
		
		String[] entries_s = null;
		double[] entries_d = null; 
		
		try
		{
			Scanner sc = new Scanner(file);
			
			//get first line
			if (sc.hasNextLine())
			{
				line = sc.nextLine(); 
			}
			
			line = line.replace("[", "");
			line = line.replace("]", "");
			line = line.replace("'", "");
			line = line.replace(" ", "");
			
			if(DEBUG) System.out.println("Clean Row: "+line);
			
			//initialize entry array
			entries_s = line.split(",");
			int row_length = entries_s.length;			
			entries_d = new double[row_length];			
			sc.reset(); //back to beginning
			
			if(DEBUG) System.out.println("Row length: "+row_length);
			
			String tmpd;
			
			while(sc.hasNextLine())
			{
				line = sc.nextLine();				
				
				if(DEBUG) System.out.println("Next line: "+line);

				line = line.replace("[", "");
				line = line.replace("]", "");
				line = line.replace("'", "");
				line = line.replace(" ", "");
				
				if(DEBUG) System.out.println("Next clean line: "+line);
				
				try
				{
					if(DEBUG)System.out.println("enter try");
					entries_s = line.split(",");								
					entries_d = new double[row_length];
					
					for( int index=0; index < row_length; index++)
					{
						if(DEBUG)System.out.println("For block ix: "+index);
						
						tmpd = entries_s[index];
						if(DEBUG)System.out.println("next_double_s: "+tmpd);
						next_double = Double.valueOf(tmpd).doubleValue();
						if(DEBUG)System.out.println("next_double_d: "+next_double);
						if (DEBUG) System.out.println("Index: "+index+" Value: "+next_double);
						entries_d[index] = next_double;
					}
					
					createCase( cb, entries_d);
					if (DEBUG) System.out.println("Line: "+counter+" ");
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
		int num_feats = entry_len-1;
		
		if (DEBUG) System.out.println("Entry Length: "+entry_len);
		
		//OpenAIAction action= new OpenAIAction(Actions.values()[(int)entry[entry_len]-1].getAction());
		//OpenAIInput input = new OpenAIInput(OpenAIInput.NAME,complexStrategy);
		
		if (DEBUG) System.out.println("..Creating Features...");

		//loop through variable size feature space		
				
		Feature[] features = new Feature[entry_len];		
		AtomicInput[] inputs = new AtomicInput[entry_len];		
		OpenAIInput input = new OpenAIInput("observation",complexStrategy);
		
		if (DEBUG) System.out.println("..Creating Inputs...");
		
		for( int i=0; i < num_feats; i++)
		{
			features[i] = new Feature(entry[i]);
			inputs[i] = new AtomicInput("input"+i,features[i],atomicStrategy);
			input.add(inputs[i]);
		}
		
		String move = "";
		
		move = Double.toString(entry[entry_len-1]);
		
		if (DEBUG) System.out.println("Action Observed: "+move);
		
		OpenAIAction action = new OpenAIAction(move);
		
		//System.out.println(vci.getChildNames().size());
		//Case thisCase = new Case(input,action);
		
		cb2.createThenAdd(input,action,stateBasedStrategy);
	}//createCase

}

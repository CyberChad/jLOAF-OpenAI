package Environment;

import py4j.ClientServer;
import py4j.GatewayServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import java.math.*;

import org.jLOAF.Agent;
import org.jLOAF.action.Action;
import org.jLOAF.action.AtomicAction;
import org.jLOAF.agents.GenericAgent;
import org.jLOAF.casebase.Case;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.inputs.AtomicInput;
import org.jLOAF.inputs.ComplexInput;
import org.jLOAF.inputs.Feature;
import org.jLOAF.inputs.Input;
import org.jLOAF.inputs.StateBasedInput;
import org.jLOAF.preprocessing.filter.CaseBaseFilter;
import org.jLOAF.preprocessing.filter.featureSelection.HillClimbingFeatureSelection;
import org.jLOAF.reasoning.BayesianReasoner;
import org.jLOAF.reasoning.KDReasoning;
import org.jLOAF.reasoning.SimpleKNN;
import org.jLOAF.reasoning.TBReasoning;
import org.jLOAF.reasoning.WeightedKNN;
import org.jLOAF.sim.SimilarityMetricStrategy;
import org.jLOAF.sim.StateBasedSimilarity;
import org.jLOAF.sim.AtomicSimilarityMetricStrategy;
import org.jLOAF.sim.atomic.EuclideanDistance;
import org.jLOAF.sim.ComplexSimilarityMetricStrategy;
import org.jLOAF.sim.atomic.PercentDifference;
import org.jLOAF.sim.complex.Mean;
import org.jLOAF.sim.StateBased.KOrderedSimilarity;
import org.jLOAF.sim.StateBased.KUnorderedSimilarity;
import org.jLOAF.sim.StateBased.OrderedSimilarity;

import AgentModules.OpenAIAction;
import AgentModules.OpenAIAgent;
import AgentModules.OpenAIInput;
import AgentModules.OpenAIAction.Actions;
import py4j.GatewayServer;


@SuppressWarnings("unused")
public class JloafClient
{
	/**
	 * Use a jLOAF Agent to play with Open Gym
	 * @param args
	 */
	
	protected static OpenAIAgent myAgent;
	//protected String src_file = "subsample.cb";
	protected static CaseBase cb;
	
	
	/* LogFile2CaseBase vars */
	protected static String log_file = "tests/lander5000.log";			
	protected static String cb_file = "tests/lander5000.cb";	
	protected static int features = 0;
	
	ComplexSimilarityMetricStrategy complexStrat;
	
	
	protected static AtomicSimilarityMetricStrategy atomicStrategy = new EuclideanDistance();
	protected static ComplexSimilarityMetricStrategy complexStrategy = new Mean();
	//protected SimilarityMetricStrategy gymStrategy = new WeightedMean(new SimilarityWeights());
	//protected StateBasedSimilarity stateBasedStrategy = new KOrderedSimilarity(1);
	protected static StateBasedSimilarity stateBasedStrategy = new KOrderedSimilarity(1);

	private static boolean DEBUG = true;	
	
	public JloafClient() //default constructor
	{
		//agent = new OpenAgent();
		System.out.println("** Instantiating JloafServer **");
		
		cb = CaseBase.load(cb_file);
		myAgent = new OpenAIAgent();
		complexStrat = new Mean();
		
		System.out.println("** Training Agent **");
		int k = 5;
		myAgent.train(new WeightedKNN(k,cb));		
	}
	

	public String nextAction(double[] obs)
	{
		System.out.println("** nextAction called from client **");
		
		int size = obs.length;
		
		
		if (DEBUG) System.out.println("..Creating Complex Input...");
		
		
		OpenAIInput input = new OpenAIInput("observation",complexStrategy);
		
		StateBasedInput stateInput = new StateBasedInput("test",stateBasedStrategy);
		

		
		//testCase.createThenAdd(input, a, sim);
		//StateBasedInput input2 = new StateBasedInput()
		
		//AtomicAction action = new AtomicAction(""+entry[entry_len-1]);
		
		if (DEBUG) System.out.println("..Creating Atomic Action...");
		AtomicAction action = null;
				

		//Feature[] features = new Feature[row_length-1];
		//AtomicInput[] inputs = new AtomicInput[row_length-1];
		
		for( int i=0; i< obs.length; i++)
		{
			System.out.println(obs[i]);
		}
			
		Feature f0 = new Feature(obs[0]);
		Feature f1 = new Feature(obs[1]);
		Feature f2 = new Feature(obs[2]);
		Feature f3 = new Feature(obs[3]);
						
		//static size, need to loop through variable size feature space
		
		AtomicInput i0 = new AtomicInput("test",f0,atomicStrategy);
		AtomicInput i1 = new AtomicInput("test",f1,atomicStrategy);
		AtomicInput i2 = new AtomicInput("test",f2,atomicStrategy);
		AtomicInput i3 = new AtomicInput("test",f3,atomicStrategy);
		
		//features[index] = new Feature(next_double);
		
		//bigint = new Integer(index);
		
		//inputs[index] = new AtomicInput("test"+bigint.toString(),features[index],atomicStrategy);
		
		if (DEBUG) System.out.println("..adding to complex Input...");
		
		
		input.add(i0);
		input.add(i1);	
		input.add(i2);
		input.add(i3);
		
		
		stateInput.setInput(input);
		
		//action = new AtomicAction("move");
		//action.setFeature(new Feature(next_double));
		
		//c0 = new Case(i0,action);
		
		if (DEBUG) System.out.println("..getting predicted action...");
		
		
		OpenAIAction predicted = (OpenAIAction)myAgent.run(stateInput);
		
		String returnVal = null;
		
		returnVal = predicted.getName();
		
		if( returnVal != null )
		{
			System.out.println("return val: "+returnVal);
		}
		else
		{
			returnVal = "";
		}
				
		//predicted = a.getR().selectAction(c0.getInput());						
		
		//System.out.println("Action Predicted: " + predicted.getName());
		
				
		return returnVal;
	}
	


	/*
	 * outputs the casebase passed to it in a .cb file with the name also passed to it
	 * @param cb the casebase to be saved
	 * @param outputFile the file in which the casebase will be saved
	 */
	private static void saveCaseBase(CaseBase cb, String outputFile)
	{
		if (DEBUG) System.out.println("--- Saving Case Base as: "+outputFile);
		CaseBase.save(cb, outputFile);
	}

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

		//static size, need to loop through variable size feature space		
		/*
		
		Feature[] features = new Feature[entry_len];		
		for( int i=0; i < num_feats; i++)
		{
			features[i] = new Feature(entry[i]);
		}

		*/
		
		Feature f0 = new Feature(entry[0]);
		Feature f1 = new Feature(entry[1]);
		Feature f2 = new Feature(entry[2]);
		Feature f3 = new Feature(entry[3]);
		Feature f4 = new Feature(entry[4]);
		Feature f5 = new Feature(entry[5]);
		Feature f6 = new Feature(entry[6]);
		Feature f7 = new Feature(entry[7]);
		
		
		
		if (DEBUG) System.out.println("..Creating Atomic Inputs...");
		
		//AtomicInput[] inputs = new AtomicInputs[]
		
		AtomicInput input0 = new AtomicInput("input0",f0,atomicStrategy);
		AtomicInput input1 = new AtomicInput("input1",f1,atomicStrategy);
		AtomicInput input2 = new AtomicInput("input2",f2,atomicStrategy);
		AtomicInput input3 = new AtomicInput("input3",f3,atomicStrategy);
		AtomicInput input4 = new AtomicInput("input3",f4,atomicStrategy);
		AtomicInput input5 = new AtomicInput("input3",f5,atomicStrategy);
		AtomicInput input6 = new AtomicInput("input3",f6,atomicStrategy);
		AtomicInput input7 = new AtomicInput("input3",f7,atomicStrategy);

		if (DEBUG) System.out.println("..Creating Complex Input...");
		
		OpenAIInput input = new OpenAIInput("observation",complexStrategy);
		
		input.add(input0);
		input.add(input1);
		input.add(input2);
		input.add(input3);
		input.add(input4);
		input.add(input5);
		input.add(input6);
		input.add(input7);
		
		//AtomicAction action = new AtomicAction(""+entry[entry_len-1]);
		
		
		if (DEBUG) System.out.println("..Creating Atomic Action...");
		
		String move = "";
		//double action = entry[8];
		
		move = Double.toString(entry[8]);
		
		/*
		if(entry[8]==1)
		{
			move = "RIGHT";
		}
		else
		{
			move = "LEFT";
		}
		
		*/
		
		
		if (DEBUG) System.out.println("Action Observed: "+move);
		
		OpenAIAction action = new OpenAIAction(move);
		
		//Feature f4 = new Feature(entry[4]);
		
		//action.setFeature(f4);		
		//entry[entry_len-1]);
		
		//System.out.println(vci.getChildNames().size());
		
		//Case thisCase = new Case(input,action);
		
		cb2.createThenAdd(input,action,stateBasedStrategy);

	}//createCase

	/*
	 * creates a casebase from a logfile passed to it.
	 * @param file a file to be parsed to a casebase
	 * @return a casebase created from the logfile.
	 */
	private static String parseLogFile(String file1, String file2)
	{
		File file= new File(file1);
		CaseBase cb = new CaseBase();
		
		System.out.println("***parsing log file***");

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
			
			//initialize entry array
			entries_s = line.split("\\s");
			int row_length = entries_s.length;			
			entries_d = new double[row_length];			
			sc.reset(); //back to beginning
			
			System.out.println("Row length: "+row_length);
			
			
			while(sc.hasNextLine())
			{
				line = sc.nextLine();				
				
				try
				{
					entries_s = line.split("\\s");								
					entries_d = new double[row_length];
					
					for( int index=0; index < row_length; index++)
					{
						next_double = sc.nextDouble();
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



	
	
	private static void testLogToCaseBase()
	{
		System.out.println("-- Test Log to Case Base --");
		//init test agent
		OpenAIAgent testAgent = new OpenAIAgent();
		
		parseLogFile(log_file,cb_file);
		
	}//testLogToCaseBase		

	private static void testLoadCaseBase()
	{
		System.out.println("-- Test Load Case Base --");
	
		CaseBase cb = CaseBase.load(cb_file);
		
		System.out.println(cb.toString());
	
	}//testLoadCaseBase
		
	private static void testTrainAgent()
	{
		System.out.println("-- Train Test Agent --");
		//testAgent.train(new SimpleKNN(5,cb));
		
		
		//test input features from the environment
		Feature f1 = new Feature(1.0);
		Feature f2 = new Feature(-2.0);
		Feature f3 = new Feature(1.3);
		Feature f4 = new Feature(-0.5);
		
		//test action features to the environment
		Feature f5 = new Feature(0);
		Feature f6 = new Feature(1);
		
		AtomicSimilarityMetricStrategy simStratEuc = new EuclideanDistance();
		
		ComplexSimilarityMetricStrategy simStratMean = new Mean();
		
		KUnorderedSimilarity sim = new KUnorderedSimilarity(3);
					
		//SimilarityMetricStrategy simMetStratMean = new Mean();
		
		AtomicInput ai1 = new AtomicInput("1",f1,simStratEuc);
		AtomicInput ai2 = new AtomicInput("2",f2,simStratEuc);
		AtomicInput ai3 = new AtomicInput("3",f3,simStratEuc);
		AtomicInput ai4 = new AtomicInput("4",f4,simStratEuc);
		
		OpenAIInput ci1 = new OpenAIInput("observation", complexStrategy);
		
		ci1.add(ai1);
						
		AtomicAction a1 = new AtomicAction("left");
		a1.setFeature(f5);
		AtomicAction a2 = new AtomicAction("right");
		a2.setFeature(f6);
		
	}//testTrainAgent
		
	private static void testRunAgentFromFile()
	{
		System.out.println("--- Test Run Agent ---");
		
		File file = new File(log_file);
		
		CaseBase cb = CaseBase.load(cb_file);
		CaseBaseFilter ft = new HillClimbingFeatureSelection(null);
		
		System.out.println("...Loading Agent...");
		
		//create generic agent
		int k = 2;
		
		int total = 0;
		int right = 0;
		OpenAIAgent testAgent = new OpenAIAgent();
		testAgent.setR(new SimpleKNN(k,cb));
		//testAgent.setR(new WeightedKNN(k,cb));
		//testAgent.setR(new KDReasoning(cb));
				
		int counter=0, index=0, row_length = 9;				
		double next_double=0;		
		
		System.out.println("...reading from observations...");
		
		if (DEBUG) System.out.println("..Creating Complex Input...");
		
		CaseBase testCase = new CaseBase();
		
		
		
		OpenAIInput input = new OpenAIInput("observation",complexStrategy);
		
		StateBasedInput stateInput = new StateBasedInput("test",stateBasedStrategy);
		

		
		//testCase.createThenAdd(input, a, sim);
		//StateBasedInput input2 = new StateBasedInput()
		
		//AtomicAction action = new AtomicAction(""+entry[entry_len-1]);
		
		if (DEBUG) System.out.println("..Creating Atomic Action...");
		AtomicAction action = null;
				

		//Feature[] features = new Feature[row_length-1];
		//AtomicInput[] inputs = new AtomicInput[row_length-1];
			
		Feature f0 = null;
		AtomicInput i0 = null;
		Case c0 = null;
		
		OpenAIAction predicted = null;
		Integer bigint = null;
		
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
						
					//static size, need to loop through variable size feature space
					
					f0 = new Feature(next_double);
					i0 = new AtomicInput("test",f0,atomicStrategy);
					
					//features[index] = new Feature(next_double);
					
					//bigint = new Integer(index);
					
					//inputs[index] = new AtomicInput("test"+bigint.toString(),features[index],atomicStrategy);
					
					input.add(i0);
						
					if( index == row_length-1 )
					{					
						//String actualAction = "";
						/*
						if(next_double == 0)
						{
							actualAction = "LEFT";
						}
						else
						{
							actualAction = "RIGHT";
						}
						
						*/
						if (DEBUG) System.out.println("Actual Action: "+next_double);
						
						stateInput.setInput(input);
						
						//action = new AtomicAction("move");
						//action.setFeature(new Feature(next_double));
						
						//c0 = new Case(i0,action);
						
						predicted = (OpenAIAction)testAgent.run(stateInput);
						
						//predicted = a.getR().selectAction(c0.getInput());						
						
										
						String next = Double.toString(next_double); 
						
						if ( next.equals(predicted.getName()) )
						{
							right++;
						}
						total++;
						System.out.println("Action Predicted: " + predicted.getName());
						input = null;
						input = new OpenAIInput("observation",complexStrategy);
						stateInput = null;
						stateInput = new StateBasedInput("test",stateBasedStrategy);
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
		
		//Action a6 = new Action("down");
		//Case c6 = new Case(i6,a6);
		
		//Action predicted = a.getR().selectAction(c6.getInput());
		
		System.out.println("Actions Correct: " + right +" out of " + total);
		
		
	
	
	}//testRunAgent

	public interface GymEnv
	{
		public String testCommand(int i, String s);
		public String getInfo();
		
		public void makeEnv(String env);
		public void resetEnv();	
		
	    public double[] getActions();
	    public double[] getObservations();
		public double[] doAction(int action);	    
	}



	
	public static void main(String [] args)
	{
		System.out.println("-- Initialize jLOAF Client --");
		
        
        // We get an entry point from the Python side
        // Java calls Python without ever having been called from Python
	    
	    /*
	    ClientServer clientServer = new ClientServer(null);
	    GymEnv my_env = (GymEnv) clientServer.getPythonServerEntryPoint(new Class[] { GymEnv.class });
        System.out.println(my_env.testCommand(2, "Hello World"));
        clientServer.shutdown();
	     */
	
		//testLogToCaseBase();
		//testLoadCaseBase();
		//testTrainAgent();		
		testRunAgentFromFile();
		
		
	}//main
}//class


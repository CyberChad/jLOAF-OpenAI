package Environment;

import py4j.ClientServer;
import py4j.GatewayServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Random;
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
import CaseBaseCreation.LogFile2CaseBase;
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
	//protected static String log_file = "lander1.log";			
	protected static String log_file = "lunar10000";
	protected static String cb_file = "lunar10000.cb";	
	//protected static int features = 0;
	
	ComplexSimilarityMetricStrategy complexStrat;
	
	
	protected static AtomicSimilarityMetricStrategy atomicStrategy = new EuclideanDistance();
	protected static ComplexSimilarityMetricStrategy complexStrategy = new Mean();
	//protected SimilarityMetricStrategy gymStrategy = new WeightedMean(new SimilarityWeights());
	//protected StateBasedSimilarity stateBasedStrategy = new KOrderedSimilarity(1);
	protected static StateBasedSimilarity stateBasedStrategy = new KOrderedSimilarity(1);

	private static boolean DEBUG = true;	
	private static boolean DEBUG_MSG = false;
	private static boolean DEBUG_ACTION = true;

	private static OpenAIAgent testAgent = new OpenAIAgent();
	
	public JloafClient() //default constructor
	{
		//agent = new OpenAgent();
		System.out.println("** Instantiating JloafClient **");
		
		myAgent = new OpenAIAgent();		
		
		cb = CaseBase.load(cb_file);
		
		
		//complexStrat = new Mean();
		
		//---- Move this to a new train function ----
		
		 
		System.out.println("** Training Agent **");
		int k = 1;
		//myAgent.train(new WeightedKNN(k,cb));
		
		
		OpenAIAgent testAgent = new OpenAIAgent();
		testAgent.setR(new SimpleKNN(k,cb));
				
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
		
		File file = new File(log_file+".log");
				
		LogFile2CaseBase lfcb = new LogFile2CaseBase();

		String cb_file = lfcb.parseLogFile(log_file+".log",log_file+".cb");
		
		CaseBase cb = CaseBase.load(cb_file);
		
		CaseBaseFilter ft = new HillClimbingFeatureSelection(null);
		
		System.out.println("...Loading Agent...");
		
		//create generic agent
		int k = 5;
		
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
		
		//CaseBase testCase = new CaseBase();
		
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
		
	    ClientServer clientServer = new ClientServer(null);
	    GymEnv gym = (GymEnv) clientServer.getPythonServerEntryPoint(new Class[] { GymEnv.class });
	    
	    System.out.println(gym.testCommand(42, "The meaning of life"));	    
	    System.out.println(gym.makeEnv("LunarLander-v2"));
		
		try
		{
			Scanner sc = new Scanner(file);
			index = 0;
			
			while(sc.hasNextLine())
			{				
				try
				{					
					next_double = sc.nextDouble();
										
					if (DEBUG_ACTION) System.out.println("Index: "+index+" Value: "+next_double);
						
					f0 = new Feature(next_double);
					String ac0 = Double.toString(next_double);
					i0 = new AtomicInput("input"+index,f0,atomicStrategy);
					//i0 = new AtomicInput("test",f0,atomicStrategy);
				
					input.add(i0);
						
					if( index == row_length-1 )
					{					
						if (DEBUG_ACTION) System.out.println("Actual Action: "+next_double);
						
						stateInput.setInput(input);
						
						predicted = (OpenAIAction)testAgent.run(stateInput);

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
						
						index=0;
					}
					
					counter++;	
					index++;
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
				
		System.out.println("Actions Correct: " + right +" out of " + total);		
		System.out.println("Shutting down server");
		clientServer.shutdown();
	
	
	}//testRunAgent

	public interface GymEnv
	{
		// --- Debug ---
		public String testCommand(int i, String s);
		public String getInfo();
		
		// --- Init & Reset ---
		public String makeEnv(String env);
		public String resetEnv();	
		public boolean isDone();	

		//--- MOVE TO PERCEPTION ---
	    public double[] getActions();
	    public double[] getObservations();
	    
	    //--- MOVE TO MOTORCONTROL ---
		public String doAction(int action);	    
	}
	
	private static void testGymDoor()
	{
		
		System.out.println("--- Test Gym Door ---");
		
		File file = new File(log_file+".log");
				
		LogFile2CaseBase lfcb = new LogFile2CaseBase();

		String cb_file = lfcb.parseLogFile(log_file+".log",log_file+".cb");
		
		//CaseBase cb = CaseBase.load(cb_file);
		CaseBase cb = CaseBase.load(log_file+".cb");
		
		CaseBaseFilter ft = new HillClimbingFeatureSelection(null);
		
		System.out.println("...Loading Agent...");

		//create generic agent
		int k = 1;
		//testAgent.setR(new SimpleKNN(k,cb));
		testAgent.setR(new SimpleKNN(k,cb));

		System.out.println("...Connecting to Gym Server...");
		
	    ClientServer clientServer = new ClientServer(null);
	    GymEnv gym = (GymEnv) clientServer.getPythonServerEntryPoint(new Class[] { GymEnv.class });
	    
	    System.out.println(gym.testCommand(42, "The meaning of life"));
	    
	    System.out.println(gym.makeEnv("LunarLander-v2"));
	    
	    //System.out.println(gym.resetEnv());
	    
	    String state;
	    double rand = 0;
		int total = 0;
		int right = 0;
	    int next_action = 0; 
	    int num_rounds = 10;
	    
	    for(int i=0; i < num_rounds; i++)
	    {
	    	System.out.println("Starting round: "+i);

	    	state = gym.doAction(next_action); //first call to reset state
		    
		    while (!gym.isDone()) 
		    {
		    	//get next action from the agent	    			
		    	next_action = nextAction(state);  	    	
		    	
		    	//get observation from the environment	    	
		    	state = gym.doAction(next_action);
		    	
		    	System.out.println(state);
		    	
		    }
		    
		    System.out.println(gym.resetEnv());
	    	
	    }
	    
		clientServer.shutdown();
	}
	
	private static int nextAction(String state)
	{
		System.out.println("** nextAction called from client **");
			
		if(DEBUG_MSG)System.out.println("Raw State: "+state);
		
		double next_double=0;
		
		String line = null;
				
		String[] entries_s = null;
		double[] entry = null; 
		
		
		//strip unnecessary characters from observation
    	state = state.replace("[","");
    	state = state.replace("]","");
    	
    	if(DEBUG_MSG)System.out.println("Clean State: "+state);
		
		
		StateBasedInput stateInput = new StateBasedInput("test",stateBasedStrategy);
		
		//initialize entry array
		entries_s = state.split(",");
		//Scanner sc = new Scanner(entries_s);
		int row_length = entries_s.length;			
		entry = new double[row_length];			
		
		System.out.println("Features Length: "+row_length);
				
		for( int index=0; index < row_length; index++)
		{	
			String tmpentry = entries_s[index];
			if (DEBUG_MSG) System.out.println("String Double: "+tmpentry); 						
			next_double = Double.parseDouble(entries_s[index]);
			if (DEBUG_MSG) System.out.println("Double Double: "+next_double);
			if (DEBUG_MSG) System.out.println("Index: "+index+" Value: "+next_double);
			entry[index] = next_double;
		}
			
				

		//************** NOW Create Action and get Predicted ***************
		
		
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
		
		Feature f0 = null;
		AtomicInput i0=null;
		
		if (DEBUG) System.out.println("..Creating Inputs...");
		
		for( int i=0; i < num_feats; i++)
		{
			features[i] = new Feature(entry[i]);			
			inputs[i] = new AtomicInput("input"+i,features[i],atomicStrategy);
			input.add(inputs[i]);
		}

		stateInput.setInput(input);
		
		//OpenAIAction predicted = (OpenAIAction)testAgent.run(stateInput);
		OpenAIAction predicted = (OpenAIAction)testAgent.run(stateInput);
		
		String returnValName = predicted.getName();
		
		if(DEBUG_ACTION) System.out.println("Predicted Name: "+returnValName);
		
		double returnVald = Double.parseDouble(returnValName);
		
		if(DEBUG_ACTION) System.out.println("Predicted Value D: "+returnVald);
				
		int returnVali = (int) returnVald;
		
		if(DEBUG_ACTION) System.out.println("Predicted Value I: "+returnVali);
				
		//predicted = a.getR().selectAction(c0.getInput());						
		
		//System.out.println("Action Predicted: " + predicted.getName());
				
		return returnVali;
	}

	
	public static void main(String [] args)
	{
		System.out.println("-- Initialize jLOAF Client --");
		//myAgent = new OpenAIAgent();		
		
		//cb = CaseBase.load(cb_file);		
		
		//complexStrat = new Mean();
		
		//---- Move this to a new train function ----
		
		 
		//System.out.println("** Training Agent **");
		//int k = 1;
		//myAgent.train(new WeightedKNN(k,cb));
		
		
		//OpenAIAgent testAgent = new OpenAIAgent();
		//testAgent.setR(new SimpleKNN(k,cb));		
		
        
        // We get an entry point from the Python side
        // Java calls Python without ever having been called from Python
	
		//testLogToCaseBase();
		//testLoadCaseBase();
		//testTrainAgent();		
		//testRunAgentFromFile();
		testGymDoor();
		

		//gatewayServer.shutdown();
		
	}//main
}//class


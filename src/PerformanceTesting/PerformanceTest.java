package PerformanceTesting;

import java.io.IOException;
import java.io.*;

import org.jLOAF.Agent;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.performance.PerformanceEvaluator;
import org.jLOAF.preprocessing.filter.CaseBaseFilter;
import org.jLOAF.preprocessing.filter.featureSelection.HillClimbingFeatureSelection;

import AgentModules.OpenAIAgent;
import CaseBaseCreation.LogFile2CaseBase;



/***
 * class PerformanceTest will create an agent with one caseBase and use a testBase to measure its performance. It will output all the performance measures such as
 * accuracy, recall, precision, f-measure.
 * @author Chad Peters
 * @since 2017 September
 ***/
public class PerformanceTest extends PerformanceEvaluator {


	public static void main(String[] args)
	{
		System.out.println("Starting Performance Evaluation");
	
		String [] filenames = 
			{"subsample.log",
			"subsample2.log"};
		
		//String [] outputFilenames = createArrayOfCasebaseNames(filenames);
		String output_filename = "Results/SomeGymStuff,TB,standardize,none,none,none,.csv";
		

		PerformanceTest pt = new PerformanceTest();
		CaseBaseFilter ft = new HillClimbingFeatureSelection(null);

		//set the performance evaluator. Not passing in reasoner, or similaritymetricstrategy (yet)
		try 
		{
			pt.PerformanceEvaluatorMethod(filenames,null,"gymOutput.txt","TB",null,null);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}


	@Override
	public OpenAIAgent createAgent() 
	{
		OpenAIAgent agent = new OpenAIAgent();

		return agent;
	}

	@Override
	public String[] createArrayOfCasebaseNames(String[] filenames) throws IOException 
	{
		LogFile2CaseBase lfcb = new LogFile2CaseBase();
		String[] outputs = new String[filenames.length];

		String outputFile ="subsample";

		int i=0;
		
		for(String s:filenames)
		{
			outputs[i]=lfcb.parseLogFile(s,outputFile+i+".cb");
			i++;
		}

		return outputs;
	}



}

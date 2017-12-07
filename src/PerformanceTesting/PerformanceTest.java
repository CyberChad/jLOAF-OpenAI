package PerformanceTesting;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.jLOAF.Agent;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.performance.PerformanceEvaluator;
import org.jLOAF.preprocessing.filter.CaseBaseFilter;
import org.jLOAF.preprocessing.filter.casebasefilter.Sampling;
import org.jLOAF.preprocessing.filter.casebasefilter.UnderSampling;
import org.jLOAF.preprocessing.filter.featureSelection.HillClimbingFeatureSelection;
import org.jLOAF.preprocessing.filter.featureSelection.WeightsSeperatorFilter;
import org.jLOAF.preprocessing.standardization.Standardization;

import AgentModules.OpenAIAgent;
import CaseBaseCreation.LogFile2CaseBase;



/***
 * class PerformanceTest will create an agent with one caseBase and use a testBase to measure its performance. It will output all the performance measures such as
 * accuracy, recall, precision, f-measure.
 * @author Chad Peters
 * @since 2017 September
 ***/
public class PerformanceTest extends PerformanceEvaluator 
{
	private static String[] filenames;
	private static String [] reasoners;
	private static File logfiles; 
	
	  private static void readConfig()
	  {
	    try 
	    {
			File fXmlFile = new File("TestConfig.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
			if (doc.hasChildNodes()) 
			{
				printNode(doc.getChildNodes());
			}//if
	    }//try 
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    }
	  }
	
	  private static void printNode(NodeList nodeList) 
	  {

	    for (int count = 0; count < nodeList.getLength(); count++) 
	    {
			Node tempNode = nodeList.item(count);

			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				// get node name and value
				System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
				System.out.println("Node Value =" + tempNode.getTextContent());

				if (tempNode.hasAttributes()) 
				{
					// get attributes names and values
					NamedNodeMap nodeMap = tempNode.getAttributes();

					for (int i = 0; i < nodeMap.getLength(); i++) 
					{
						Node node = nodeMap.item(i);
						System.out.println("attr name : " + node.getNodeName());
						System.out.println("attr value : " + node.getNodeValue());
					}
				}

				if (tempNode.hasChildNodes()) 
				{
					// loop again if has child nodes
					printNode(tempNode.getChildNodes());
				}

				System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");

			}//if

	    }//for

	  }//printNode
  
	  
	public static void main(String[] args)
	{
		//readConfig();
		
		System.out.println("Starting Performance Evaluation");
		
		//CaseBaseFilter WSF = new WeightsSeperatorFilter(null);
		//CaseBaseFilter standardize = new Standardization(WSF);
		//CaseBaseFilter smote = new UnderSampling(standardize);
		//CaseBaseFilter sample = new Sampling(standardize);
		

		//PerformanceTest pt = new PerformanceTest();
		//pt.PerformanceEvaluatorMethod(filenames, standardize, output_filename,"dbn",null, null);

		 
		File logfiles = new File("logfiles-lunar.cfg");
		File reasonfiles = new File("reasoners.cfg");
				
		//load the filenames of log files
		
		
		try
		{
			String line = null;			
			Scanner sc = new Scanner(logfiles);
			
			//get first line
			if (sc.hasNextLine())
			{
				line = sc.nextLine(); 
			}
			line.replace(" ", ""); //strip whitespace
			
			int ix = 0;
			int numlines = Integer.valueOf(line);
			
			System.out.println("numlines: "+numlines);
			
			filenames = new String[numlines];
			
			while(sc.hasNextLine())
			{
				line = sc.nextLine();				
				line.replace(" ", ""); //strip whitespace
				
				filenames[ix] = line;
				System.out.println("file :"+ix+" name: "+filenames[ix]);
				ix++;

			}//while
			
			sc.close();
			
		}//try
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			String line = null;			
			Scanner sc = new Scanner(reasonfiles);
			
			//get first line
			if (sc.hasNextLine())
			{
				line = sc.nextLine(); 
			}
			line.replace(" ", ""); //strip whitespace
			
			int ix = 0;
			int numlines = Integer.valueOf(line);
			
			System.out.println("numlines: "+numlines);
			
			reasoners = new String[numlines];
			
			while(sc.hasNextLine())
			{
				line = sc.nextLine();				
				line.replace(" ", ""); //strip whitespace
				
				reasoners[ix] = line;
				System.out.println("reasoner :"+ix+" name: "+reasoners[ix]);
				ix++;

			}//while
			
			sc.close();
			
		}//try
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*		
	    String [] filenames = 
	    		 {"mountain01k.log"	    	 
	    	      ,"mountain02k.log"
	    	      ,"mountain03k.log"
	    	      };

		
		String [] reasoners =
			{"ONEKNN"
			,"KNN"
			,"weightedKNN"
			};
		
		*/
		//String [] outputFilenames = createArrayOfCasebaseNames(filenames);
		//String output_filename = "Results/SomeGymStuff,TB,standardize,none,none,none,.csv";
		
		CaseBaseFilter oversample = new Sampling(null);
		CaseBaseFilter undersample = new UnderSampling(null);
		CaseBaseFilter bothsample = new Sampling(undersample);

		PerformanceTest pt = new PerformanceTest();
		
		//CaseBaseFilter ft = new HillClimbingFeatureSelection(null);

		//set the performance evaluator. Not passing in reasoner, or similaritymetricstrategy (yet)

		int num_reasons = reasoners.length;
		try 
		{
			for(int i=0; i < num_reasons; i++)
			{
				System.out.println("Starting Evaluation "+i+": "+reasoners[i]);
				pt.PerformanceEvaluatorMethod(filenames,bothsample,"gymOutput.txt",reasoners[i],null,null);
			}//for
		}//try 
		catch (IOException e) 
		{
			e.printStackTrace();
		}//catch
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

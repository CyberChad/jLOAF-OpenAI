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
		try {
			Scanner sc = new Scanner(file);
			int index=0;
			double[] entry=new double[9];

			while(sc.hasNextLine())
			{

				try {

				entry[index%9] = sc.nextDouble();

				if(index%9==8){

					this.createCase(cb,entry);
				}

				index++;


				}catch (NoSuchElementException e){
					break;
				}

			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			this.saveCaseBase(cb, file2);
			System.out.println("done with creating one caseBase");
		return file2;
	}


	/*
	 * creates a case from the double values passed to it and then adds it to the casebase
	 * @param cb2 the casebase of the observed expert
	 * @param entry an array of double values represent the parameters of the actions and the inputs
	 */
	public void createCase(CaseBase cb2, double[] entry)
	{
		
		OpenAIAction act= new OpenAIAction(Actions.values()[(int)entry[8]-1].getAction());
		OpenAIInput vci = new OpenAIInput(OpenAIInput.NAME,complexStrategy);
		for(int i=0;i<entry.length-1;i=i+4){
			ComplexInput ci =Inputs.values()[i/4].setFeat(complexStrategy);
			 AtomicInput a1 = new AtomicInput("up",new Feature(entry[i]-1),atomicStrategy);
			 AtomicInput a2 = new AtomicInput("down",new Feature(entry[i+1]-1),atomicStrategy);
			 AtomicInput a3 = new AtomicInput("left",new Feature(entry[i+2]-1),atomicStrategy);
			 AtomicInput a4 = new AtomicInput("right",new Feature(entry[i+3]-1),atomicStrategy);
			 ci.add(a1);
			 ci.add(a2);
			 ci.add(a3);
			 ci.add(a4);
			 vci.add(ci);
			 
			//System.out.println(vci.getChildNames().size());			
		}
		Case c =new Case(vci,act);

		cb2.createThenAdd(vci,act,stateBasedStrategy);





	}


}

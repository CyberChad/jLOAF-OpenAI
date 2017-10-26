package CaseBaseCreation;

import org.jLOAF.inputs.AtomicInput;
import org.jLOAF.inputs.ComplexInput;
import org.jLOAF.inputs.Feature;
import org.jLOAF.sim.SimilarityMetricStrategy;
import org.jLOAF.sim.ComplexSimilarityMetricStrategy;

	public enum Inputs
	{
		Wall("Wall"),Dirt("Dirt");
		private String name;
		
		 Inputs(String cp)
		 {
			name=cp;			
		}
		 public String getName(){
			 return name;
		 }
		 
		
		 public ComplexInput setFeat(ComplexSimilarityMetricStrategy sim){
			 ComplexInput ci= new ComplexInput(name,sim);
			 
			
			 return ci;
		 }
	
	
	}


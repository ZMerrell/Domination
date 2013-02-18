/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.domination.engine.ai.planrecognition.planlibraryobjects.components.explanation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.yura.domination.engine.ai.planrecognition.planlibraryobjects.components.action.ActionArmyMovement;
import net.yura.domination.engine.ai.planrecognition.planlibraryobjects.components.action.ActionCountryReinforced;
import net.yura.domination.engine.ai.planrecognition.planlibraryobjects.components.action.ActionFailedDefence;
import net.yura.domination.engine.ai.planrecognition.planlibraryobjects.components.action.ActionSuccessfulDefence;
import net.yura.domination.engine.ai.planrecognition.planlibraryobjects.components.action.ActionSuccessfulOccupation;
import net.yura.domination.engine.ai.planrecognition.planlibraryobjects.components.action.BasicAction;
import net.yura.domination.engine.ai.planrecognition.planlibraryobjects.components.observation.BasicObservation;
import net.yura.domination.engine.ai.planrecognition.planlibraryobjects.components.rootgoalmanagement.RootGoal;
import net.yura.domination.engine.ai.planrecognition.planlibraryobjects.components.subgoalmanagement.SubGoal;
import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;

/**
 *
 * @author s0914007
 */
public class OccupyAsiaAfrica extends Explanation implements Serializable {

    public OccupyAsiaAfrica() {
        
        this.setMissionName("Occupy Asia and Africa");
        
        this.getRootGoalSet().add(new RootGoal("Occupy", "Asia and South America", 1.0f/20.0f));  
        
        this.getSubGoalSet().add(new SubGoal("Occupy", "Asia", 1.0f));
        this.getSubGoalSet().add(new SubGoal("Occupy", "Africa", 1.0f));
        
        this.calcInitialExpProb();
    }
    
    public boolean isConsistent(BasicAction action) {
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addConsistentActions(Continent continent) {
        
        for(Object country : continent.getTerritoriesContained()){
                
            Country currentCountry = (Country) country;
                    
            this.getConActions().add(new ActionSuccessfulOccupation(currentCountry.getName(), 1.0f));
            this.getConActions().add(new ActionCountryReinforced(currentCountry.getName(), 1.0f));
            this.getConActions().add(new ActionArmyMovement(currentCountry.getName(), 1.0f));
            this.getConActions().add(new ActionSuccessfulDefence(currentCountry.getName(), 1.0f));

            this.getInConActions().add(new ActionFailedDefence(currentCountry.getName(), 1.0f));
        }
    }
    
    public void computeMissionProbability(String observationType, Set<BasicAction> activeSet, BasicObservation currentObservation){
        
        List<BasicAction> conActiveSet = new ArrayList<BasicAction>();
        List<BasicAction> inConActiveSet = new ArrayList<BasicAction>();
        
        int conActionCounter = 0;
        int inConActionCounter = 0;
        
        float missionProbability = this.getExplanationProbability();
        
        if(!observationType.equals("Failed Defence") && !observationType.equals("Successful Defence")){
            
            // Filters out actions that are not in the same action type
            for(BasicAction ba : this.getConActions()){
                
                if(ba.getActionType().equals(observationType)){

                    conActiveSet.add(ba);
                }
            }

            /*System.out.println("Agents Active Pending Set");
            for(BasicAction a : activeSet){

                System.out.println(a.getActionType() + " " + a.getCountryName());
            }
            System.out.println(" ");
            System.out.println("Explanation Consistent Actions");
            for(BasicAction b : conActiveSet){

                System.out.println(b.getActionType() + " " + b.getCountryName());
            }
            System.out.println(" ");*/

            // Count number of inconsistent and consistent actions in active pending set when action took place
            for(BasicAction b : activeSet){

                if(conActiveSet.contains(b)){

                    conActionCounter++;

                } 
            }

            //System.out.println(conActionCounter);
            //System.out.println(" ");

            // Use a different maths function to calculate weighting amounts!
            float conActionProb = 0.6f / conActionCounter;
            // If it is not consistent it is inconsistent
            float inconActionProb = 0.4f / (activeSet.size() - conActionCounter);

            //System.out.println(currentObservation.getActionType() + " " + currentObservation.getCountryName());

            if(conActiveSet.contains(currentObservation)){

                System.out.println(this.getMissionName() + " consistent action!" + " " + missionProbability + " * " + conActionProb);
                //System.out.println(" ");
                this.setExplanationProbability(missionProbability *= conActionProb);

            } else { 

                System.out.println(this.getMissionName() + " inconsistent action!" + " " + missionProbability + " * " + inconActionProb);
                //System.out.println(" ");
                this.setExplanationProbability(missionProbability *= inconActionProb);  
            }
        } else {
            
            // Filters out actions that are not in the same action type
            for(BasicAction ba : this.getInConActions()){

                if(ba.getActionType().equals(observationType)){

                    inConActiveSet.add(ba);
                }
            }
            
            for(BasicAction ba : this.getConActions()){
                
                if(ba.getActionType().equals(observationType)){
                    
                    conActiveSet.add(ba);
                }
            }
            
            // Count number of inconsistent and consistent actions in active pending set when action took place
            for(BasicAction b : activeSet){

                //System.out.println(b.getActionType() + " " + b.getCountryName());
                if(inConActiveSet.contains(b)){

                    inConActionCounter++;
                } 
            }
            
            for(BasicAction b : activeSet){
                
                if(conActiveSet.contains(b)){
                    
                    conActionCounter++;
                }
            }
            
             // Use a different maths function to calculate weighting amounts!
            float conActionProb = 1.02f;
            // If it is not consistent it is inconsistent
            float inconActionProb = 0.98f;
            
            //System.out.println(currentObservation.getActionType() + " " + currentObservation.getContinentName() + " " + currentObservation.getCountryName());
            if(inConActiveSet.contains(currentObservation)){

                System.out.println(this.getMissionName() + " inconsistent action!" + " " + missionProbability + " * " + inconActionProb);
                //System.out.println(" ");
                this.setExplanationProbability(missionProbability *= inconActionProb);    

            } else if(conActiveSet.contains(currentObservation)){
                
                System.out.println(this.getMissionName() + " consistent action!" + " " + missionProbability + " * " + conActionProb );
                this.setExplanationProbability(missionProbability *= conActionProb);
                
            } else { 

                System.out.println(this.getMissionName() + " action does not apply!");
                //System.out.println(" ");   
            }
        }
    }
}
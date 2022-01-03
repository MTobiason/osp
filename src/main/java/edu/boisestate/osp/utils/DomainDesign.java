/*
 * The MIT License
 *
 * Copyright 2021 mtobi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.boisestate.osp.utils;

import edu.boisestate.osp.design.Design;
import edu.boisestate.osp.design.DesignProperty;
import edu.boisestate.osp.design.HasVariableSequences;
import edu.boisestate.osp.seqevo.ISeqEvoDesign;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import edu.boisestate.osp.sequence.LinearSequence;
import edu.boisestate.osp.sequence.NamedSetOfMixed;

/**
 *
 * @author mtobi
 */

public class DomainDesign implements Design, ISeqEvoDesign{
    final Map<String,LinearSequence> fixedDomainSequences;
    final Map<String,LinearSequence> variableDomainSequences;
    final Map<String,String[]> linearStrandDomains;
    final Map<String,String[]> circularStrandDomains;
    final Map<String,LinearSequence> linearStrandSequences;
    final Map<String,LinearSequence> circularStrandSequences;
    
    final NamedSetOfMixed strandSequences;
    
    DomainDesign(Map<String,LinearSequence> fixedDomainSequences, Map<String,LinearSequence> variableDomainSequences, Map<String,String[]> linearStrandDomains, Map<String,String[]> circularStrandDomains){
        //initialize data structures
        this.fixedDomainSequences = new TreeMap<>();
        this.linearStrandDomains =new TreeMap<>();
        this.linearStrandSequences = new TreeMap<>();
        this.circularStrandDomains =new TreeMap<>();
        this.circularStrandSequences = new TreeMap<>();
        this.variableDomainSequences = new TreeMap<>();
            
        //fill data structures
        try{
            fixedDomainSequences.forEach((key,value) -> {
                if(key.contains(".") || key.contains(",")) throw new RuntimeException("Domain "+key+" cannot contain '.' or ','");
                if(fixedDomainSequences.containsKey(key)) throw new RuntimeException("Domain "+key+" is present in fixed domains multiple times.");
                this.fixedDomainSequences.put(key,value);
            });
            
            variableDomainSequences.forEach((key,value) -> {
                if(key.contains(".") || key.contains(",")) throw new RuntimeException("Domain "+key+" cannot contain '.' or ','");
                if(fixedDomainSequences.containsKey(key)) throw new RuntimeException("Domain "+key+" is present in both fixed domains and variable domains.");
                if (this.variableDomainSequences.containsKey(key)) throw new RuntimeException("Domain "+key+" is present in variable domains multiple times.");
                this.variableDomainSequences.put(key,value);
            });
            
            linearStrandDomains.forEach((key,value) ->{
                this.linearStrandDomains.put(key,value.clone());
                LinearSequence[] subSequences  = new LinearSequence[value.length];
                for(int i =0; i < value.length; i++){
                    String domainName = value[i];
                    boolean isComplement = false;
                    if(domainName.startsWith("c.")||domainName.startsWith("C.")) {isComplement = true; domainName = domainName.substring(2);};
                    LinearSequence currentDomain = fixedDomainSequences.get(domainName);
                    if(currentDomain == null) currentDomain = variableDomainSequences.get(domainName);
                    if(currentDomain == null) throw new RuntimeException("Could not find domain '"+domainName+"'.");
                    if(isComplement) currentDomain = currentDomain.getComplement();
                    subSequences[i] = currentDomain;
                }
                linearStrandSequences.put(key,GenericBaseSequence.newFromBaseSequences(subSequences));
            });
            
            circularStrandDomains.forEach((key,value) ->{
                this.circularStrandDomains.put(key,value.clone());
                LinearSequence[] subSequences  = new LinearSequence[value.length];
                for(int i =0; i < value.length; i++){
                    String domainName = value[i];
                    boolean isComplement = false;
                    if(domainName.startsWith("c.")||domainName.startsWith("C.")) {isComplement = true; domainName = domainName.substring(2);};
                    LinearSequence currentDomain = fixedDomainSequences.get(domainName);
                    if(currentDomain == null) currentDomain = variableDomainSequences.get(domainName);
                    if(currentDomain == null) throw new RuntimeException("Could not find domain '"+domainName+"'.");
                    if(isComplement) currentDomain = currentDomain.getComplement();
                    subSequences[i] = currentDomain;
                }
                circularStrandSequences.put(key,GenericBaseSequence.newFromBaseSequences(subSequences));
            });
        }
        catch (Exception e){
            System.out.println("Error while initiating design.");
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    
    DomainDesign(DomainDesign design){
        this.fixedDomainSequences = new TreeMap<>(design.fixedDomainSequences);
        this.linearStrandDomains =new TreeMap<>(design.linearStrandDomains);
        this.linearStrandSequences = new TreeMap<>(design.linearStrandSequences);
        this.circularStrandDomains =new TreeMap<>(design.circularStrandDomains);
        this.circularStrandSequences = new TreeMap<>(design.circularStrandSequences);
        this.variableDomainSequences = new TreeMap<>(design.variableDomainSequences);
    }
    
    static public Design newFromParameters(Map<String,LinearSequence> fixedDomainSequences, Map<String,LinearSequence> variableDomainSequences, Map<String,String[]> linearStrandDomains, Map<String,String[]> circularStrandDomains){
        return new DomainDesign(fixedDomainSequences, variableDomainSequences, linearStrandDomains, circularStrandDomains);
    }

    public NamedSetOfMixed getStrandSequences(){
        
    }
    
    public Map<String, LinearSequence> getCircularStrandSequences() {
        return new TreeMap<String,LinearSequence>(circularStrandSequences);
    }

    @Override
    public Map<String, LinearSequence> getLinearStrandSequences() {
        return new TreeMap<String,LinearSequence>(linearStrandSequences);
    }

    @Override
    public Map<String, LinearSequence> getVariableSequences() {
        return new TreeMap<String,LinearSequence>(variableDomainSequences);
    }

    @Override
    public UpdatedDesign newFromVariableSequences(Map<String, LinearSequence> variableSequences) {
        return UpdatedDomainDesign.getNew(this,variableSequences);
    }

    @Override
    public String getPropertyValue(DesignProperty property) {
        return getPropertyReport(property).getValue();
    }
    
    public DesignPropertyReport getPropertyReport(DesignProperty property){
        DesignPropertyReport report = propertyReports.get(property);
        if(report ==null) {
            report = property.calculateReport(this);
            propertyReports.put(property,report);
        }
        return report;
    }
}

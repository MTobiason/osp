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
package edu.boisestate.osp.util;

import edu.boisestate.osp.design.DesignProperty;
import edu.boisestate.osp.design.DesignPropertyReport;
import edu.boisestate.osp.design.UpdatedDesign;
import java.util.Map;
import java.util.TreeMap;
import edu.boisestate.osp.sequence.LinearSequence;
import edu.boisestate.osp.sequence.LinearSequence;

/**
 *
 * @author mtobi
 */
public class UpdatedDomainDesign extends SeqEvoDomainDesign implements UpdatedDesign{
        
    final Map<String,LinearSequence> previous_fixedDomainSequences;
    final Map<String,LinearSequence> previous_variableDomainSequences;
    final Map<String,String[]> previous_linearStrandDomains;
    final Map<String,String[]> previous_circularStrandDomains;
    final Map<String,LinearSequence> previous_linearStrandSequences;
    final Map<String,LinearSequence> previous_circularStrandSequences;
    Map<DesignProperty,DesignPropertyReport> previous_propertyReports;
    
    UpdatedDomainDesign(SeqEvoDomainDesign design, Map<String,LinearSequence> updatedVariableDomains){
        super(design);
        
        previous_fixedDomainSequences = design.fixedDomainSequences;
        previous_linearStrandDomains = design.linearStrandDomains;
        previous_linearStrandSequences = design.linearStrandSequences;
        previous_circularStrandDomains = design.circularStrandDomains;
        previous_circularStrandSequences = design.circularStrandSequences;
        previous_variableDomainSequences = design.variableDomainSequences;
        previous_propertyReports = design.propertyReports;
    }
    
    @Override
    public Map<String, LinearSequence> getPreviousCircularStrandSequences() {
        return new TreeMap<String,LinearSequence>(previous_circularStrandSequences);
    }

    @Override
    public Map<String, LinearSequence> getPreviousLinearStrandSequences() {
        return new TreeMap<String,LinearSequence>(previous_linearStrandSequences);
    }

    @Override
    public Map<String, LinearSequence> getPreviousVariableSequences() {
        return new TreeMap<String,LinearSequence>(previous_variableDomainSequences);
    }

    @Override
    public String getPreviousPropertyValue(DesignProperty property) {
        return getPreviousPropertyReport(property).getValue();
    }
    
    public DesignPropertyReport getPreviousPropertyReport(DesignProperty property){
        DesignPropertyReport report = propertyReports.get(property);
        if(report ==null) {
            report = property.calculateReport(this);
            propertyReports.put(property,report);
        }
        return report;
    }
    
    public static UpdatedDomainDesign getNew(SeqEvoDomainDesign design, Map<String,LinearSequence> newVariableDomains){
        return new UpdatedDomainDesign(design,newVariableDomains);
    }
}

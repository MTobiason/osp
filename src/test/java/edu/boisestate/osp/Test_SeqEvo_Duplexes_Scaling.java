/*
 * The MIT License
 *
 * Copyright 2023 mtobi.
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

/**
 *
 * @author mtobi
 */

package edu.boisestate.osp;

import edu.boisestate.osp.SeqEvo.Report;
import java.util.Map;
import java.util.TreeMap;

public class Test_SeqEvo_Duplexes_Scaling {
    
    public static void main(String[] args){
        int[] duplexNumbers = {1,2,4,8,16,32,64,128,256,512,1024};
        int[] duplexSizes = {8/4,16/4,32/4,64/4,128/4,256/4,512/4,1024/4};
        
        double[] numberTimes = new double[duplexNumbers.length];
        for (int i =0; i < duplexNumbers.length; i++){
            System.out.println("Starting "+duplexNumbers[i]+" duplexes");
            numberTimes[i] = measureTime(duplexNumbers[i], 8/4);
            System.out.println("Time: "+numberTimes[i]);
        }
        
        double[] sizeTimes = new double[duplexSizes.length];
        for (int i =0; i < duplexSizes.length; i++){
            System.out.println("Starting "+duplexSizes[i]*4+" base-pairs");
            sizeTimes[i] = measureTime(1, duplexSizes[i]);
            System.out.println("Time: "+sizeTimes[i]);
        }
        
        System.out.println("Number duplexes,total time");
        for (int i =0; i < duplexNumbers.length; i++){
            System.out.println(duplexNumbers[i]+"\t"+numberTimes[i]);
        }
        
        System.out.println("Duplex sizes,total time");
        for (int i =0; i < duplexSizes.length; i++){
            System.out.println(duplexSizes[i]*4+"\t"+sizeTimes[i]);
        }
        
        System.exit(0);
    }
    
        
    private static double measureTime(int numberDuplexes, int oneFourthBasesPerDuplex){
        Map<String,String> parameters = new TreeMap<>();{
            //parameters.put("CPL", "1000");
        }
        
        Map<String,String> fixedDomains = new TreeMap<>();
        Map<String,String> variableDomains = new TreeMap<>();
        Map<String,String[]> oligomerDomains = new TreeMap<>();
        
        addVariableDomains(variableDomains,numberDuplexes,oneFourthBasesPerDuplex);
        addDuplexOligomerDomains(oligomerDomains,numberDuplexes);
        
        
        SeqEvo s = new SeqEvo();
        SeqEvo.Request req = new SeqEvo.Request(parameters, fixedDomains, variableDomains, oligomerDomains,System.out);
        Report rep = s.run(req);
        return rep.totalTimeSeconds;
    }
    
    private static void addDuplexOligomerDomains(Map<String,String[]> oligomerDomains, int numberDuplexes){
        for(int i =1; i <= numberDuplexes; i++){
            oligomerDomains.put("Duplex-"+i+"-Top", new String[] {"Domain-"+i});
            oligomerDomains.put("Duplex-"+i+"-Bottom", new String[] {"c.Domain-"+i});
        }
    }
    
    private static void addVariableDomains(Map<String,String> variableDomains, int numberDuplexes, int numberOfEachBase){
        StringBuilder domainSequence = new StringBuilder();
        for(int i =0; i < numberOfEachBase; i++){
            domainSequence.append('A');
            domainSequence.append('C');
            domainSequence.append('G');
            domainSequence.append('T');
        }
        
        for(int i =1; i <= numberDuplexes; i++){
            variableDomains.put("Domain-"+i,domainSequence.toString());
        }
    }
    
}

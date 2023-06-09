import edu.boisestate.osp.SeqEvo;
import edu.boisestate.osp.SeqEvo.Report;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

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
public class SeqEvoTest_Duplex_Scaling {
    
    public static void main(String[] args){
        Map<String,String> parameters = new TreeMap<>();{
        }
        
        Map<String,String> fixedDomains = new TreeMap<>();
        Map<String,String> variableDomains = new TreeMap<>();
        Map<String,String[]> oligomerDomains = new TreeMap<>();
        
        addVariableDomains(variableDomains,1,256/4);
        addDuplexOligomerDomains(oligomerDomains,1);
        
        SeqEvo s = new SeqEvo(parameters);
        Report r = s.run(fixedDomains, variableDomains, oligomerDomains);
        System.exit(0);
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

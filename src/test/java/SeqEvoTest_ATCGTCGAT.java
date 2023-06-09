
import edu.boisestate.osp.SeqEvo;
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
public class SeqEvoTest_ATCGTCGAT {
    
    public static void main(String[] args){
        Map<String,String> parameters = new TreeMap<>();{
            parameters.put("maxAA", "6");
            parameters.put("maxCC", "3");
            parameters.put("maxGG", "3");
            parameters.put("maxTT", "6");
            parameters.put("CPL", "1");
            parameters.put("NL", "8");
        }
        Map<String,String> fixedDomains = new TreeMap<>();{
            fixedDomains.put("A", "ATCG");
            fixedDomains.put("B", "T");
        }
        Map<String,String> variableDomains = new TreeMap<>();{
            variableDomains.put("C", "CGAT");
        }
        Map<String,String[]> oligomerDomains = new TreeMap<>();{
            oligomerDomains.put("Top-Strand", new String[] {"A", "B", "C"});
            oligomerDomains.put("Bottom-Strand", new String[] {"c.C", "c.B", "c.A"});
        }
        
        SeqEvo s = new SeqEvo(parameters);
        SeqEvo.Report r = s.run(fixedDomains, variableDomains, oligomerDomains);
        System.exit(0);
    }
    
}

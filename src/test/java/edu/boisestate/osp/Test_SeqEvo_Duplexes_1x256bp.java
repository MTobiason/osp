/*
 * Copyright (c) 2019 Boise State University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.boisestate.osp;

import java.util.Map;
import java.util.TreeMap;

public class Test_SeqEvo_Duplexes_1x256bp {
    
    public static void main(String[] args){
        Map<String,String> parameters = new TreeMap<>();{
            parameters.put("CPL", "1000");
        }
        
        Map<String,String> fixedDomains = new TreeMap<>();{
        }
        
        Map<String,String> variableDomains = new TreeMap<>();{
            variableDomains.put("Domain-1", "AAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTTAAAACCCCGGGGTTTT");
        }
        Map<String,String[]> oligomerDomains = new TreeMap<>();{
            oligomerDomains.put("Duplex-1-Top", new String[] {"Domain-1"});
            oligomerDomains.put("Duplex-1-Bottom", new String[] {"c.Domain-1"});
        }
        
        SeqEvo s = new SeqEvo();
        SeqEvo.Request req = new SeqEvo.Request(parameters, fixedDomains, variableDomains, oligomerDomains,System.out);
        SeqEvo.Report rep = s.run(req);
        System.exit(0);
    }
    
}

package edu.boisestate.osp;

import edu.boisestate.osp.SeqEvo;
import edu.boisestate.osp.SeqEvo.Report;
import edu.boisestate.osp.SeqEvo.Request;
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
public class SeqEvoTest_16x8 {
    
    public static void main(String[] args){
        Map<String,String> parameters = new TreeMap<>();{
        }
        Map<String,String> fixedDomains = new TreeMap<>();{
        }
        Map<String,String> variableDomains = new TreeMap<>();{
            variableDomains.put("Domain-1", "AATTCCGG");
            variableDomains.put("Domain-2", "AATTCCGG");
            variableDomains.put("Domain-3", "AATTCCGG");
            variableDomains.put("Domain-4", "AATTCCGG");
            variableDomains.put("Domain-5", "AATTCCGG");
            variableDomains.put("Domain-6", "AATTCCGG");
            variableDomains.put("Domain-7", "AATTCCGG");
            variableDomains.put("Domain-8", "AATTCCGG");
            variableDomains.put("Domain-9", "AATTCCGG");
            variableDomains.put("Domain-10", "AATTCCGG");
            variableDomains.put("Domain-11", "AATTCCGG");
            variableDomains.put("Domain-12", "AATTCCGG");
            variableDomains.put("Domain-13", "AATTCCGG");
            variableDomains.put("Domain-14", "AATTCCGG");
            variableDomains.put("Domain-15", "AATTCCGG");
            variableDomains.put("Domain-16", "AATTCCGG");
        }
        Map<String,String[]> oligomerDomains = new TreeMap<>();{
            oligomerDomains.put("Duplex-1-Top", new String[] {"Domain-1"});
            oligomerDomains.put("Duplex-1-Bottom", new String[] {"c.Domain-1"});
            oligomerDomains.put("Duplex-2-Top", new String[] {"Domain-2"});
            oligomerDomains.put("Duplex-2-Bottom", new String[] {"c.Domain-2"});
            oligomerDomains.put("Duplex-3-Top", new String[] {"Domain-3"});
            oligomerDomains.put("Duplex-3-Bottom", new String[] {"c.Domain-3"});
            oligomerDomains.put("Duplex-4-Top", new String[] {"Domain-4"});
            oligomerDomains.put("Duplex-4-Bottom", new String[] {"c.Domain-4"});
            oligomerDomains.put("Duplex-5-Top", new String[] {"Domain-5"});
            oligomerDomains.put("Duplex-5-Bottom", new String[] {"c.Domain-5"});
            oligomerDomains.put("Duplex-6-Top", new String[] {"Domain-6"});
            oligomerDomains.put("Duplex-6-Bottom", new String[] {"c.Domain-6"});
            oligomerDomains.put("Duplex-7-Top", new String[] {"Domain-7"});
            oligomerDomains.put("Duplex-7-Bottom", new String[] {"c.Domain-7"});
            oligomerDomains.put("Duplex-8-Top", new String[] {"Domain-8"});
            oligomerDomains.put("Duplex-8-Bottom", new String[] {"c.Domain-8"});
            oligomerDomains.put("Duplex-9-Top", new String[] {"Domain-9"});
            oligomerDomains.put("Duplex-9-Bottom", new String[] {"c.Domain-9"});
            oligomerDomains.put("Duplex-10-Top", new String[] {"Domain-10"});
            oligomerDomains.put("Duplex-10-Bottom", new String[] {"c.Domain-10"});
            oligomerDomains.put("Duplex-11-Top", new String[] {"Domain-11"});
            oligomerDomains.put("Duplex-11-Bottom", new String[] {"c.Domain-11"});
            oligomerDomains.put("Duplex-12-Top", new String[] {"Domain-12"});
            oligomerDomains.put("Duplex-12-Bottom", new String[] {"c.Domain-12"});
            oligomerDomains.put("Duplex-13-Top", new String[] {"Domain-13"});
            oligomerDomains.put("Duplex-13-Bottom", new String[] {"c.Domain-13"});
            oligomerDomains.put("Duplex-14-Top", new String[] {"Domain-14"});
            oligomerDomains.put("Duplex-14-Bottom", new String[] {"c.Domain-14"});
            oligomerDomains.put("Duplex-15-Top", new String[] {"Domain-15"});
            oligomerDomains.put("Duplex-15-Bottom", new String[] {"c.Domain-15"});
            oligomerDomains.put("Duplex-16-Top", new String[] {"Domain-16"});
            oligomerDomains.put("Duplex-16-Bottom", new String[] {"c.Domain-16"});
        }
        
        SeqEvo s = new SeqEvo(parameters);
        Request req = new Request(fixedDomains, variableDomains, oligomerDomains,System.out);
        Report rep = s.run(req);
        System.exit(0);
    }
    
}

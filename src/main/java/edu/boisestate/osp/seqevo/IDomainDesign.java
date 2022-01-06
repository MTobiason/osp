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
package edu.boisestate.osp.seqevo;

import edu.boisestate.osp.sequence.CircularSequence;
import edu.boisestate.osp.sequence.LinearSequence;
import edu.boisestate.osp.structure.SimpleSecondaryStructure;
import java.util.Map;


/**
 *
 * @author mtobi
 */
public interface IDomainDesign{
    Map<String,CircularSequence> getCircularStrandSequences();
    Map<String,LinearSequence> getLinearStrandSequences();
    Map<String,LinearSequence> getVariableDomains();
    /**
     * 
     * @return returns an array containing the largest independent simple 
     * secondary structures implied by the design. This includes both  
     * structures resulting from the domain level design and structures 
     * implied by any fixed sequences.
     */
    SimpleSecondaryStructure[] getImpliedStructures();
    
    /**
     * 
     * @return returns an array containing the largest independent simple 
     * secondary structures implied by the design. This includes only  
     * structures resulting from the domain level design and not structures 
     * implied by any fixed sequences.
     */
    SimpleSecondaryStructure[] getIntentionalStructures();
}

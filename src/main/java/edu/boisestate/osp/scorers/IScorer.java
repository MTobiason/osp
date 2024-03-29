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

package edu.boisestate.osp.scorers;

import edu.boisestate.osp.networks.IDomainBasedEncodedNetwork;
import edu.boisestate.osp.networks.IDomainBasedEncodedScoredNetwork;

/**
 *
 * @author mtobi
 */
public interface IScorer {
    
    /**
     * Returns 1 if network1 is more fit than network2.
     * Returns 0 if network1 and network2 are equally fit.
     * Returns -1 if network1 is less fit than network 2.
     * @param network1
     * @param network2
     * @return
     */
    int compareFitness(IDomainBasedEncodedScoredNetwork network1, IDomainBasedEncodedScoredNetwork network2);
    
    /**
     * Returns a scored version of the given network;
     * @param network
     * @return
     */
    IDomainBasedEncodedScoredNetwork getScored(IDomainBasedEncodedNetwork network);

    /**
     * Returns a version of the given network scored using this scorer;
     * @param network
     * @return
     */
    IDomainBasedEncodedScoredNetwork getScored(IDomainBasedEncodedScoredNetwork network);

    /**
     * Returns a scored version of the given network.
     * @param previousNetwork The prior network
     * @param newNetwork The new network which has had one variable domain updated.
     * @param updatedDomainIndex The domain index of the variable domain which was updated.
     * @return
     */
    IDomainBasedEncodedScoredNetwork getScored(IDomainBasedEncodedScoredNetwork previousNetwork, IDomainBasedEncodedNetwork newNetwork, int updatedDomainIndex);
    
    /**
     * Returns a human-readable string for labeling this score. 
     * @return
     */
    String getScoreLabel();
    
    /**
     * Returns a human-readable string for describing the units of this score.
     * @return
     */
    String getScoreUnits();
}

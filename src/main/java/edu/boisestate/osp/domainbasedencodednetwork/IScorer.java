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
package edu.boisestate.osp.domainbasedencodednetwork;

/**
 *
 * @author mtobi
 */
public interface IScorer {
        
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
}

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


package edu.boisestate.osp.validators;

import edu.boisestate.osp.networks.IDomainBasedEncodedNetwork;

/**
 *
 * @author mtobi
 */
public interface IValidator{

    /**
     * Returns true if the network is valid and false otherwise.
     * @param network
     * @return
     */
    boolean isValidNetwork(IDomainBasedEncodedNetwork network);

    /**
     * For a previously valid network, returns true if the network is still 
     * valid after domain updatedDomain has been updated and false 
     * otherwise.
     * @param network
     * @param updatedDomainIndex
     * @return
     */
    boolean isValidNetwork (IDomainBasedEncodedNetwork network, int updatedDomainIndex);

    /**
     * Returns true the partial network is valid and false otherwise.A partial network may contain 0's in encoded sequences.
     * @param network
     * @return
     */
    boolean isValidPartialNetwork (IDomainBasedEncodedNetwork network);

    /**
     * For a previously valid partial network, returns true if the network 
     * is still valid after domain updatedDomain has been updated and false 
     * otherwise.A partial network may contain 0's in encoded sequences.
     * @param network
     * @param updatedDomainIndex
     * @return
     */
    boolean isValidPartialNetwork (IDomainBasedEncodedNetwork network, int updatedDomainIndex);
}

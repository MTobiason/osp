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

import java.util.Map;
import java.util.TreeMap;

public class Test_SeqEvo_Qian_Winfree_2011 {

    public static void main(String[] args) {
        Map<String, String> parameters = new TreeMap<>();
        {
            parameters.put("CPL","25000");
            parameters.put("NL","64");
            parameters.put("GPC","1");
            parameters.put("NMPC","2");
            parameters.put("NDPM","1");
            parameters.put("intraSLC","2");
            parameters.put("interSLC","2");
        }
        Map<String, String> fixedDomains = new TreeMap<>();
        {
            fixedDomains.put("c", "CA");
            fixedDomains.put("Sf", "TTTTTTTTTTT");
            fixedDomains.put("T", "TCT");
        }
        Map<String, String> variableDomains = new TreeMap<>();
        {
            variableDomains.put("S1","TCCATTCCACT");
            variableDomains.put("S4","CATAACAACCA");
            variableDomains.put("S7","ACATATCAATT");
            variableDomains.put("S10","TACAACATCTA");
            variableDomains.put("S13","CAACTCATTAC");
            variableDomains.put("S16","CTTCATAAATC");
            variableDomains.put("S19","CCTCTTAAACA");
            variableDomains.put("S22","TTCCTACATTT");
            variableDomains.put("S25","ATTCACTCAAT");
            variableDomains.put("S28","TCTACAATTCA");
            variableDomains.put("S31","ATCCACACTTC");
            variableDomains.put("S34","CATAACAAAAC");
            variableDomains.put("S37","CCTCTTCCCTT");
            variableDomains.put("S40","ATACAAATCCA");
            variableDomains.put("S43","TCATACCTACT");
            variableDomains.put("S46","AACCCAACTCA");
            variableDomains.put("S49","TCCTTAACTCC");
            variableDomains.put("S52","CTTCACAACTA");
            variableDomains.put("S2","AAACAAAACCT");
            variableDomains.put("S5","CCACCAAACTT");
            variableDomains.put("S8","CTAACATACAA");
            variableDomains.put("S11","ATATCCATAAC");
            variableDomains.put("S14","TTATTCAAACC");
            variableDomains.put("S17","ACTCCTAATAT");
            variableDomains.put("S20","ATCTAACACTC");
            variableDomains.put("S23","AATCTTCATCC");
            variableDomains.put("S26","TTCATTACCTC");
            variableDomains.put("S29","CCAATACTCCT");
            variableDomains.put("S32","CACTTCAAACT");
            variableDomains.put("S35","CTCTCCATCAC");
            variableDomains.put("S38","TACCCTTTTCT");
            variableDomains.put("S41","ACAAACCATTA");
            variableDomains.put("S44","AAACTCTCTCT");
            variableDomains.put("S47","TTCTCCCACCT");
            variableDomains.put("S50","TTACCAACCAC");
            variableDomains.put("S53","TATCTAATCTC");
            variableDomains.put("S3","CCCTAAAATCT");
            variableDomains.put("S6","TAACACAATCA");
            variableDomains.put("S9","CCATCAAATAA");
            variableDomains.put("S12","TCAATCAACAC");
            variableDomains.put("S15","CACTATAATTC");
            variableDomains.put("S18","TCTTCTAACAT");
            variableDomains.put("S21","ACCATACTAAA");
            variableDomains.put("S24","CTCATCCTTTA");
            variableDomains.put("S27","AACACTCTATT");
            variableDomains.put("S30","CCATTACAATC");
            variableDomains.put("S33","ACTCAAACATA");
            variableDomains.put("S36","AACTAAACAAC");
            variableDomains.put("S39","CTATACACACC");
            variableDomains.put("S42","CTTTTCACTAT");
            variableDomains.put("S45","CCCAAAACCCA");
            variableDomains.put("S48","TCACCACTATA");
            variableDomains.put("S51","CAAACTACATC");

        }
        Map<String, String[]> oligomerDomains = new TreeMap<>();
        {
            oligomerDomains.put("AA",new String[]{"c","S6","c","T","c","S5","c"});
            oligomerDomains.put("AB",new String[]{"c.c","c.T","c.c","c.S5","c.c","c.T","c.c"});
            oligomerDomains.put("AC",new String[]{"c","S7","c","T","c","S5","c"});
            oligomerDomains.put("AD",new String[]{"c","S5","c"});
            oligomerDomains.put("AE",new String[]{"c.c","c.S5","c.c","c.T","c.c","c.S2"});
            oligomerDomains.put("AF",new String[]{"c","S5","c","T","c","S2","c"});
            oligomerDomains.put("AG",new String[]{"c.c","c.T","c.c","c.S2","c.c","c.T","c.c"});
            oligomerDomains.put("AH",new String[]{"c","S2","c","T","c","S1","c"});
            oligomerDomains.put("AI",new String[]{"c.c","c.T","c.c","c.S1","c.c","c.T","c.c"});
            oligomerDomains.put("AJ",new String[]{"c","S10","c","T","c","S1","c"});
            oligomerDomains.put("AK",new String[]{"c","S1","c"});
            oligomerDomains.put("AL",new String[]{"c.c","c.S1","c.c","c.T","c.c","c.S4"});
            oligomerDomains.put("AM",new String[]{"c","S1","c","T","c","S4","c"});
            oligomerDomains.put("AN",new String[]{"c.c","c.T","c.c","c.S4","c.c","c.T","c.c"});
            oligomerDomains.put("AO",new String[]{"c","S2","c","T","c","S3","c"});
            oligomerDomains.put("AP",new String[]{"c.c","c.T","c.c","c.S3","c.c","c.T","c.c"});
            oligomerDomains.put("AQ",new String[]{"c","S11","c","T","c","S3","c"});
            oligomerDomains.put("AR",new String[]{"c","S3","c"});
            oligomerDomains.put("AS",new String[]{"c.c","c.S3","c.c","c.T","c.c","c.S12"});
            oligomerDomains.put("AT",new String[]{"c","S3","c","T","c","S12","c"});
            oligomerDomains.put("AU",new String[]{"c.c","c.T","c.c","c.S12","c.c","c.T","c.c"});
            oligomerDomains.put("AW",new String[]{"c.c","c.T","c.c","c.S8","c.c","c.T","c.c"});
            oligomerDomains.put("AX",new String[]{"c","S15","c","T","c","S8","c"});
            oligomerDomains.put("AY",new String[]{"c","S8","c"});
            oligomerDomains.put("AZ",new String[]{"c.c","c.S8","c.c","c.T","c.c","c.S16"});
            oligomerDomains.put("BA",new String[]{"c","S8","c","T","c","S16","c"});
            oligomerDomains.put("BB",new String[]{"c.c","c.T","c.c","c.S16","c.c","c.T","c.c"});
            oligomerDomains.put("BD",new String[]{"c.c","c.T","c.c","c.S17","c.c","c.T","c.c"});
            oligomerDomains.put("BE",new String[]{"c","S19","c","T","c","S17","c"});
            oligomerDomains.put("BF",new String[]{"c","S17","c"});
            oligomerDomains.put("BG",new String[]{"c.c","c.S17","c.c","c.T","c.c","c.S20"});
            oligomerDomains.put("BH",new String[]{"c","S17","c","T","c","S20","c"});
            oligomerDomains.put("BI",new String[]{"c.c","c.T","c.c","c.S20","c.c","c.T","c.c"});
            oligomerDomains.put("BJ",new String[]{"c","S20","c","T","c","S21","c"});
            oligomerDomains.put("BK",new String[]{"c","S20","c","T","c","S22","c"});
            oligomerDomains.put("BL",new String[]{"c","S16","c","T","c","S18","c"});
            oligomerDomains.put("BM",new String[]{"c","S4","c","T","c","S9","c"});
            oligomerDomains.put("BN",new String[]{"c","S12","c","T","c","S13","c"});
            oligomerDomains.put("BO",new String[]{"c","S12","c","T","c","S14","c"});
            oligomerDomains.put("BP",new String[]{"c","S2","c","T","c","S8","c"});
            oligomerDomains.put("BQ",new String[]{"c","S2","c","T","c","S17","c"});
            oligomerDomains.put("BR",new String[]{"c","S4","c","T","c","S23","c"});
            oligomerDomains.put("BS",new String[]{"c","S16","c","T","c","S24","c"});
            oligomerDomains.put("CA",new String[]{"c","S6","c"});
            oligomerDomains.put("CB",new String[]{"c.c","c.S6","c.c","c.T","c.c"});
        }

        SeqEvo s = new SeqEvo();
        SeqEvo.Request req = new SeqEvo.Request(parameters, fixedDomains, variableDomains, oligomerDomains, System.out);
        SeqEvo.Report rep = s.run(req);
        System.exit(0);
    }

}

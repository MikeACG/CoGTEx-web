/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

import java.util.Arrays;

/**
 *
 * @author INTEL
 */
public class DirStructurer {
    
    public static String version2folder(String versionName) {
        
        String versionFolder = "";
        switch (versionName) {
            
            case "TPM":
                versionFolder =  "v0.2A";
                break;
            
            case "Z-score":
                versionFolder = "v0.2C";
                break;
            
        }
        
        return versionFolder;
    }
    
    public static String ensembl2folder(String ensembl, long each) {
        
        long ensNumber = Long.parseLong(ensembl.replace("ENSG", ""));
        long lowLimit = ensNumber - (ensNumber % each);
        long upLimit = lowLimit + (each - 1);
        
        int ndigits = String.valueOf(lowLimit).length();
        int nZerosPad = (ndigits > 11) ? 0 : 11 - ndigits;
        String padStr = repeatStr('0', nZerosPad);
        
        String folderFirstHalf = "ENSG" + padStr + String.valueOf(lowLimit);
        String folderSecondHalf = "ENSG" + padStr + String.valueOf(upLimit);
        return folderFirstHalf + "_" + folderSecondHalf + "/";
    }
    
    public static String repeatStr(char c, int n) {
        
        if (n == 0) return "";
        char[] chars = new char[n];
        Arrays.fill(chars, c);
        return new String(chars);
        
    }
    
}

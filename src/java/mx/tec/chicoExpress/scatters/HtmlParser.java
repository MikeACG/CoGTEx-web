/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

//import java.util.Arrays;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class HtmlParser {
    
    public static String legendTable(List<String> labels, List<String> colors, int npercol) {
        String shapeClass = "ring";
        String legendClass = "legendTable";
        
        int nlabels = labels.size();
        int ncols = nlabels / npercol;
        int remainder = nlabels % npercol;
        int ncolsAdj = ncols;
        if (remainder != 0) ncolsAdj += 1;
        ncolsAdj *= 2;
        int k = 0;
        
        String html = "<table class='" + legendClass + "'>";
        String label, color;
        int i, j;
        for (i = 0; i < npercol; i++) {
            html += "<tr>";
            for (j = 0; j < ncolsAdj; j++) {
                if (i >= remainder & j >= ncols * 2) { // no info left to add
                    html += "<td> </td>";
                } else  if (j % 2 == 0){ // icon column
                    color = colors.get(k);
                    html += "<td> <span class='" + shapeClass + "' style='--color: " + color + ";'></span> </td>";
                } else { // label column
                    label = labels.get(k++);
                    html += "<td>" + label + "</td>";
                }
            }
            html += "</tr>";
        }
        html += "</table>";
        
        return html;
    }
    
    public static String contingencyTableVertical(GTest gtest) {
        
        int n = gtest.getNcats();
        int[][] obs = reverseRowsInt(transposeInt(gtest.getObs()));
        double[][] expec = reverseRowsDoub(transposeDoub(gtest.getExpec()));
        double[][] g = reverseRowsDoub(transposeDoub(gtest.getGcells()));
        double[][] p = reverseRowsDoub(transposeDoub(gtest.getPvals()));
        
        double[][] pvalPercs = pvals2percs(p);
        String[][] pvalColors = pvals2colors(p, obs, expec, 0.05);
        
        String html = "<table class='contingencyTable'>";
        html += "<caption> ";
        html += "<p class='obsP'> Observed </p> ";
        html += "<p class='expecP'> Expected </p> ";
        html += "<p class='statP'> G-stat </p> ";
        html += "</caption>";
        
        int i, j;
        String colorHeight;
        for (i = 0; i < n; i++) {
            html += "<tr>";
            for (j = 0; j < n; j++) {
                colorHeight = "100% " + pvalPercs[i][j] + "%";
                html += "<td class='contingencyCell'" + 
                        "' style='--color: " + pvalColors[i][j] + 
                        "; --colorHeight: " + colorHeight + ";'>";
                html += "<p class='obsP'>" + obs[i][j] + "</p> ";
                html += "<p class='expecP'>" + format(expec[i][j]) + "</p> ";
                html += "<p class='statP'>" + format(g[i][j]) + "</p> ";
                html += "</td>";
            }
            html += "</tr>";
        }
        html += "</table>";
        
        return html;
    }
    
    public static double[][] pvals2percs(double[][] pvals) {
        
        int n = pvals.length;
        int m = pvals[0].length;
        
        double[][] minusLogPvals = new double[n][m];
        int i, j, iMin = 0, jMin = 0;
        double logp, currentMin = Double.POSITIVE_INFINITY;
        for (i = 0; i < n; i++) {
            for (j = 0; j < m; j++) {
                logp = (Double.compare(pvals[i][j], 0) == 0) ? 
                        Math.log10(Double.MIN_VALUE) : Math.log10(pvals[i][j]);
                if (logp < currentMin) {
                    currentMin = logp;
                    iMin = i;
                    jMin = j;
                }
                minusLogPvals[i][j] = logp * -1;
            }
        }
        
        double maxMinusLogPval = minusLogPvals[iMin][jMin];
        double[][] out = new double[n][m];
        for (i = 0; i < n; i++) {
            for (j = 0; j < m; j++) {
                out[i][j] = (minusLogPvals[i][j] * 100) / maxMinusLogPval;
            }
        }
        
        return out;
    }
    
    public static String[][] pvals2colors(double[][] pvals, int[][] obs, double[][] expec, double alpha) {
        
        int n = pvals.length;
        int m = pvals[0].length;
        
        String[][] out = new String[n][m];
        int i, j;
        for (i = 0; i < n; i++) {
            for (j = 0; j < m; j++) {
                if (pvals[i][j] < alpha && obs[i][j] > expec[i][j]) {
                    out[i][j] = "#fccfcf";
                } else if (pvals[i][j] < alpha && obs[i][j] < expec[i][j]) {
                    out[i][j] = "#cce0ff";
                } else {
                    out[i][j] = "#dedede";
                }
            }
        }
        
        return out;
    }
    
    public static double format(double v) {
        double roundFactor = 100.0;
        
        double vRounded = Math.round(v * roundFactor) / roundFactor;
        
        return vRounded;
    }
    
    public static double [][] transposeDoub(double[][] arr) {
        
        int nrows = arr.length;
        int ncols = arr[0].length;
        
        double[][] out = new double[nrows][ncols];
        int i, j;
        for (i = 0; i < nrows; i++) {
            for (j = 0; j < ncols; j++) {
                out[j][i] = arr[i][j];
            }
        }
        
        return out;
    }
    
    public static int [][] transposeInt(int[][] arr) {
        
        int nrows = arr.length;
        int ncols = arr[0].length;
        
        int[][] out = new int[nrows][ncols];
        int i, j;
        for (i = 0; i < nrows; i++) {
            for (j = 0; j < ncols; j++) {
                out[j][i] = arr[i][j];
            }
        }
        
        return out;
    }
    
    public static double [][] reverseRowsDoub(double[][] arr) {
        
        int nrows = arr.length;
        int ncols = arr[0].length;
        
        double[][] out = new double[nrows][ncols];
        int i, j, ir;
        for (i = 0; i < nrows; i++) {
            ir = nrows - i - 1;
            for (j = 0; j < ncols; j++) {
                out[ir][j] = arr[i][j];
            }
        }
        
        return out;
    }
    
    public static int [][] reverseRowsInt(int[][] arr) {
        
        int nrows = arr.length;
        int ncols = arr[0].length;
        
        int[][] out = new int[nrows][ncols];
        int i, j, ir;
        for (i = 0; i < nrows; i++) {
            ir = nrows - i - 1;
            for (j = 0; j < ncols; j++) {
                out[ir][j] = arr[i][j];
            }
        }
        
        return out;
    }
    
    public static String contingencyTableHorizontal(GTest gtest) {
        
        int n = gtest.getNcats();
        int[][] obs = reverseRowsInt(transposeInt(gtest.getObs()));
        double[][] expec = reverseRowsDoub(transposeDoub(gtest.getExpec()));
        double[][] g = reverseRowsDoub(transposeDoub(gtest.getGcells()));
        double[][] p = reverseRowsDoub(transposeDoub(gtest.getPvals()));

        double[][] pvalPercs = pvals2percs(p);
        String[][] pvalColors = pvals2colors(p, obs, expec, 0.05);
        
        String html = "<div> ";
        html += "<p class='obsP'> Observed </p> ";
        html += "<p class='expecP'> Expected </p> ";
        html += "<p class='statP'> G-stat </p> ";
        html += "</div> ";
        html += "<div class='contingencySubdiv'> ";
        html += "<table class='contingencyTable'>";

        int i, j;
        String colorHeight;
        for (i = 0; i < n; i++) {
            html += "<tr>";
            for (j = 0; j < n; j++) {
                colorHeight = "100% " + pvalPercs[i][j] + "%";
                html += "<td class='contingencyCell'" + 
                        "' style='--color: " + pvalColors[i][j] + 
                        "; --colorHeight: " + colorHeight + ";'>";
                html += "<p class='obsP'>" + obs[i][j] + "</p> ";
                html += "<p class='expecP'>" + format(expec[i][j]) + "</p> ";
                html += "<p class='statP'>" + format(g[i][j]) + "</p> ";
                html += "</td>";
            }
            html += "</tr>";
        }
        html += "</table>";
        html += "</div>";

        return html;
    }
}

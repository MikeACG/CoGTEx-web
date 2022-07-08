/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package mx.tec.chicoExpress.rankComp;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.tec.chicoExpress.rankComp.GzTableReader.Pair;

/**
 *
 * @author mike
 */
@WebServlet(name = "RankCompServlet", urlPatterns = {"/RankCompServlet"})
public class RankCompServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RankCompServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RankCompServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String perGeneSep = "\t"; // field separator for the files in perGeneDir
        int perGeneDirSize = 500; // maximum number of files per folder in perGeneDir
        double formatFactor = 100.0;
        String ensembl = request.getParameter("ensembl");
        String symbol = request.getParameter("symbol");
        
        // work on getting first ranks vector
        String xVersionName = request.getParameter("xVersion");
        String xVersion = (xVersionName.equals("Base")) ? "v0.2A" : "v0.2C";
        String xPerGeneDir = "chicoExpress/" + xVersion + "/per-gene-tables/"; // path to directory containing folders organizing separator-based gz files with the gene lists of each gene
        String xPerGeneHeadersPath = "chicoExpress/" + xVersion + "/aux-files/geneListHeaders.txt"; // path to file containing a single column with the headers that one wants to display for each of the columns of the gene lists, they correspond with to the columns in any perGeneFile
        String xEnsemblsFile = "chicoExpress/" + xVersion + "/aux-files/ensembls.txt"; // file with a single column specifying the ensembl ids of the genes in the database
        String xSymbolsFile = "chicoExpress/" + xVersion + "/aux-files/symbols.txt"; // file with a single column specifying the gene symbols of the genes in the database
        // get the column index of the desired ranks vector in the per genes files
        List<String> xPerGeneHeaders = 
                SimpleFileReader.readSingleField(xPerGeneHeadersPath);
        String xDatabase = request.getParameter("xdb");
        int xcolIdx = xPerGeneHeaders.indexOf(xDatabase);
        // load the values and index
        String xListPath = xPerGeneDir + Ensembl2Folder.convert(ensembl, 
                perGeneDirSize) + ensembl + ".txt.gz";
        String compSize = request.getParameter("compSize");
        int ntop = (compSize.equals("max")) 
                ? -1 : Integer.parseInt(compSize);
        List<Pair> x = GzTableReader.readCol2pairs(xListPath, perGeneSep, xcolIdx);
        // sort, rank and only leave top n values
        Collections.sort(x);
        List<Double> xVals = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) xVals.add( ((double) ((int) x.get(i).getValue())) / formatFactor );
        List<Double> xRanks = Ranker.rankAvgTie(xVals);
        GzTableReader.filterPair(x, xVals, xRanks, ntop);
        // get gene ids for ntop genes in the rank
        List<String> xEnsembls = SimpleFileReader.readSingleField(xEnsemblsFile);
        List<String> xSymbols = SimpleFileReader.readSingleField(xSymbolsFile);
        int n = xEnsembls.size();
        String[] usedEnsembls = new String[n];
        String[] usedSymbols = new String[n];
        int j;
        for (int i = 0; i < x.size(); i++) {
            j = (int) x.get(i).getIndex();
            usedEnsembls[i] = xEnsembls.get(j);
            usedSymbols[i] = xSymbols.get(j);
        }
        
        // work on second ranks vector
        String yVersionName = request.getParameter("yVersion");
        String yVersion = (yVersionName.equals("Base")) ? "v0.2A" : "v0.2C";
        String yPerGeneDir = "chicoExpress/" + yVersion + "/per-gene-tables/"; // path to directory containing folders organizing separator-based gz files with the gene lists of each gene
        String yPerGeneHeadersPath = "chicoExpress/" + yVersion + "/aux-files/geneListHeaders.txt"; // path to file containing a single column with the headers that one wants to display for each of the columns of the gene lists, they correspond with to the columns in any perGeneFile
        // get the column index of the desired ranks vector in the per genes files
        List<String> yPerGeneHeaders = 
                SimpleFileReader.readSingleField(yPerGeneHeadersPath);
        String yDatabase = request.getParameter("ydb");
        int ycolIdx = yPerGeneHeaders.indexOf(yDatabase);
        // load the values and index
        String yListPath = yPerGeneDir + Ensembl2Folder.convert(ensembl, 
                perGeneDirSize) + ensembl + ".txt.gz";
        List<Pair> y = GzTableReader.readCol2pairs(yListPath, perGeneSep, ycolIdx);
        // sort and rank all genes
        Collections.sort(y);
        List<Double> yValsAll = new ArrayList<>();
        for (int i = 0; i < y.size(); i++) yValsAll.add( ((double) ((int) y.get(i).getValue())) / formatFactor );
        List<Double> yRanksAll = Ranker.rankAvgTie(yValsAll);
        // get the y ranks and values of the genes in x
        List<Double> yRanks = new ArrayList<>();
        List<Double> yVals = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            j = (int) x.get(i).getIndex();
            yRanks.add(yRanksAll.get(j));
            yVals.add(yValsAll.get(j));
        }
        
        // pack to plotly object
        PlotlyScatter plotlyScatter = new PlotlyScatter();
        plotlyScatter.trace.setX(xRanks);
        plotlyScatter.trace.setY(yRanks);
        plotlyScatter.trace.setText(usedEnsembls, usedSymbols, xVals, yVals);
        plotlyScatter.layout.setTitle(ensembl, symbol, xEnsembls.size());
        plotlyScatter.layout.xaxis.setTitle(xVersionName, xDatabase);
        plotlyScatter.layout.yaxis.setTitle(yVersionName, yDatabase);
        //plotlyScatter.layout.xaxis.setRange(new int[]{1, m});
        //plotlyScatter.layout.yaxis.setRange(new int[]{1, m});
        
        // serve
        String json = new Gson().toJson(plotlyScatter);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json); 
        
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

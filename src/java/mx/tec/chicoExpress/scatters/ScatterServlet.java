/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package mx.tec.chicoExpress.scatters;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mike
 */
@WebServlet(name = "ScatterServlet", urlPatterns = {"/ScatterServlet"})
public class ScatterServlet extends HttpServlet {

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
        
        String ensemblsPath = "chicoExpress/webFiles/producedData/ensembls.txt"; // path to single column file with the ensembls for all genes in the database (assumes same set for all possible versions)
        String symbolsPath = "chicoExpress/webFiles/producedData/symbols.txt"; // path to single column file with the gene symbols for all genes in the database (assumes same set for all possible versions)
        String perGeneSep = "\t"; // field separator for the files in perGeneDir
        int perGeneDirSize = 500; // maximum number of files per folder in perGeneDir
        double formatFactor = 100.0;
        
        // get paths to source files
        // x variable
        String xVersionName = request.getParameter("xVersion");
        String xVersion = DirStructurer.version2folder(xVersionName);
        String xPerGeneDir = "chicoExpress/" + xVersion + "/per-gene-tables/"; // path to directory containing folders organizing separator-based gz files with the gene lists of each gene
        String xPerGeneHeadersPath = "chicoExpress/webFiles/producedData/geneListHeaders/" + xVersion; // path to file containing a single column with the headers that one wants to display for each of the columns of the gene lists, they correspond with to the columns in any perGeneFile
        String xEnsembl = request.getParameter("xEnsembl");
        String xListPath = xPerGeneDir + DirStructurer.ensembl2folder(xEnsembl, 
                perGeneDirSize) + xEnsembl + ".txt.gz";
        // y variable
        String yVersionName = request.getParameter("yVersion");
        String yVersion = DirStructurer.version2folder(yVersionName);
        String yPerGeneDir = "chicoExpress/" + yVersion + "/per-gene-tables/"; // path to directory containing folders organizing separator-based gz files with the gene lists of each gene
        String yPerGeneHeadersPath = "chicoExpress/webFiles/producedData/geneListHeaders/" + yVersion; // path to file containing a single column with the headers that one wants to display for each of the columns of the gene lists, they correspond with to the columns in any perGeneFile
        String yEnsembl = request.getParameter("yEnsembl");
        String yListPath = yPerGeneDir + DirStructurer.ensembl2folder(yEnsembl, 
                perGeneDirSize) + yEnsembl + ".txt.gz";
        
        // if x and y are the same gene in same version some things can be sped up later
        boolean yIsX = xEnsembl.equals(yEnsembl) && xVersion.equals(yVersion);

        // get the x variable data
        List<String> xPerGeneHeaders = 
                SimpleFileReader.readSingleField(xPerGeneHeadersPath);
        String xDatabase = request.getParameter("xdb");
        int xcolIdx = xPerGeneHeaders.indexOf(xDatabase);
        List<Gene> genes;
        if (yIsX) { // only have to read 1 file
            String yDatabase = request.getParameter("ydb");
            int ycolIdx = xPerGeneHeaders.indexOf(yDatabase);
            genes = GzTableReader.readCols2genes(xListPath, 
                    perGeneSep, formatFactor, xcolIdx, ycolIdx);
        } else {
            genes = GzTableReader.readCol2genes(xListPath, 
                    perGeneSep, formatFactor, xcolIdx);
        }
        
        // get the y variable data
        String yDatabase = request.getParameter("ydb");
        if (!yIsX) {
            List<String> yPerGeneHeaders = 
                SimpleFileReader.readSingleField(yPerGeneHeadersPath);
            int ycolIdx = yPerGeneHeaders.indexOf(yDatabase);
            GzTableReader.readCol2geneList(genes, yListPath,
                    perGeneSep, formatFactor, ycolIdx);
        }
        
        // add metadata to the data points
        SimpleFileReader.readSingleField2geneList(genes, ensemblsPath, "ensembl");
        SimpleFileReader.readSingleField2geneList(genes, symbolsPath, "symbol");
        
        // rank the values of the axis that is NOT the guide first
        String sortGuide = request.getParameter("sortGuide");
        String antiGuide = sortGuide.equals("x") ? "y" : "x";
        GeneListOperator geneListOperator = new GeneListOperator();
        Collections.sort(genes, geneListOperator.new GeneComparator(antiGuide));
        Collections.reverse(genes); // descending order
        geneListOperator.rankAvgTie(genes, antiGuide);
        
        // rank the values of the axis that IS the guide last
        Collections.sort(genes, geneListOperator.new GeneComparator(sortGuide));
        Collections.reverse(genes); // descending order
        geneListOperator.rankAvgTie(genes, sortGuide);
        
        // filter data points to display if neccessary
        String compSize = request.getParameter("compSize");
        if (!compSize.equals("max")) geneListOperator.head(genes, Integer.parseInt(compSize));
        
        // log of ranks if neccessary
        String xformat = request.getParameter("xformat");
        String yformat = request.getParameter("yformat");
        if (xformat.equals("logranks")) geneListOperator.logRank(genes, "x");
        if (yformat.equals("logranks")) geneListOperator.logRank(genes, "y");
        
        // pack to plotly object
        PlotlyScatter plotlyScatter = new PlotlyScatter();
        switch (xformat) {
            
            case "estimates":
              plotlyScatter.trace.setX(geneListOperator.getEstimates(genes, "x"));
              break;
              
            case "ranks":
              plotlyScatter.trace.setX(geneListOperator.getRanks(genes, "x"));
              break;
              
            case "logranks":
              plotlyScatter.trace.setX(geneListOperator.getLogranks(genes, "x"));
              break;
            
        }
        switch (yformat) {
            
            case "estimates":
              plotlyScatter.trace.setY(geneListOperator.getEstimates(genes, "y"));
              break;
              
            case "ranks":
              plotlyScatter.trace.setY(geneListOperator.getRanks(genes, "y"));
              break;
              
            case "logranks":
              plotlyScatter.trace.setY(geneListOperator.getLogranks(genes, "y"));
              break;
            
        }
        plotlyScatter.trace.setText(genes);
        
        String xsymbol = request.getParameter("xsymbol");
        String ysymbol = request.getParameter("ysymbol");
        plotlyScatter.layout.xaxis.setTitle(xEnsembl, xsymbol, xformat, xVersionName, xDatabase);
        plotlyScatter.layout.yaxis.setTitle(yEnsembl, ysymbol, yformat, yVersionName, yDatabase);
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

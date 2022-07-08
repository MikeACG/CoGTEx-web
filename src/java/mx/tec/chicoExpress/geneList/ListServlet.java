/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneList;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author INTEL
 */
@WebServlet(name = "ListServlet", urlPatterns = {"/ListServlet"})
public class ListServlet extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ListServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ListServlet at " + request.getContextPath() + "</h1>");
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
        
        String versionName = request.getParameter("Version");
        String version = (versionName.equals("Base")) ? "v0.2A" : "v0.2C";
        
        String precolsPath = "chicoExpress/" + version + "/aux-files/perGeneTablesPrecols.txt"; // psth to separator-based file with 3 columns (ensembl, gene symbol and entrez) of identifiers for all genes in the databaser
        String precolsSep = "\t"; // field separator for the precolsPath file
        String[] precolsHeaders = new String[] {"Ensembl", "Gene Symbol", "Entrez"}; // headers for the columns in the precolsPathFile
        String perGeneDir = "chicoExpress/" + version + "/per-gene-tables/"; // path to directory containing folders organizing separator-based gz files with the gene lists of each gene
        String perGeneSep = "\t"; // field separator for the files in perGeneDir
        int perGeneDirSize = 500; // maximum number of files per folder in perGeneDir
        String perGeneHeadersPath = "chicoExpress/" + version + "/aux-files/geneListHeaders.txt"; // path to file containing a single column with the headers that one wants to display for each of the columns of the gene lists, they correspond with to the columns in any perGeneFile
        double formatFactor = 100.0;
        int formatAfter = 2; // all columns of gene tables after this index will be converted from int to double by dividing their values over formatFactor
        String [] deltasFor = new String[]{" Sex ", " Age ", " Ischemia "};
        
        String ensembl = request.getParameter("Gene ensembl");
        String orderDatabase = request.getParameter("Order database");
        String listSize = request.getParameter("List size");
        int geneListNrows = (listSize.equals("max")) 
                ? -1 : Integer.parseInt(listSize);
        List<List<String>> precols = SimpleFileReader.readMultiField(
                precolsPath, precolsSep);
        
        List<String> perGeneHeaders = 
                SimpleFileReader.readSingleField(perGeneHeadersPath);
        int orderColIdx = perGeneHeaders.indexOf(orderDatabase);
        String listPath = perGeneDir + Ensembl2Folder.convert(ensembl, 
                perGeneDirSize) + ensembl + ".txt.gz";
        List<List<String>> geneList = Gz2NestedList.main(listPath, perGeneSep, 
                geneListNrows, orderColIdx, precols);
        
        List<String> allHeaders = new ArrayList<>(Arrays.asList(precolsHeaders));
        allHeaders.addAll(perGeneHeaders);
        List<DTCol> dtCols = Headers2DTCols.convert(allHeaders, formatAfter);
        DataTable dataTable = new DataTable(geneList, dtCols, 
                orderColIdx + precolsHeaders.length);
        dataTable.intStrDivide2doubleStr(formatAfter, formatFactor);
        dataTable.addMaxDeltas(deltasFor, formatFactor);
        
        // pack the objects to be served and serve them
        ServedObj r = new ServedObj(dataTable, 
                DataTableHTML.build(dataTable, formatAfter, formatFactor));
        String json = new Gson().toJson(r);
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

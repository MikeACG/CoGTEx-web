/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneHome;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author INTEL
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/HomeServlet"})
public class HomeServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * 
     * TODO:
     * - Improve the gene info table reading speed
     * 
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet HomeServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HomeServlet at " + request.getContextPath() + "</h1>");
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
        
        String infoFile = "chicoExpress/webFiles/producedData/geneinfo.txt"; // path to separator text file containing info for the genes in the database, can be of any number of columns, they are already expected to be in the intended order of display
        String infoFileSep = "\t"; // separator delimiting the columns of the last file
        String headersFile = "chicoExpress/webFiles/producedData/geneinfoHeaders.txt"; // file with a single column specifying the names of the columns in infoFile
        String[] shownHeaders = new String[] {"Ensembl", "Gene Symbol", 
            "Entrez", "Description", "TPM Pearson >= 0.65 (Q99)"}; // headers of columns to be shown by default
        String orderHeader = "TPM Pearson >= 0.65 (Q99)"; // header of column by which the geneInfo will be sorted by default
        
        List<String> infoHeaders = SimpleFileReader.readSingleField(headersFile);
        DTCol[] dtCols = Headers2DTCols.convert(infoHeaders, shownHeaders);
        List<String[]> geneInfo = SimpleFileReader.readMultiField(infoFile, 
                infoFileSep);
        
        DataTable dataTable = new DataTable(geneInfo, dtCols, orderHeader);
        String dataTableHTML = DataTableHTML.buildSkeleton(dataTable);
        String selectorsHTML = DataTableHTML.buildColSelectors(dataTable);
        
        // pack the objects to be served and serve them
        ServedObj r = new ServedObj(dataTable, dataTableHTML, selectorsHTML);
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

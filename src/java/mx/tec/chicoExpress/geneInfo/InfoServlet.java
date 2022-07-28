/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneInfo;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author INTEL
 */
@WebServlet(name = "InfoServlet", urlPatterns = {"/InfoServlet"})
public class InfoServlet extends HttpServlet {

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
            out.println("<title>Servlet InfoServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet InfoServlet at " + request.getContextPath() + "</h1>");
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
        
        String infoFile = "chicoExpress/webFiles/producedData/geneinfo.txt"; // path to separator text file containing info for the genes in the database, can be of any number of columns, they are already expected to be in the intended order of display
        String infoFileSep = "\t"; // separator delimiting the columns of the last file
        String headersFile = "chicoExpress/webFiles/producedData/geneinfoHeaders.txt"; // file with a single column specifying the names of the columns in infoFile
        String[] shownHeaders = new String[] {"Biotype", "Description"}; // headers of columns to be shown by default
        String filteringColumn = "Ensembl";
        
        List<String> infoHeaders = SimpleFileReader.readSingleField(headersFile);
        int filteringColumnIdx = infoHeaders.indexOf(filteringColumn);
        
        Set<String> toKeepOnFilter = new HashSet<>
            (Arrays.asList(request.getParameter("ids").split("\\|")));
        List<GeneInfo> infoArray = SimpleFileReader.readMultiFieldFilter2GeneInfo(
                infoFile, infoFileSep, filteringColumnIdx, toKeepOnFilter, 
                infoHeaders, shownHeaders);
        
        // pack the objects to be served and serve them
        ServedObj r = new ServedObj(infoArray, shownHeaders);
        String json = new Gson().toJson(r);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        
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

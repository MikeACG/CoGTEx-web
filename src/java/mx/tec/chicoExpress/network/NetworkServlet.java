/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.network;

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
@WebServlet(name = "NetworkServlet", urlPatterns = {"/NetworkServlet"})
public class NetworkServlet extends HttpServlet {

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
            out.println("<title>Servlet NetworkServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NetworkServlet at " + request.getContextPath() + "</h1>");
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
        
        String versionName = request.getParameter("version");
        String version = (versionName.equals("Base")) ? "v0.2A" : "v0.2C";
        String database = request.getParameter("db");
        
         // tune some settings that include constants for all plots
        String ensemblsFile = "chicoExpress/" + version + "/aux-files/ensembls.txt"; // file with a single column specifying the ensembl ids of the genes in the database
        String symbolsFile = "chicoExpress/" + version + "/aux-files/symbols.txt"; // file with a single column specifying the gene symbols of the genes in the database
        String dbFile = "chicoExpress/" + version + "/source-matrix/sql/" + database + ".sqlite";
        int formatFactor = 100;
        int diagval = (database.equals("mins/G")) ? 0 : 1 * formatFactor;
        
        String ensemblsStr = request.getParameter("ensembls");
        String[] ensembls = ensemblsStr.split("\\|");
        
        List<String> ensemblsDb = SimpleFileReader.readSingleField(ensemblsFile);
        double[][] associations = AssociationsFetcher.fetchGeneset(ensembls, 
                dbFile, ensemblsDb, formatFactor, diagval);
        HierarchicalClusterer hc = new HierarchicalClusterer(associations);
        hc.calculateEuclideanDists();
        hc.cluster();
        
        List<String> symbolsDb = SimpleFileReader.readSingleField(symbolsFile);
        List<String> geneNames = GeneConverter.ensembls2symbols(ensembls, 
                ensemblsDb, symbolsDb);
        
        ClustergrammerJson cj = new ClustergrammerJson(associations);
        cj.makeRowNodes(hc.getClusters(), geneNames);
        cj.makeColNodes();
        cj.makeMatrixColors();
        
        // pack the objects to be served and serve them
        //ServedObj r = new ServedObj();
        String json = new Gson().toJson(cj);
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

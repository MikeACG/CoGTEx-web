/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.box;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author INTEL
 */
@WebServlet(name = "BoxServlet", urlPatterns = {"/BoxServlet"})
public class BoxServlet extends HttpServlet {

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
            out.println("<title>Servlet BoxServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BoxServlet at " + request.getContextPath() + "</h1>");
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
        
        // tune some settings that remain constant for all plots
        String ensemblsFile = "chicoExpress/" + version + "/aux-files/ensembls.txt"; // file with a single column specifying the ensembl ids of the genes in the database
        String sqlsDir = "chicoExpress/" + version + "/source-data/sql/numExp/"; // path to the directory with the SQL databases holding the gene expression
        String groupsFile = "chicoExpress/" + version + "/aux-files/sampleGroups.txt"; // path to separator-based file containing 2 columns: first are the IDs of all samples and second the group (E.g. tissue) of each sample
        String colorsFile = "chicoExpress/" + version + "/aux-files/sampleColors.txt"; // path to separator-based file containing 2 columns: first are the IDs of all samples and second the color by which each sample is identified (corresponds to the group to which the sample belongs)
        String descriptionsFile = "chicoExpress/" + version + "/aux-files/sampleDescriptions.txt"; // path to separator-based file containing 2 columns: first are the IDs of all samples and second the long name by which each sample is identified (corresponds to the group to which the sample belongs)
        String sep = "\t"; // separator which delimits the columns of the last three files
        String xTitle = null; // x-axis title for boxplot
        String yTitle = (version.equals("v0.2A")) ? 
                "Batch-corrected Log10(TPM + 1)" : "Z-score"; // y-axis title for boxplot
        double xlabsSize = 10; // x-axis labels size
        double ylabsSize = 12; // y-axis labels size
        double xlabsAngle = -45; // angle of x-axis labels
        double ylabsAngle = 0; // angle of y-axis labels 
        
        // get the internal database id (index) for the requested gene
        String symbol = request.getParameter("symbol");
        String ensembl = request.getParameter("ensembl");
        int ensemblIdx = Gene2Idx.convert(ensembl, ensemblsFile);
        
        // get correct filename for database
        String dbRequest = request.getParameter("db");
        String dbFile = DatabaseParser.makeFile(dbRequest, sqlsDir);
        String db4title = DatabaseParser.makeTitle(dbRequest, dbFile);

        // fetch all sample metadata and expression data for the gene and desired database
        HashMap<String,String> groupsMap = 
                SimpleFileReader.readBicolumn2Map(groupsFile, sep);
        HashMap<String,String> colorsMap = 
                SimpleFileReader.readBicolumn2Map(colorsFile, sep);
        HashMap<String,String> descriptionsMap = 
                SimpleFileReader.readBicolumn2Map(descriptionsFile, sep);
        Gene gene = SQLFetcher.fetchGene(ensemblIdx, dbFile);
        
        // add the sample metadata to gene according to the samples used by the gene
        gene.setGroups(groupsMap);
        gene.setColors(colorsMap);
        gene.setDescriptions(descriptionsMap);
        
        // convert data to plotly format and customize the plot's layout
        PlotlyTrace[] traces = PlotlyDataBuilder.build(gene);
        PlotlyLayout layout = new PlotlyLayout(ensembl, symbol, db4title, xTitle, 
                yTitle, xlabsAngle, ylabsAngle, xlabsSize, ylabsSize, traces);
        String[] medianColors = PlotlyDataBuilder.computeMedianColors(traces);
        
        
        // pack the objects to be served and serve them
        ServedObj r = new ServedObj(traces, layout, medianColors);
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

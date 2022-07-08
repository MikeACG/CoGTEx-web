/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.util.HashMap;

/**
 *
 * @author INTEL
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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ScatterServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ScatterServlet at " + request.getContextPath() + "</h1>");
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
     * 
     * TODO;
     * - Show doubled statistic values on contingency table cells (check correctness)
     * - Include long (more descriptive) group names in tooltips
     * - Include gene name in the coordinates of tooltips
     * - Make tooltips of breaks (lines binning expression) prettier
     * 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String versionName = request.getParameter("version");
        String version = (versionName.equals("Base")) ? "v0.2A" : "v0.2C";
        
        // tune some settings that remain constant for all plots
        String groupsPath = "chicoExpress/" + version + "/aux-files/sampleGroups.txt"; // path to separator-based file containing 2 columns: first are the IDs of all samples and second the group (E.g. tissue) of each sample
        String colorsPath = "chicoExpress/" + version + "/aux-files/sampleColors.txt"; // path to separator-based file containing 2 columns: first are the IDs of all samples and second the color by which each sample is identified (corresponds to the group to which the sample belongs)
        String sep = "\t"; // separator which delimits the columns of the last files
        int pointSize = 5; // point size in pixels of scatterplot
        int nTicks = 5; // number of axis labels denoting gene expresison values
        int nPerLgndCol = 21; // number of labels per column of the custom legend
        String in2outPltRatio = "80%"; // percentage of the plot's div ocuppied by the inner part of the chart. Increasing expands plotting area but axis labels can get cut out for example
        double axLabsSize = 10; // size in pixels of the axes labels
        boolean xAxLabRotate = false; // rotate x-axis labels 90 degrees?
        boolean yAxLabRotate = true; // rotate y-axis labels 90 degrees?
        int nBrksPerGene = 2; // number of total breaks to be plotted in the data, currently each gene is binned into 3 discrete categories so there are 2 breaks per gene
        String[] titleLabels = new String[] {"G:", "pearson:", "spearman:"}; // labels for the information in the title of the plots, must correspond with the "stats" array assembled later
        
        // get the internal database id (index) for the requested genes
        String xEnsembl = request.getParameter("xEnsembl");
        String xSymbol = request.getParameter("xSymbol");
        int xIdx = GeneFinder.main(xEnsembl);
        String yEnsembl = request.getParameter("yEnsembl");
        String ySymbol = request.getParameter("ySymbol");
        int yIdx = GeneFinder.main(yEnsembl);
        
        // get correct filename for requested database
        String database = request.getParameter("db");
        String dbFile = DatabaseParser.main(database, xIdx, yIdx, version);
        String dbName = dbFile.replace(".sqlite", "");
        String db4title = (database.equals(dbName)) ? 
                database : database + " (" + dbName + ")";
        
        // get the data associated with the genes from the requested database
        Gene x = SQLFetcher.main(xIdx, dbFile, version);
        Gene y = SQLFetcher.main(yIdx, dbFile, version);
        
        // compute association measures for the pair of genes
        GTest gtest = new GTest(x, y);
        gtest.run();
        double pearson = PearsonCorrelation.main(x, y);
        double spearman = SpearmanCorrelation.main(x, y);
        double[] stats = new double[] {gtest.getG(), pearson, spearman};
        
        // set sample groups for the retrieved data
        HashMap<String, String> groupsMap = FetchSampleMeta.getSampleMap(groupsPath, sep);
        y.setGroups(FetchSampleMeta.mapSamples(y.getSamples(), groupsMap));
        
        // set sample colors for the retrieved data
        HashMap<String, String> colorsMap = FetchSampleMeta.getSampleMap(colorsPath, sep);
        y.setColors(FetchSampleMeta.mapSamples(y.getSamples(), colorsMap));
        
        // construct the objects to be served
        GCScatterObj gcScatterObj = GCOBuilder.build(x, y);
        Axis xAxis = new Axis(new ViewWindow(x.getMinExp(), x.getMaxExp()), 
                new String[] {xEnsembl, xSymbol}, 
                Interpolater.doubSeq(x.getMinExp(), x.getMaxExp(), nTicks), 
                axLabsSize, xAxLabRotate);
        Axis yAxis = new Axis(new ViewWindow(y.getMinExp(), y.getMaxExp()), 
                new String[] {yEnsembl, ySymbol}, 
                Interpolater.doubSeq(y.getMinExp(), y.getMaxExp(), nTicks), 
                axLabsSize, yAxLabRotate);
        GCoptions gcScatterOptions = new GCoptions(xAxis, yAxis, pointSize, 
                new ChartArea(in2outPltRatio, in2outPltRatio));
        gcScatterOptions.makeTitle(stats, titleLabels, 
                db4title, x.getExpression().size(), versionName);
        CustomLegend legend = new CustomLegend(
                HtmlParser.legendTable(y.getSizedGroupLabs(), 
                        y.uniq(y.getColors()), nPerLgndCol));
        boolean contingencyVertical = Boolean.parseBoolean(request.getParameter("contingencyVertical"));
        ContingencyTable contingency = new ContingencyTable(
                (contingencyVertical) ? HtmlParser.contingencyTableVertical(gtest) : 
                        HtmlParser.contingencyTableHorizontal(gtest));
        String[] brkSeriesIdxs = Interpolater.intSeqAsStr(gcScatterObj.getGroupsN(), 
                nBrksPerGene * 2);
        
        // pack the objects to be served and serve them
        ServedObj r = new ServedObj(gcScatterObj, gcScatterOptions, legend, 
                contingency, brkSeriesIdxs);
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

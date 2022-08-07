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
        
        String precolsPath = "chicoExpress/webFiles/producedData/perGeneTablesPrecols.txt"; // path to separator-based file with 3 columns (ensembl, gene symbol and entrez) of identifiers for all genes in the databaser
        String precolsSep = "\t"; // field separator for the precolsPath file
        String[] precolsHeaders = new String[] {"Ensembl", "Gene Symbol", "Entrez"}; // headers for the columns in the precolsPathFile
        String[] versionIds = new String[] {"v0.2A", "v0.2C"}; // versions of the database to be displayed
        String[] versionNames = new String[] {"TPM", "Z-score"}; // names to be shown to the user, correspond to versionIds
        String perGeneHeadersDir = "chicoExpress/webFiles/producedData/geneListHeaders/"; // folder from where to read the headers of the gene lists columns for each version, files for each version are named as the entries versionsIds
        String versionsDir = "chicoExpress/"; // folder that contains the folders for the data of all versions
        String perGeneDir = "per-gene-tables/"; // directory in each version containing folders organizing separator-based gz files with the gene lists of each gene
        String perGeneSep = "\t"; // field separator for the files in perGeneDir
        int perGeneDirSize = 500; // maximum number of files per folder in perGeneDir
        double formatFactor = 100.0;
        int formatAfter = 2; // all columns of gene tables after this index will be converted from int to double by dividing their values over formatFactor
        //String [] deltasFor = new String[]{" Sex ", " Age ", " Ischemia "};
        String[] shownHeaders = new String[] {"Ensembl", "Gene Symbol", "Entrez", "TPM Min Pearson", "Z-score Min Pearson"}; // columns to show by default in the gene list
        
        // load the columns to be appended to every gene list (gene ids and such)
        List<List<String>> precols = SimpleFileReader.readMultiField(
                precolsPath, precolsSep);
        
        // load the gene list headers for each version of database and identify the column used to filter
        String orderVersionName = request.getParameter("orderVersionName");
        String orderDatabase = request.getParameter("orderDatabase");
        int nVersions = versionIds.length;
        List<List<String>> perVersionGeneHeaders = new ArrayList<>();
        int filterFile = -1;
        int filterColumn = -1;
        for (int i = 0; i < nVersions; i++) {
            perVersionGeneHeaders
                    .add(SimpleFileReader.readSingleField(perGeneHeadersDir + versionIds[i]));
            if (orderVersionName.equals(versionNames[i])){
                filterFile = i;
                filterColumn = perVersionGeneHeaders.get(i).indexOf(orderDatabase);
            }
            
        }
        
        // load the gene list for query gene that has all versions concatenated
        String ensembl = request.getParameter("ensembl");
        String ensemblDir = Ensembl2Folder
                .convert(ensembl, perGeneDirSize) + ensembl + ".txt.gz";
        String[] listsPaths = new String[nVersions];
        for (int i = 0; i < nVersions; i++) {
            listsPaths[i] = versionsDir + versionIds[i] + "/" + perGeneDir + ensemblDir;
        }
        String listSize = request.getParameter("listSize");
        int geneListNrows = (listSize.equals("max")) 
                ? -1 : Integer.parseInt(listSize);
        List<List<String>> geneList = Gz2NestedList.multipleReader(listsPaths, perGeneSep, 
                geneListNrows, filterFile, filterColumn, precols);
        
        // concatenate all headers and convert to data table headers, set database used to order the list as shown by default
        List<String> allHeaders = new ArrayList<>(Arrays.asList(precolsHeaders));
        for (int i = 0; i < nVersions; i++) {
            List<String> hs = new ArrayList<>();
            for (int j = 0; j < perVersionGeneHeaders.get(i).size(); j++) {
                hs.add(versionNames[i] + " " + perVersionGeneHeaders.get(i).get(j));
            }
            allHeaders.addAll(hs);
        }
        List<String> shownHeadersList = new ArrayList<>(Arrays.asList(shownHeaders));
        shownHeadersList.add(versionNames[filterFile] + " " + perVersionGeneHeaders.get(filterFile).get(filterColumn));
        List<DTCol> dtCols = Headers2DTCols.convert(allHeaders, formatAfter, shownHeadersList);
        
        // make data table object from gene list and format it in the desired form
        DataTable dataTable = new DataTable(geneList, dtCols, 
                filterColumn + precolsHeaders.length);
        dataTable.intStrDivide2doubleStr(formatAfter, formatFactor);
        //dataTable.addMaxDeltas(deltasFor, formatFactor);
        
        // pack the objects to be served and serve them
        ServedObj r = new ServedObj(dataTable, 
                DataTableHTML.build(dataTable, formatAfter, formatFactor), 
                DataTableHTML.buildVersionedColSelectors(dataTable, perVersionGeneHeaders, versionNames));
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

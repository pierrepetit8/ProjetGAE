package LoanApproval.Services;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;


public class LoanApproval extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Properties properties = System.getProperties();

        response.setContentType("text/plain");
        String idCompte = (String) request.getParameter("idCompte");
        String sommeString = request.getParameter("somme");
        Long somme = Long.valueOf(sommeString);
        boolean risk = true;
        if(somme < 100000) {
            // acc manager check account
            URL url = new URL("https://calm-cliffs-46267.herokuapp.com/checkAccount/" + idCompte);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            response.getWriter().println(content.toString());
            risk = "true".equals(content.toString());
            if(risk) {
                // post approval idCompte nom accepte
            } else {
                URL url2 = new URL("https://calm-cliffs-46267.herokuapp.com/account/" + idCompte + "/" + somme);
                URLConnection con2 = url2.openConnection();
                HttpURLConnection http = (HttpURLConnection)con2;
                http.setRequestMethod("POST"); // PUT is another valid option
                http.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(http.getOutputStream());
                out.close();
            }
        }
        response.getWriter().println("Hello App Engine - Standard using "
                + SystemProperty.version.get() + " Java "
                + properties.get("java.specification.version"));
    }

    public static String getInfo() {
        return "Version: " + System.getProperty("java.version")
                + " OS: " + System.getProperty("os.name")
                + " User: " + System.getProperty("user.name");
    }
}

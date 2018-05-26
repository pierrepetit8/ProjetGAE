package LoanApproval.Services;

import com.google.appengine.api.utils.SystemProperty;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;


public class LoanApproval extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Properties properties = System.getProperties();

        response.setContentType("text/plain");
        Long somme = (Long) request.getAttribute("somme");
        boolean risk = true;
        if(somme < 100000) {
            // acc manager check account
            // if risk
            if(risk) {
                // post approval idCompte nom accepte
            } else {
                URL url = new URL("https://calm-cliffs-46267.herokuapp.com/account");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }
        }
        URL url = new URL("https://calm-cliffs-46267.herokuapp.com/account");
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
        response.getWriter().println("Hello App Engine - Standard using "
                + SystemProperty.version.get() + " Java "
                + properties.get("java.specification.version"));
        response.getWriter().println(content.toString());
    }

    public static String getInfo() {
        return "Version: " + System.getProperty("java.version")
                + " OS: " + System.getProperty("os.name")
                + " User: " + System.getProperty("user.name");
    }
}

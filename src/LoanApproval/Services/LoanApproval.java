package LoanApproval.Services;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.Properties;


public class LoanApproval extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Properties properties = System.getProperties();

        response.setContentType("text/plain");
        String idCompte = request.getParameter("idCompte");
        String sommeString = request.getParameter("somme");
        Long somme = Long.valueOf(sommeString);
        boolean risk;
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
                try {
                    boolean accepte = getApproval(idCompte);
                    if(accepte) {
                        giveMoney(idCompte, somme);
                        response.setStatus(200);
                        JSONObject jsonObject = getAccount(idCompte);
                        response.getWriter().println("Crédit accepté approval OK");
                        response.getWriter().println(jsonObject.toString());
                    } else {
                        response.getWriter().println("Crédit refusé risque élevé et approval refusée");
                    }
                } catch (Exception e) {
                    response.setStatus(500);
                    e.printStackTrace();
                }

            } else {
                giveMoney(idCompte, somme);
                response.setStatus(200);
                try {
                    JSONObject jsonObject = getAccount(idCompte);
                    response.getWriter().println("Crédit accepté: risque faible");
                    response.getWriter().println(jsonObject.toString());
                } catch (Exception e) {
                    response.setStatus(500);
                    e.printStackTrace();
                }
            }
        } else {
            try {
                boolean accepte = getApproval(idCompte);
                if(accepte) {
                    giveMoney(idCompte, somme);
                    JSONObject jsonObject = getAccount(idCompte);
                    response.getWriter().println("Crédit accepté: approval OK");
                    response.getWriter().println(jsonObject.toString());
                } else {
                    response.getWriter().println("Crédit refusé: somme importante et approval refusée");
                }
            } catch (Exception e) {
                response.setStatus(500);
                e.printStackTrace();
            }

        }
    }

    public void giveMoney(String idCompte, Long somme) throws IOException {
        URL urlSomme = new URL("https://calm-cliffs-46267.herokuapp.com/account/" + idCompte + "/" + somme);
        URLConnection conSomme = urlSomme.openConnection();
        HttpURLConnection http = (HttpURLConnection)conSomme;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(http.getOutputStream());
        out.close();
    }

    public boolean getApproval(String idCompte) throws IOException, JSONException {
        URL urlApproval = new URL("https://calm-cliffs-46267.herokuapp.com/approval/" + idCompte);
        HttpURLConnection conApproval = (HttpURLConnection) urlApproval.openConnection();
        conApproval.setRequestMethod("GET");
        String inputLine;
        BufferedReader inApproval = new BufferedReader(
                new InputStreamReader(conApproval.getInputStream()));
        StringBuffer contentApproval = new StringBuffer();
        while ((inputLine = inApproval.readLine()) != null) {
            contentApproval.append(inputLine);
        }
        inApproval.close();
        JSONObject jsonObject = new JSONObject(contentApproval.toString());
        return (boolean) jsonObject.get("accepte");
    }

    public JSONObject getAccount(String idCompte) throws IOException, JSONException{
        URL urlAccount = new URL("https://calm-cliffs-46267.herokuapp.com/account/" + idCompte);
        HttpURLConnection conAccount = (HttpURLConnection) urlAccount.openConnection();
        conAccount.setRequestMethod("GET");
        String inputLine;
        BufferedReader inAccount = new BufferedReader(
                new InputStreamReader(conAccount.getInputStream()));
        StringBuffer contentAccount = new StringBuffer();
        while ((inputLine = inAccount.readLine()) != null) {
            contentAccount.append(inputLine);
        }
        inAccount.close();
        return new JSONObject(contentAccount.toString());
    }

}

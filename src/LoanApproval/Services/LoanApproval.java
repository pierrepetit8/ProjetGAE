package LoanApproval.Services;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.Date;


public class LoanApproval extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/plain");
        String idCompte = null;
        String sommeString = request.getParameter("somme");
        Long somme = Long.valueOf(sommeString);
        boolean risk = true;
        if(somme < 100000) {
            try {
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
                risk = "true".equals(content.toString());
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(500);
            }
            if(risk) {
                try {
                    boolean accepte = getApproval(idCompte);
                    if(accepte) {
                        giveMoney(idCompte, somme);
                        response.setStatus(200);
                        JSONObject jsonObject = getAccount(idCompte);
                        jsonObject.put("message", "Crédit accepté approval OK");
                        response.getWriter().println(jsonObject.toString());
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("message", "Crédit refusé risque élevé et approval refusée");
                    }
                } catch (Exception e) {
                    response.setStatus(500);
                    e.printStackTrace();
                }

            } else {
                try {
                    giveMoney(idCompte, somme);
                    response.setStatus(200);
                    JSONObject jsonObject = getAccount(idCompte);
                    jsonObject.put("message", "Crédit accepté: risque faible");
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
                    jsonObject.put("message", "Crédit accepté: approval OK");
                    response.getWriter().println(jsonObject.toString());
                } else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message", "Crédit refusé: somme importante et approval refusée");
                }
            } catch (Exception e) {
                response.setStatus(500);
                e.printStackTrace();
            }

        }
        logRequestDatastore(idCompte);
    }

    public void giveMoney(String idCompte, Long somme) throws IOException {
        String url = "https://calm-cliffs-46267.herokuapp.com/account/" + idCompte + "/" + somme;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.flush();
        wr.close();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

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
    public void logRequestDatastore(String idCompte) {
        Entity request = new Entity("Request");
        request.setProperty("idCompte", idCompte);
        request.setProperty("dateRequest", new Date());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(request);
    }

}

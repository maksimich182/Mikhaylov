package Publication;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import MyException.EmptyCategoryException;
import MyException.ErrorLinkException;
import org.json.*;


public class PublicationClass implements Runnable {
    private String _url;
    private String _description;
    private String _link;

    public PublicationClass(String link) {
        _description = "";
        _url = "";
        _link = link;
    }

    public String getUrl() {
        return _url;
    }

    public String getDescription() {
        return _description;
    }

    public String getLink() {
        return _link;
    }

    @Override
    public void run() {
        try {
            String tempLink = _link + "0?json=true";

            JSONObject joGeneral = new JSONObject(getString(tempLink));
            int totalCount = joGeneral.getInt("totalCount");
            if (totalCount == 0) {
                throw new EmptyCategoryException(tempLink);
            }

            int nmPublication = new Random().nextInt(joGeneral.getInt("totalCount"));
            int countPublicationOnPage = joGeneral.getJSONArray("result").length();
            int nmPage = nmPublication / countPublicationOnPage;
            int nmPublicationOnPage = nmPublication % countPublicationOnPage;

            tempLink = _link + nmPage + "?json=true";
            joGeneral = new JSONObject(getString(tempLink));

            _description = joGeneral.getJSONArray("result").getJSONObject(nmPublicationOnPage).getString("description");
            _url = joGeneral.getJSONArray("result").getJSONObject(nmPublicationOnPage).getString("gifURL");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (EmptyCategoryException e) {
            _description = "";
            _url = "";
        }
    }

    public void loadPublication() {
        try {
            if (_link.equals("")) {
                throw new ErrorLinkException("Empty link");
            }

            Thread thread = new Thread(this);
            thread.start();
            if (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (ErrorLinkException e) {
            e.printStackTrace();
        }
    }

    private String getString(String tempLink) throws IOException {
        StringBuilder strJson = new StringBuilder();
        URL url = new URL(tempLink);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String data = "";

        while ((data = reader.readLine()) != null) {
            strJson.append(data).append("\n");
        }
        conn.disconnect();
        return strJson.toString();
    }
}




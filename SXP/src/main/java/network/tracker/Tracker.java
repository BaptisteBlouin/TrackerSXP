package network.tracker;

import java.io.*;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * class qui permet l'echange d'adresse ip
 */
public class Tracker {
    private String ressourcesFolder = "src/main/ressources/";
    private String ipServeur = ressourcesFolder + "ipserveur.txt";
    private String ipClient = ressourcesFolder + "ips.txt";

    /**
     * methode qui éxecute les requetes d'echange d'adresse ip
     * @return un tableau de String contenant les adresses ip des autres utilisateur
     */
    public String [] requete(){
        String[] ips = new String[0];
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in2 = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in2.readLine();

            FileInputStream file = new FileInputStream(ipServeur);
            BufferedReader buff = new BufferedReader(new InputStreamReader(file));
            String ipServeur;
            boolean goodUrl = false;
            String result = "";
            while ((ipServeur = buff.readLine()) != null && goodUrl == false ) {//essaye de se connecter a un serveur opérationnel
                try {

                    String urlRequest = "http://" + ipServeur + ":3000/request/" + ip;
                    System.out.println(urlRequest);
                    URL url = new URL(urlRequest);
                    URLConnection connection = url.openConnection();
                    connection.setReadTimeout(5000);// Timeout de connection au serveur apres lequel le serveur est consideré comme non opérationnel
                    connection.setConnectTimeout(5000);
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        result += inputLine;

                    in.close();
                    goodUrl = true;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (SocketTimeoutException ex) {
                    System.out.println(ex.toString());
                    goodUrl = false;
                }
            }

            if (goodUrl == true) {//si on a reussi a ce connecter a un serveur on met a jour la liste des adresses ip en local
                BufferedWriter out;
                out = new BufferedWriter(new FileWriter(ipClient));
                out.write("");
                out.close();
                out = new BufferedWriter(new FileWriter(ipClient, true));
                for (int i = 0; i < ips.length; i++) {
                    out.write(ips[i] + "\n");
                }
            } else {
                file = new FileInputStream(ipClient);//si les connections au serveurs ont echouées alors on lit dans le fichier local
                buff = new BufferedReader(new InputStreamReader(file));
                String line;
                while ((line = buff.readLine()) != null) {
                    result += line + "\n";
                }
            }
            ips = result.split(";");
            if(result.length() > 0){
                for (int i = 0; i < ips.length; i++) {
                    ips[i] = "tcp://" + ips[i] + ":9800";
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ips;
    }
}

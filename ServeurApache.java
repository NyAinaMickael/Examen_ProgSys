package sock;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.*;
public class ServeurApache{
    private static int port;
    private static String repertoire;
    private static int stop=0;
    private static boolean handlePHP;
    private static int THREAD_POOL_SIZE;//limitation du nombre de threads a utiliser pour des soucis de performance
    public static void start()
    {
        initVars();
        handleRequests();
    }
    public static void initVars() {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("config.conf")) {
            config.load(fis);
            // Lire les valeurs du fichier de configuration
            port = Integer.parseInt(config.getProperty("port", "8080")); // Par défaut : 8080
            repertoire = config.getProperty("repertoire", "Pages");    // Par défaut : Pages
            handlePHP = Boolean.valueOf(config.getProperty("handlePHP", "true"));    // Par défaut : Pages
            THREAD_POOL_SIZE=Integer.parseInt(config.getProperty("max_number_of_connections", "10"))+1;//apiana ray pour le controle du serveur
            System.out.println("Configuration chargée :");
            System.out.println("\tPort : " + port);
            System.out.println("\tRépertoire : " + repertoire);
        } catch (FileNotFoundException e) {
            System.err.println("Fichier config.conf introuvable dans : " + new File("config.conf").getAbsolutePath());
            port = 8080;
            repertoire = "Pages";
            THREAD_POOL_SIZE=10;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erreur lors de la lecture de la configuration : " + e.getMessage());
            System.out.println("Utilisation des valeurs par défaut.");
            port = 8080;
            repertoire = "Pages";
            THREAD_POOL_SIZE=10;
        }
    }
    public static int getConfigPort()
    {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("config.conf")) {
            config.load(fis);
            // Lire les valeurs du fichier de configuration
            return Integer.parseInt(config.getProperty("port", "8080")); // Par défaut : 8080

        } catch (Exception e) {
            return 8080;
        }
    }
    public static void setConfigPort(int port)
    {
        //alaina lay fichier de configuration File
        //anaovana bufferedReader
        //jerena ny contenu any raha misy port
        //raha tss dia amoronana ligne vaovao misy port ftsn
        //sinon tadiavina ilay ligne misy anle port dia 
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("config.conf")) 
        {
            config.load(fis);
            // Lire les valeurs du fichier de configuration
            
            
        } catch (Exception e) {
            return;
        }
        config.setProperty("port", ""+port);
        try(FileOutputStream fos=new FileOutputStream("config.conf"))
        {
            config.store(fos,"");
        } catch (Exception e) {
            return;
        }
    }
    public static void setHandlePHP(boolean handle)
    {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("config.conf")) 
        {
            config.load(fis);
            // Lire les valeurs du fichier de configuration
            
        } catch (Exception e) {
            return;
        }
        config.setProperty("handlePHP", ""+handle);
        try(FileOutputStream fos=new FileOutputStream("config.conf"))
        {
            config.store(fos,"");
        } catch (Exception e) {
            return;
        }
    }

    public static void setRepertoire(String directory)
    {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("config.conf");
        ) 
        {
            config.load(fis);
            // Lire les valeurs du fichier de configuration
            
        } catch (Exception e) {
            return;
        }
        
        config.setProperty("repertoire", directory);

        try(FileOutputStream fos=new FileOutputStream("config.conf"))
        {
            config.store(fos,"");
        } catch (Exception e) {
            return;
        }
    }
    public static int getPort(){
        return port;
    }

    public static void handleRequests() 
    {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        try (ServerSocket serveurSocket = new ServerSocket(port)) {
            System.out.println("Serveur démarré sur le port " + port);
            Logger.log("Serveur démarré sur le port " + port);
            while (stop<1) {
                System.out.println("En attente de connexion...");
                if(!serveurSocket.isClosed())
                {
                    Socket socketClient = serveurSocket.accept();
                    System.out.println("Connexion acceptée de " + socketClient.getInetAddress());
                    Logger.log("Connexion acceptée de " + socketClient.getInetAddress());
                    if(threadPool.getActiveCount()<THREAD_POOL_SIZE)//Pour permettre de garder un thread pour le serveur!
                    {
                        threadPool.execute(()->handleClient(serveurSocket,socketClient,threadPool));// Communication avec le client
                    }
                }
            }
            //serveurSocket.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
    public static void handleClient(ServerSocket serveurSocket ,Socket socketClient,ExecutorService threadPool)
    {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        OutputStream out = socketClient.getOutputStream()) {
        String line;
        StringBuilder content = new StringBuilder();
        int stop=0;
        // Lire les en-têtes HTTP
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if(line.equals("stop"))
            {
                System.out.println("Arret du serveur");
                Logger.log("Serveur ouvert sur le port "+port+" ferme");
                stop=1;
                break;
            }
            // Ajout des lignes d'en-tête au contenu
            content.append(line).append("\r\n");
            System.out.println("En-tête reçu : " + line);
        }
        if (stop == 1) {
            System.out.println("Server stopped");
            threadPool.shutdownNow();
            serveurSocket.close();
            threadPool=(ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        }
        System.out.println("En-têtes complètes reçues :\r\n" + content.toString());
        // Si le contenu a un corps (par exemple POST), le traiter ici
        if (content.toString().contains("Content-Length")) {
            int contentLength = Integer.parseInt(content.toString().split("Content-Length:")[1].split("\n")[0].trim());
            char[] bodyBuffer = new char[contentLength];
            in.read(bodyBuffer, 0, contentLength);
            //en gros ,cette ligne lit n caracteres du reste de la requete http(en l'ocurrence le corps) pour le mettre progessivement dans notre char[]
            String body = new String(bodyBuffer);
            content.append(body);
            System.out.println("Corps de la requête :\r\n" + body);
        }
            // Traiter la requête (envoi de la réponse)
            ServeurApache.receiveRequest(out, content.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socketClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Connexion fermée avec le client.");
        }   
    }
    public static String getContentTypeByExtension(String filename)
    {
        String ext=Outils.getExtension(filename);
        HashMap<String,String> data=new HashMap<>() ;
        data.put("txt", "text/plain");
        data.put("html", "text/html");
        data.put("php", "text/html");
        data.put("htm", "text/html");
        data.put("css", "text/css");
        data.put("csv", "text/csv");
        data.put("xml", "application/xml");
        data.put("json", "application/json");
        data.put("jpg", "image/jpeg");
        data.put("jpeg", "image/jpeg");
        data.put("png", "image/png");
        data.put("gif", "image/gif");
        data.put("bmp", "image/bmp");
        data.put("svg", "image/svg+xml");
        data.put("webp", "image/webp");
        data.put("mp3", "audio/mpeg");
        data.put("wav", "audio/wav");
        data.put("ogg", "audio/ogg");
        data.put("mp4", "video/mp4");
        data.put("webm", "video/webm");
        data.put("ogg", "video/ogg");
        data.put("pdf", "application/pdf");
        data.put("zip", "application/zip");
        data.put("tar", "application/x-tar");
        data.put("gzip", "application/gzip");
        data.put("exe", "application/octet-stream");
        data.put("woff", "font/woff");
        data.put("woff2", "font/woff2");
        data.put("ttf", "font/ttf");
        data.put("otf", "font/otf");
        data.put("xls", "application/vnd.ms-excel");
        data.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        data.put("ppt", "application/vnd.ms-powerpoint");
        data.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        data.put("doc", "application/msword");
        data.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        data.put("rar", "application/vnd.rar");
        data.put("class", "application/java-class");
        data.put("js", "application/javascript");
        data.put("mjs", "application/javascript");
        data.put("md", "text/markdown");
        data.put("yml", "application/x-yaml");
        data.put("yaml", "application/x-yaml");
        

        return data.get(ext);
    }
    public static String hasIndex(String dir)//ilaina eto ilay header hi-identifiena ny extension sy ny methode 
    {
        System.out.println("Voici la fonction qui se charge de trouver un probable index");
        System.out.println(dir);
        File pages=new File(dir);
        //en supposant qu'il n'y ait qu'un index
        File[] files = pages.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
                // Si le fichier est trouvé, le retourner
                if (file.isFile() && file.getName().startsWith("index")) 
                {
                    System.out.println("Nahita index."+file.getName().substring(file.getName().lastIndexOf(".")+1));
                    return file.getName().substring(file.getName().lastIndexOf(".")+1);
                }
            }
        }
        return "";
        //angalana ny Content-Type
    }

    public static Object findFile(String nomFichier)//chemin sans Pages/ fotsiny ity
    {
        System.out.println("Repertoire racine:"+repertoire);
        //le nomFichier ici contiendra eventuellement une requete apres ?
        String fullquery=nomFichier;
        String post=null;
        System.out.println("Infos:"+nomFichier.substring(nomFichier.lastIndexOf("?")+1));
        String query=nomFichier.substring(nomFichier.lastIndexOf("?")+1).split("POST:")[0];
        if(nomFichier.substring(nomFichier.lastIndexOf("?")+1).split("POST:").length>1)
        {
            post=nomFichier.substring(nomFichier.lastIndexOf("?")+1).split("POST:")[1];
        }
        System.out.println("Post:"+post);
        System.out.println("Query:"+query);
        if(fullquery.contains("?"))
        {
            nomFichier=fullquery.substring(1,nomFichier.lastIndexOf("?"));
        }
        else{
            nomFichier=fullquery.substring(1);
        }
        System.out.println("Nom fichier:"+nomFichier);
        File testFichier=new File(repertoire+"/"+nomFichier);
        System.out.println(repertoire+"/"+nomFichier);
        if("".equals(nomFichier))
        {
            System.out.println("hasIndex:"+hasIndex(repertoire+"/"+nomFichier));
            if(!hasIndex(repertoire+"/"+nomFichier).equals(""))
            {
                return findFile("/index."+hasIndex(repertoire+"/"+nomFichier));
            }
            return printAvailaibleFilesAndFolders("");
        }
        else if(testFichier.isDirectory())
        {
            System.out.println("La ressource demandee est un dossier");
            System.out.println("hasIndex:"+hasIndex(repertoire+"/"+nomFichier));
            if(!hasIndex(repertoire+"/"+nomFichier).equals(""))
            {
                String ext=hasIndex(repertoire+"/"+nomFichier);
                System.out.println("Fichier a chercher:/"+nomFichier+"/index."+ext);
                return findFile("/"+nomFichier+"/index."+ext);
            }
            //andramo tadiavina ny index
            //sinon
            return printAvailaibleFilesAndFolders(nomFichier);
        }
        String chemin="";
        //mitady ny repertoire misy anle fichier ahafahana manao recherche
        if(nomFichier.contains("/"))
        {
            chemin=nomFichier.substring(0,nomFichier.lastIndexOf("/"));
        }
        System.out.println("Chemin complet:"+repertoire+"/"+chemin);//affichage ftsn
        //mitady ny nom final an'ilay fichier sans le chemin qui y mene
        nomFichier=nomFichier.substring(nomFichier.lastIndexOf("/")+1);
        File pages=new File(repertoire+"/"+chemin);
        if (pages.isDirectory()) 
        {
            // Liste tous les fichiers du dossier
            File[] files = pages.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Si le fichier est trouvé, le retourner
                    if (file.isFile() && file.getName().equals(nomFichier)) {
                        try{
                            if(file.getName().endsWith("php") && handlePHP){//andramo compilena raha fichier php ary raha mahazaka php ilay serveur
                                return compilePHP(file,query,post);
                            }
                            else//sinon andramo vakiana fotsiny ny ao anatiny
                            {
                                return Files.readString(file.toPath());
                            }
                        }catch(Exception e)//sinon andramo sode binaire lay izy/lay fichier tadavina
                        {
                            try{
                                return Files.readAllBytes(file.toPath());
                            }
                            catch(Exception e2)
                            {
                                Logger.error("Error reading file: " + file.getName() + ", " + e2.getMessage());
                                System.out.println("Tsy avotra intsony");
                            }
                            System.out.println("Nisy blem lay fichier");   
                            return null;
                        }
                    }
                }
            }
            
            System.out.println("Aucun fichier dans le serveur");
            return null;
        }
        else 
        {
            System.out.println("Adinonlisany ny namorona anle dossier racine");
            return null;
        }

    }
    public static String printAvailaibleFilesAndFolders(String location)//a faire passer en bytes plus tard
    {
        location=repertoire+"/"+location;
        File folder=new File(location);
        File[] files = folder.listFiles();
        String script="";
        script+="<html>";
        if (files != null) {
            script+="<ul>\n";
            for (File file : files) {
                // Si le fichier est trouvé, le retourner
                String chemin=file.toPath().toString();
                System.out.println("chemin av:"+chemin);
                chemin=chemin.substring(chemin.indexOf("/")+1);
                System.out.println("chemin ap:"+chemin);
                if (file.isFile()) {
                    script+="\t<li>File:<a href=\""+chemin+"\">"+file.getName()+"</a></li>\n";
                }
                else if(file.isDirectory())
                {
                    script+="\t<li>Directory:<a href=\""+chemin+"\">"+file.getName()+"</a></li>\n";
                }
            }
            script+="</ul>\n";
        script+="</html>";
            return script;
        }
        else{
            return "";
        }
    }
    // public static String compilePHP(File file) throws IOException//tokony
    // { 
    //     ProcessBuilder processBuilder = new ProcessBuilder("php", file.getAbsolutePath());
    //     Process process = processBuilder.start();
    //     try (BufferedReader phpOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
    //         StringBuilder responseBody = new StringBuilder();
    //         String line;
    //         while ((line = phpOutput.readLine()) != null) {
    //             responseBody.append(line).append("\n");
    //         }
    //         System.out.println(responseBody.toString());
    //         return responseBody.toString();
    //     }
    // }
    // public static String compilePHP(File file,String query) throws IOException//tokony
    // { 
    //     ProcessBuilder processBuilder = new ProcessBuilder("php", file.getAbsolutePath());
    //     if(query!=null)
    //     {
    //         processBuilder.environment().put("QUERY_STRING", query);
    //     }
    //     //processBuilder.environment().put("CONTENT_LENGTH", file.toString().length());
    //     Process process = processBuilder.start();
    //     try (BufferedReader phpOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
    //         StringBuilder responseBody = new StringBuilder();
    //         String line;
    //         while ((line = phpOutput.readLine()) != null) {
    //             responseBody.append(line).append("\n");
    //         }
    //         System.out.println(responseBody.toString());
    //         return responseBody.toString();
    //     }
    // }

    public static String compilePHP(File file,String query,String post) throws IOException,InterruptedException //tokony
    { 
        //initialisation utile pour permettre l'usage de $_GET et $_POST
        String iniPOST_GET="<?php if (php_sapi_name() === 'cli' && getenv(\"QUERY_STRING\")) {parse_str(getenv(\"QUERY_STRING\"), $_GET);}if (php_sapi_name() === 'cli' && getenv(\"REQUEST_METHOD\") === 'POST') {$postData = stream_get_contents(STDIN);if ($postData) {parse_str($postData, $_POST);}}?>";
        String fileContent=Files.readString(file.toPath());
        PrintWriter p=new PrintWriter(file);
        if(!fileContent.contains(iniPOST_GET))
        {
            p.write(iniPOST_GET+"\n"+fileContent);
        }
        else{
            p.write(fileContent);
        }
        p.close();
        System.out.println("Voici la fonction qui se charge des post et des get en mm temps");
        System.out.println("Post:"+post);
        System.out.println("Get:"+query);
        ProcessBuilder processBuilder = new ProcessBuilder("php", file.getAbsolutePath());
        if(query!=null)
        {
            processBuilder.environment().put("QUERY_STRING", query);
        }
        if(post!=null)
        {
            processBuilder.environment().put("REQUEST_METHOD", "POST");
            processBuilder.environment().put("CONTENT_LENGTH", ""+post.length());
            processBuilder.environment().put("CONTENT_TYPE", "application/x-www-form-urlencoded");
        }
        else{
            processBuilder.environment().put("REQUEST_METHOD", "GET");
        }
        Process process = processBuilder.start();
        if(post!=null)
        {
            try
            {
                OutputStream processInput=process.getOutputStream();
                processInput.write(post.getBytes("UTF-8"));
                processInput.flush();
                processInput.close();
            }catch(Exception eee)
            {
                System.out.println("\t\tNISY ERREUR");
                eee.printStackTrace();
            }
        }
        System.out.println("Filecontent:"+fileContent);
        //p.write(fileContent);
        //process.waitFor();
        try (BufferedReader phpOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = phpOutput.readLine()) != null) {
                responseBody.append(line).append("\n");
            }
            System.out.println(responseBody.toString());
            return responseBody.toString();
        }
        
    }

    public static void receiveRequest(OutputStream out,String contentHTTP)
    { 
        //Alaintsika ilay outputstream azo avy amle client ahafahana mamerina directement ilay valin'ilay requete any aminy 
        if(!contentHTTP.equals(""))
        {
            String[] lignes=contentHTTP.split("\n");
            String request=lignes[0];//
            String[] composants=request.split(" ");
            String method=composants[0];
            String path=composants[1];
            String post=null;
            if(method.equals("POST"))
            {
                
                post=lignes[lignes.length-1];//prends a derniere ligne du body passe par handleRequest
                //etape de concatenation des donnees de post avec la ressource demandee pour eviter d'augmenter les parametres
                if(path.contains("?"))
                {
                    path+="POST:"+post;
                }
                else{
                    path+="?POST:"+post;
                }
            }
            String regex="(.*?)(?:\\?(.*?))?(?:#(.*?))?$";
                 //exemple:index.php?id=3#ok
                 //ce regex est concu pour recuperer les trois infos dans la ressource demandee au serveur
            Pattern pattern=Pattern.compile(regex);
            Matcher matcher=pattern.matcher(path);
            String ressource="";
            String query="";
            String fragments="";
            if(matcher.find())//recherche de correspondance entre le regex et la ressource demandee
            {
                ressource=matcher.group(1);
                query=matcher.group(2);
                fragments=matcher.group(3);
            }
            //Verification hoe nety daholo ve
            System.out.println("Method:"+method);
            System.out.println("Ressource:"+ressource);
            System.out.println("Query:"+query);
            System.out.println("Fragments:"+fragments);
            if(findFile(ressource)!=null)
            {
                sendResponse(out,200, "OK", getContentTypeByExtension(ressource),findFile(path),"keep-alive");
                Logger.log("Response sent for file:"+ressource);
            }
            else{
                sendResponse(out,404, "Not Found" , "text/html","<h1>Error 404 :Not Found</h1>","keep-alive");
                Logger.error("Error 404:File '"+ressource+"' not found");
            }
        }

        //mi-identifie ny methode
        //mi-identifie ny ressource demandee
        //mi-identifie ny Content Type en fonction de ce qu'on obtient :getMimeType()
        //rehefa ok daholo dia sendena ny reponse :sendResponse(); 

    }
    public static void sendResponse(OutputStream out,int status,String message,String contentType,Object body,String connexion)
    {
    
    String script="HTTP/1.1 "+status+" "+message+"\n";
    script+="Server: Apache-Lite/0.1\n";
    script+="Content-Type: "+contentType+"\n";
    if(body instanceof String)
    {
        script+="Content-Length: "+body.toString().length() +"\n";
    }
    else if(body instanceof byte[])
    {
        script+="Content-Length: "+((byte[]) body).length +"\n";
    }
    script+="Connection: "+connexion+"\n";
    script+="\n";
    System.out.println(script);
    try{
        out.write(script.getBytes());
        if(body instanceof byte[])
            out.write((byte[]) body);
        else if(body instanceof String)
            out.write(((String)body).getBytes());
    }
    catch(IOException e)
    {
        e.printStackTrace();
    }
        //return script;
        //atao eto ny syntaxe ilaina rehetra anaovana reponse complete 
    }

    
    public static void main(String[] args)
    {
        //int port = 8080;
        ServeurApache sA=new ServeurApache();
        
    }
}

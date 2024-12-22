package sock;
public class Outils {
    public static String getExtension(String filename)
    {
        return filename.substring(filename.lastIndexOf(".")+1);
    }    
}

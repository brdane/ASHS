import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.net.InetAddress;

//Describes as much as possible about the client connecting to our server.
class Session
{
    private String hostname = "n/a";
    private String http_version = "n/a";
    private String platform = "n/a";
    private String user_agent = "n/a";
    private String browser_name = "n/a";
    private String browser_version = "n/a";
    private String accepted_content  = "n/a";
    private String ned_exchange = "n/a";
    private String accepted_lang = "n/a";
    private String fetch_site = "n/a";
    private String fetch_mode = "n/a";
    private String fetch_dest = "n/a";
    private String fetch_user = "n/a";
    private String full_url = "n/a";
    private String accepted_encode = "n/a";
    private String client_ip = "n/a";
    private String connection = "n/a";
    private String fetch_url = "n/a";

    private boolean bMobile = false;

    public Session(){}

    public Session(String[] dump)
    {
        for (String s : dump)
        {
            if (s.startsWith("GET")) {
                String[] part = s.split(" ");

                if (part.length == 3) {
                    fetch_url = part[1];
                    http_version = part[2];
                }
            }
            String[] parts = s.split(": ");

            if (parts.length == 2) {
                if (parts[0].contains("sec-ch-ua-platform")) {
                    platform = parts[1].replaceAll("\"", "");
                }

                if (parts[0].contains("User-Agent")) {
                    user_agent = parts[1];
                }

                if (parts[0].contains("Accept")) {
                    accepted_content = parts[1];
                }

                if (parts[0].contains("ned-exchange")) {
                    ned_exchange = parts[1];
                }

                if (parts[0].contains("sec-ch-ua")) {
                    browser_name = parts[1].replaceAll("\"", "");
                }

                if (parts[0].contains("sec-ch-ua-mobile")) {
                    bMobile = parts[1].equals("?1");
                }

                if (parts[0].contains("sec-ch-ua-platform")) {
                    platform = parts[1].replaceAll("\"", "");
                }

                if (parts[0].contains("Accept-Encoding")) {
                    accepted_encode = parts[1];
                }

                if (parts[0].contains("Accept-Language")) {
                    accepted_lang = parts[1];
                }

                if (parts[0].contains("X-Forwarded-For")) {
                    client_ip = parts[1];

                    try {
                        InetAddress inBoi = InetAddress.getByName(client_ip);
                        hostname = inBoi.getHostName();
                    } catch (Exception _) {
                    }
                }

                if (parts[0].contains("Connection")) {
                    connection = parts[1];
                }

                if (parts[0].contains("Sec-Fetch-Site")) {
                    fetch_site = parts[1];
                }

                if (parts[0].contains("Sec-Fetch-Mode")) {
                    fetch_mode = parts[1];
                }

                if (parts[0].contains("Sec-Fetch-User")) {
                    fetch_user = parts[1];
                }

                if (parts[0].contains("Sec-Fetch-Dest")) {
                    fetch_dest = parts[1];
                }

                if (parts[0].contains("Host")) {
                    full_url = parts[1].concat(fetch_url);
                }
            }
        }
    }

    public String getSessionInfo()
    {
        String result = "Session Info:\n-----------------------------------\n";

        result = result.concat("HTTP Version: ").concat(getHTTPVersion()).concat("\n");
        result = result.concat("User-Agent: ").concat(getUserAgent()).concat("\n");
        result = result.concat("Accepted Content: ").concat(getAcceptedContent()).concat("\n");
        result = result.concat("Ned Exchange: ").concat(getNedExchange()).concat("\n");
        result = result.concat("Browser Info: ").concat(getBrowserInfo()).concat("\n");
        result = result.concat("Mobile Device: ").concat(isMobileDevice() ? "Yes" : "No").concat("\n");
        result = result.concat("Accepted Encoding: ").concat(getAcceptedEncoding()).concat("\n");
        result = result.concat("Full URL: ").concat(getFullURL()).concat("\n");
        result = result.concat("Fetch URL: ").concat(getFetchURL()).concat("\n");
        result = result.concat("Client's IP Address: ").concat(getClientIPAddress()).concat("\n").concat("\n");
        result = result.concat("Keep Connection Alive: ").concat(getKeepAliveConnection() ? "Yes" : "No").concat("\n");
        result = result.concat("Accepted Langauge: ").concat(getAcceptLanguage()).concat("\n");
        result = result.concat("Sec Fetch Site: ").concat(getSecFetchSite()).concat("\n");
        result = result.concat("Sec Fetch Mode: ").concat(getSecFetchMode()).concat("\n");
        result = result.concat("Sec Fetch User: ").concat(getSecFetchUser()).concat("\n");
        result = result.concat("Sec Fetch Dest: ").concat(getSecFetchDest()).concat("\n");
        result = result.concat("Platform: ").concat(getPlatform()).concat("\n\n");
        return result;
    }

    public String getHostName()
    {
        return hostname;
    }

    public String getHTTPVersion()
    {
        return http_version;
    }

    public String getUserAgent()
    {
        return user_agent;
    }

    public String getAcceptedContent()
    {
        return accepted_content;
    }

    public String getNedExchange()
    {
        return ned_exchange;
    }

    public String getBrowserInfo()
    {
        return browser_name;
    }

    public boolean isMobileDevice()
    {
        return bMobile;
    }

    public String getAcceptedEncoding()
    {
        return accepted_encode;
    }

    public String getFetchURL()
    {
        return fetch_url;
    }

    public String getFullURL()
    {
        return full_url;
    }

    public String getClientIPAddress()
    {
        return client_ip;
    }

    public boolean getKeepAliveConnection()
    {
        return (connection.equals("Keep-Alive"));
    }

    public String getAcceptLanguage()
    {
        return accepted_lang;
    }

    public String getSecFetchSite()
    {
        return fetch_site;
    }

    public String getSecFetchMode()
    {
        return fetch_mode;
    }

    public String getSecFetchUser()
    {
        return fetch_user;
    }

    public String getSecFetchDest()
    {
        return fetch_dest;
    }
    public String getPlatform()
    {
        return platform;
    }

}

public class SimpleHTTPServer
{
    public static ServerSocket socket;
    public static Socket connection;
    public static OutputStream output;
    static String request_method = "GET";
    public String grabbing = "";
    public static String lastIP = "";
    public static Session session;

    //a simple counter that increments everytime something is being requested by the client, this includes picture, icons and .js files.
    long inquiry_num = 0;

    enum contentType
    {
        HTML, HTMLUTF8, XHTML, TextJavaScript, JavaScript, ECMAScript,
        CSS, XML, TextXML, SVG, JSON, WebManifest, RSS, AtomXML,
        XHTMLandXML, WebP, JPEG, PNG, GIF, BMP, MPEG, OGG, WAV, MP4,
        WEBM, WOFF, WOFF2, TTF, OTF, ApplicationWOFF, ApplicationWOFF2,
        Text, Binary
    }

    static class labelPair
    {
        String label, tag = "";

        public labelPair(String newLabel, String newTag)
        {
            label = newLabel;
            tag = newTag;
        }
    }

    public final String[] listDirectory(String dir, boolean bListFiles, boolean bListFolders)
    {
        File file = new File(dir);

        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name)
            {
                File f = new File(current, name);

                if ( (bListFolders) && (f.isDirectory()) )
                {
                    return true;
                }

                if ( (bListFiles) && (!f.isDirectory()) )
                {
                    return true;
                }

                return false;
            }
        });

        return directories;
    }



    public final String sha256(final String base)
    {
        try
        {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++)
            {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }

        catch(Exception _) {}

        return "";
    }

    private static String convertToHex(byte[] raw) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < raw.length; i++) {
            sb.append(Integer.toString((raw[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public final String sha512(String passwordToHash)
    {
        MessageDigest sha = null;
        byte[] hash = null;
        try
        {
            sha = MessageDigest.getInstance("SHA-512");
            hash = sha.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        assert hash != null;
        return convertToHex(hash);
    }

    public final void sendBinaryResponse(byte[] data, String contentTypeString)
    {
        String responseHeader = """
        HTTP/1.1 200 OK
        Content-Type: %s
        Content-Length: %d

        """.formatted(contentTypeString, data.length);

        try {
            // Send headers
            output.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            // Send binary data
            output.write(data);
            output.flush(); // ensure all data is sent
        } catch (IOException e) {
            log("Failed to send binary response: " + e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                log("Failed to close connection");
            }
        }
    }

    public final String getProperContentType(contentType inType)
    {
        if (inType == contentType.HTML)
            return "text/html";

        if (inType == contentType.XHTML)
            return "application/xhtml+xml";

        if (inType == contentType.HTMLUTF8)
            return "text/html;charset=UTF-8";

        if (inType == contentType.JavaScript)
            return "application/javascript";

        if (inType == contentType.TextJavaScript)
            return "text/javascript";

        if (inType == contentType.CSS)
            return "text/css";

        if (inType == contentType.XML)
            return "application/xml";

        if (inType == contentType.TextXML)
            return "text/xml";

        if (inType == contentType.SVG)
            return "image/svg+xml";

        if (inType == contentType.JSON)
            return "application/json";

        if (inType == contentType.WebManifest)
            return "application/manifest+json";

        if (inType == contentType.RSS)
            return "application/rss+xml";

        if (inType == contentType.AtomXML)
            return "application/atom+xml";

        if (inType == contentType.XHTMLandXML)
            return "application/xhtml+xml";

        if (inType == contentType.WebP)
            return "image/webp";

        if (inType == contentType.ECMAScript)
            return "application/ecmascript";

        if (inType == contentType.JPEG)
            return "image/jpeg";

        if (inType == contentType.PNG)
            return "image/png";

        if (inType == contentType.GIF)
            return "image/gif";

        if (inType == contentType.BMP)
            return "image/bmp";

        if (inType == contentType.MP4)
            return "video/mp4";

        if (inType == contentType.WEBM)
            return "video/webm";

        if (inType == contentType.WOFF)
            return "font/woff";

        if (inType == contentType.WOFF2)
            return "font/woff2";

        if (inType == contentType.TTF)
            return "font/ttf";

        if (inType == contentType.OTF)
            return "font/otf";

        if (inType == contentType.ApplicationWOFF)
            return "application/font-woff";

        if (inType == contentType.ApplicationWOFF2)
            return "font/woff2";

        if (inType == contentType.Text)
            return "text/plain";

        if (inType == contentType.Binary)
            return "application/octet-stream";


        return "text/plain";
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss.SSS]").format(new Date());
    }

    public static void log(String msg)
    {
        System.out.println(getCurrentTimeStamp() + "[" + ConsoleColors.GREEN + session.getHostName() + " " + ConsoleColors.RESET + "(" + ConsoleColors.YELLOW + session.getClientIPAddress() + ConsoleColors.RESET + ") " + msg);
    }

    public static void logCritical(String msg)
    {
        msg = getCurrentTimeStamp().concat("%s[%s(%s)] %s%s").formatted(ConsoleColors.RED, session.getHostName(), session.getClientIPAddress(), msg, ConsoleColors.RESET);
        System.out.println(msg);
    }

    public static final void StartServer(int port, String successMsg, String failMsg, boolean bPrintStackOnFail)
    {
        session = new Session();

        try
        {
            socket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            if (bPrintStackOnFail)
            {
                e.printStackTrace();
            }

            if (failMsg.isEmpty())
            {
                log("Could not open Socket");
            }
            else
            {
                log(failMsg);
            }
            return;
        }

        if (successMsg.isEmpty())
        {
            log("Server successfully launched.");
        }
        else
        {
            log(successMsg);
        }

    }

    public final void StopServer()
    {
        try
        {
            socket.close();
            socket = null;

            connection.close();
            connection = null;
        }
        catch(IOException _)
        {
            log("Could not stop server... should we be worried?");
        }
    }

    protected boolean isBanned(String inIP)
    {
        String iplist = readFile(BaseDirectory("banned.ip"));

        if (iplist.isEmpty())
        {
            return false;
        }

        String[] list = iplist.split("\n");

        if (list.length < 1)
        {
            return false;
        }

        for (int i=0; i<list.length; i++)
        {
            if (list[i].contains(inIP))
            {
                return true;
            }
        }

        return false;
    }

    protected boolean isBannedURL()
    {
        String iplist = readFile(BaseDirectory("banned.urls"));

        if (iplist.isEmpty())
        {
            return false;
        }

        String[] list = iplist.split("\n");

        if (list.length < 1)
        {
            return false;
        }

        for (int i=0; i<list.length; i++)
        {
            if (session.getFetchURL().toUpperCase().contains(list[i].toUpperCase()))
            {
                return true;
            }
        }

        return false;
    }

    protected boolean isBannedHostName()
    {
        String iplist = readFile(BaseDirectory("bannedhostnam.es"));

        if (iplist.isEmpty())
        {
            return false;
        }

        String[] list = iplist.split("\n");

        if (list.length < 1)
        {
            return false;
        }

        for (int i=0; i<list.length; i++)
        {
            if (session.getHostName().toUpperCase().contains(list[i].toUpperCase()))
            {
                return true;
            }
        }

        return false;
    }

    //This is the FIRST function is called RIGHT when a new client is requesting to connect.
    //There is a pointer to the Socket and ServerSocket. You can do whatever
    //authentication or security checks here, if you want.
    // to access these in later functions, use getConnection() and getSocket().
    //If you want the request-method, use getRequestMethod().
    public boolean PreProcess(Socket inConnection, ServerSocket inSocket)
    {
        if (isBanned(session.getClientIPAddress()))
        {
            //logCritical("\n-----------[Stopped Request due to banned IP]-----------");
            return false;
        }

        if (isBannedURL())
        {
            //logCritical("\n-----------[Stopped Request due to banned request]-----------\n".concat(session.getFetchURL()));
            return false;
        }

        if (isBannedHostName())
        {
            //logCritical("\n-----------[Stopped Request due to banned hostname]-----------\n".concat(session.getFetchURL()));
            return false;
        }

        inquiry_num++;
        return true;
    }

    public final Socket getConnection()
    {
        return connection;
    }

    public final ServerSocket getSocket()
    {
        return socket;
    }

    public final boolean newClientIncoming()
    {
        try
        {
            connection = socket.accept();
        }
        catch (IOException e)
        {
            return false;
        }
        try
        {
            output = connection.getOutputStream();
        }
        catch (IOException e)
        {
            return false;
        }

        lastIP = connection.getInetAddress().getHostAddress();
        return true;
    }

    //file or folder.
    public final boolean fileExists(String file_dir)
    {
        return new File(file_dir).exists();
    }

    public final boolean writeFile(String file_dir, String contents)
    {
        if (fileExists(file_dir))
        {
            return false;
        }
        try
        {
            FileWriter myWriter = new FileWriter(file_dir);
            myWriter.write(contents);
            myWriter.close();
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public final boolean appendFile(String file_dir, String contents)
    {
        if (file_dir.isEmpty())
        {
            return false;
        }

        if (!fileExists(file_dir))
        {
            writeFile(file_dir,"");
        }

        try
        {
            Files.write(Paths.get(file_dir), contents.getBytes(), StandardOpenOption.APPEND);
        }
        catch (IOException e)
        {
            return false;
        }
        return true;
    }


    //ASCII or UTF-8 only.
    public final String readFile(String file_dir)
    {
        try
        {
            return Files.readString(Paths.get(file_dir), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            return ""; // Return empty string if file is not found
        }
    }

    public final String unsplit(String[] in, String withThis)
    {
        if (in.length == 1)
        {
            return in[0];
        }

        String result = "";

        for (int i=0; i<in.length; i++)
        {
            result = result.concat(in[i]);

            if (i < in.length-1)
            {
                result = result.concat(withThis);
            }
        }

        return result;
    }

    public final String unsplit(byte[] in, String withThis)
    {
        if (in.length == 1)
        {
            return Integer.toHexString(in[0]);
        }

        String result = "";

        for (int i=0; i<=in.length-1; i++)
        {
            result = result.concat(Integer.toHexString(in[i]));

            if (i < in.length-2)
            {
                result = result.concat(withThis);
            }
        }

        return result;
    }

    public final String getRequestMethod()
    {
        return request_method;
    }

    public final void SendOutput(contentType type, String outText)
    {

        outText = outText.replaceAll("%20", " ");

        String response = """
                HTTP/1.1 200 OK
                Content-Type: %s
                Content-Length: %d
                
                %s""".formatted(getProperContentType(type), outText.getBytes(StandardCharsets.UTF_8).length, outText);

        try
        {
            output.write(response.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            log("Failed to send the following text: \n".concat(response));
            return;
        }
        try
        {
            connection.close();
        }
        catch (IOException e)
        {
            log("Failed to close client's socket.");
        }
    }

    public final void sendInput(ArrayList<labelPair> labels)
    {

        if (labels.isEmpty())
        {
            SendOutput(contentType.Text,"");
            return;
        }

        String formHtml = "<form>";

        for (int i=0; i<labels.size(); i++)
        {
            formHtml = formHtml.concat("""
                              <label for="%s">%s:</label><br>
                              <input type="text" id="%s" name="%s"><br>
                    """.formatted(labels.get(i).tag,labels.get(i).label,labels.get(i).tag,labels.get(i).tag));
        }

        formHtml = formHtml.concat("""
                <input type="submit" value="Submit">""").concat("<form>");

        String response = """
            HTTP/1.1 200 OK
            Content-Type: text/html; charset=UTF-8
            Content-Length: %d

            %s
            """.formatted(formHtml.getBytes(StandardCharsets.UTF_8).length, formHtml);

        try
        {
            output.write(response.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            log("Failed to send input form.");
        }
        finally
        {
            try
            {
                connection.close(); // Close after sending
            }
            catch (IOException e)
            {
                log("Failed to close connection after sending form.");
            }
        }
    }

    public void sendCSS(String file_dir)
    {
        SendOutput(contentType.CSS, readFile(file_dir));
    }

    public void sendJS(String file_dir)
    {
        SendOutput(contentType.JavaScript, readFile(file_dir));
    }

    public void sendJPEG(String file_dir)
    {
        try {
            // Read the JPEG file into a byte array
            byte[] imageBytes = Files.readAllBytes(Paths.get(file_dir));

            // Prepare HTTP response headers
            String headers = """
            HTTP/1.1 200 OK
            Content-Type: image/jpeg
            Content-Length: %d
            
            """.formatted(imageBytes.length);

            // Send headers
            output.write(headers.getBytes(StandardCharsets.UTF_8));
            // Send image bytes
            output.write(imageBytes);
            output.flush();

            // Close connection
            connection.close();
        }
        catch (IOException e)
        {
            log("Error sending JPEG: " + e.getMessage());
        }
    }

    public void sendPNG(String file_dir)
    {
        try {
            // Read the JPEG file into a byte array
            byte[] imageBytes = Files.readAllBytes(Paths.get(file_dir));

            // Prepare HTTP response headers
            String headers = """
            HTTP/1.1 200 OK
            Content-Type: image/png
            Content-Length: %d
            
            """.formatted(imageBytes.length);

            // Send headers
            output.write(headers.getBytes(StandardCharsets.UTF_8));
            // Send image bytes
            output.write(imageBytes);
            output.flush();

            // Close connection
            connection.close();
        }
        catch (IOException e)
        {
            log("Error sending PNG: " + e.getMessage());
        }
    }

    public void sendICO(String file_dir)
    {
        try {
            // Read the .ico file into a byte array
            byte[] iconBytes = Files.readAllBytes(Paths.get(file_dir));

            // Prepare HTTP response headers
            String headers = """
        HTTP/1.1 200 OK
        Content-Type: image/x-icon
        Content-Length: %d

        """.formatted(iconBytes.length);

            // Send headers
            output.write(headers.getBytes(StandardCharsets.UTF_8));
            // Send icon bytes
            output.write(iconBytes);
            output.flush();

            // Close connection
            connection.close();
        }
        catch (IOException e)
        {
            log("Error sending ICO: " + e.getMessage());
            // Handle error (e.g., send 404 or error message)
        }
    }

    public void sendGIF(String file_dir)
    {
        byte[] gifData;

        try
        {
            gifData = Files.readAllBytes(Paths.get(file_dir));
        }
        catch (Exception e)
        {
            SendOutput(contentType.Text, "Can't find GIF.");
            return;
        }

        sendBinaryResponse(gifData, getProperContentType(contentType.GIF));
    }

    public void sendFavicon(String file_dir)
    {
        try {
            byte[] iconBytes = Files.readAllBytes(Paths.get(file_dir));

            String headers = """
        HTTP/1.1 200 OK
        Content-Type: image/x-icon
        Content-Length: %d
        
        """.formatted(iconBytes.length);

            output.write(headers.getBytes(StandardCharsets.UTF_8));
            output.write(iconBytes);
            output.flush();
            connection.close();
        } catch (IOException e) {
            log("Error sending favicon: " + e.getMessage());
        }
    }

    public void sendFontTTF(String file_dir)
    {
        try {
            byte[] fontBytes = Files.readAllBytes(Paths.get(file_dir));

            String headers = """
        HTTP/1.1 200 OK
        Content-Type: font/ttf
        Content-Length: %d
        Access-Control-Allow-Origin: *
        
        """.formatted(fontBytes.length);

            output.write(headers.getBytes(StandardCharsets.UTF_8));
            output.write(fontBytes);
            output.flush();
            connection.close();
        } catch (IOException e) {
            log("Error sending TTF font: " + e.getMessage());
        }
    }

    public void sendFontWOFF(String file_dir)
    {
        try {
            byte[] fontBytes = Files.readAllBytes(Paths.get(file_dir));

            String headers = """
        HTTP/1.1 200 OK
        Content-Type: font/woff
        Content-Length: %d
        Access-Control-Allow-Origin: *
        
        """.formatted(fontBytes.length);

            output.write(headers.getBytes(StandardCharsets.UTF_8));
            output.write(fontBytes);
            output.flush();
            connection.close();
        } catch (IOException e) {
            log("Error sending WOFF font: " + e.getMessage());
        }
    }

    public void sendFontWOFF2(String file_dir)
    {
        try {
            byte[] fontBytes = Files.readAllBytes(Paths.get(file_dir));

            String headers = """
        HTTP/1.1 200 OK
        Content-Type: font/woff2
        Content-Length: %d
        Access-Control-Allow-Origin: *
        
        """.formatted(fontBytes.length);

            output.write(headers.getBytes(StandardCharsets.UTF_8));
            output.write(fontBytes);
            output.flush();
            connection.close();
        } catch (IOException e) {
            log("Error sending WOFF2 font: " + e.getMessage());
        }
    }


    public void sendFile(String file_path, String present_filename_as)
    {
        File file = new File(file_path);

        if (!file.exists() || !file.isFile())
        {
            log("Error: Attempted to send file to client, but cannot find it: \n".concat(file_path));
            SendOutput(contentType.Text, "Error: A file you are trying to get is not here.");
            return;
        }

        // Get MIME type, fallback to application/octet-stream
        String mimeType = "application/octet-stream";
        try {
            String probeType = Files.probeContentType(file.toPath());
            if (probeType != null)
            {
                mimeType = probeType;
            }
        }
        catch (IOException e)
        {
            // fallback remains
        }

        String fileName = file.getName();

        String responseHeader = """
            HTTP/1.1 200 OK
            Content-Type: %s
            Content-Length: %d
            Content-Disposition: attachment; filename="%s"
            
            """.formatted(mimeType, file.length(), (present_filename_as.isEmpty()) ? fileName : present_filename_as);

        try
        {
            // Send headers
            output.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            output.flush();

            // Send file bytes
            try (FileInputStream fis = new FileInputStream(file))
            {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1)
                {
                    output.write(buffer, 0, bytesRead);
                }
            }
            output.flush();
        } catch (IOException e) {
            log("Error sending file: " + e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                log("Failed to close connection");
            }
        }
    }

    //Overridable
    public void homePage()
    {
        SendOutput(contentType.Text, "Welcome to the home-page!");
    }


    public void processRequestForFile(String[] file)
    {
        SendOutput(contentType.Text, "We are supposed to be looking for the file: ".concat(unsplit(file,"/")));
    }

    public void processPath(String[] path)
    {
        String dir_list = "";

        for (int i = 0; i <= path.length-1; i++)
        {
            dir_list = dir_list.concat(path[i]).concat("/");
        }

        if (dir_list.equals("/"))
        {
            homePage();
        }
        else
        {
            SendOutput(contentType.Text, "You are in " + dir_list);
        }
    }

    public void processParameterOfPath(String[] path, String[] parameter, String[] value)
    {
        String pList = "";

        for (int i=0; i<parameter.length; i++)
        {
            pList = pList.concat("Parameter: '%s'<br>Value: '%s'<br><br>".formatted(parameter[i], value[i]));
        }

        SendOutput(contentType.HTML, pList);
    }

    //Overridable End

    public final int paramIndex(String[] in, String p_name)
    {
        int result = -1;

        for (int i=0; i<in.length; i++)
        {
            if (in[i].equals(p_name))
            {
                result = i;
            }
        }

        return result;
    }

    public final void processNextRequest() throws Exception
    {
        if (!newClientIncoming())
        {
            return;
        }

        BufferedReader inStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String full = "";
        String line;

        // in a try/catch block due to occasional connection reset, which throws an IOException.
        try
        {
            while ((line = inStream.readLine()) != null)
            {
                full = full.concat(line).concat("\n");

                if (line.isEmpty())
                {
                    break;
                }
            }
        }
        catch (Exception e)
        {
            return;
        }

        session = new Session(full.split("\n"));

        if (!PreProcess(connection, socket))
        {
            try
            {
                connection.close();
            }
            catch (Exception e)
            {
            }
            return;
        }

        String parameter_line = session.getFetchURL().substring(1);
        boolean bHandled = false;

        if (!parameter_line.isEmpty())
        {
            String[] paths = parameter_line.split("/");
            String[] cpl = new String[0];
            String[] paramLines = new String[0];
            String foundParams = "", foundValues = "";
            String current_path = "";
            boolean bParamsFound = false;
            boolean bMultipleParams = false;

            if (paths.length > 0)
            {
                for (String path : paths)
                {
                    current_path = current_path.concat(path).concat("/");

                    if (path.contains("?"))
                    {
                        String[] paramString = path.split("\\?");

                        if (paramString[1].contains("&"))
                        {
                            paramLines = paramString[1].split("&");
                            bMultipleParams = true;
                        }

                        if (bMultipleParams)
                        {
                            for (String paramLine : paramLines)
                            {
                                if (paramLine.contains("="))
                                {
                                    cpl = current_path.split("\\?");
                                    cpl = cpl[0].split("/");
                                    String[] paramInfo = paramLine.split("=");

                                    if (paramInfo.length > 1)
                                    {
                                        foundParams = foundParams.concat(paramInfo[0]).concat(",");
                                        foundValues = foundValues.concat(paramInfo[1]).concat(",");
                                        bParamsFound = true;
                                        bHandled = true;
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (paramString[1].contains("="))
                            {
                                cpl = current_path.split("\\?");
                                cpl = cpl[0].split("/");
                                String[] paramInfo = paramString[1].split("=");

                                if (paramInfo.length == 2)
                                {
                                    if ((!paramInfo[0].isEmpty()) && (!paramInfo[1].isEmpty()))
                                    {
                                        foundParams = foundParams.concat(paramInfo[0]).concat(",");
                                        foundValues = foundValues.concat(paramInfo[1]).concat(",");
                                        bParamsFound = true;
                                        bHandled = true;
                                    }
                                }
                            }
                        }
                        if (bParamsFound)
                        {
                            processParameterOfPath(cpl, foundParams.split(","), foundValues.split(","));
                        }
                    }
                }
                if (!bHandled)
                {
                    if ((paths[paths.length - 1].contains(".")) && (getRequestMethod().equals("GET"))) {
                        processRequestForFile(paths);
                    }
                    else
                    {
                        processPath(paths);
                    }
                }
            }
        }
        else
        {
            homePage();
        }
    }

    // Returns your base directory for your server. Change this to where ever your server
    //files are located.
    public static String BaseDirectory(String addOnDir)
    {
        return "C:\\Users\\Administrator\\Desktop\\web_servers\\example\\".concat(addOnDir);
        //return "/home/brenden/Documents/web_servers/example/".concat(addOnDir);
    }


}

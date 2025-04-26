import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class SimpleHTTPServer
{
    public static ServerSocket socket;
    public static Socket connection;
    public static OutputStream output;
    static String request_method = "GET";
    public String grabbing = "";

    enum contentType
    {
        HTML, HTMLUTF8, XHTML, TextJavaScript, JavaScript, ECMAScript,
        CSS, XML, TextXML, SVG, JSON, WebManifest, RSS, AtomXML,
        XHTMLandXML, WebP, JPEG, PNG, GIF, BMP, MPEG, OGG, WAV, MP4,
        WEBM, WOFF, WOFF2, TTF, OTF, ApplicationWOFF, ApplicationWOFF2,
        Text, Binary
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

        catch(Exception _)
        {
        }

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

    public static void log(String msg)
    {
        System.out.println(msg);
    }

    public static final void StartServer(int port, String successMsg, String failMsg, boolean bPrintStackOnFail)
    {
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

    public void PreProcess(Socket inConnection, ServerSocket inSocket)
    {
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

        PreProcess(connection, socket);
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
            log("Failed to update output text.");
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

        if ((dir_list.equals("index/") || (dir_list.equals("index.html/"))))
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
        String line = "";

        try
        {
            line = inStream.readLine();
        }
        catch (IOException e)
        {
            SendOutput(contentType.Text,"Error: Invalid parsing URL.");
            return;
        }

        line = line.trim();

        String[] lines = line.split(" ");
        request_method = lines[0];

        if (lines.length < 2)
        {
            SendOutput(contentType.Text,"Error: Invalid request.");
            return;
        }

        String parameter_line = lines[1].substring(1);
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
                                    if ((!paramInfo[0].isEmpty()) && (!paramInfo[1].isEmpty())) {
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
                        if ( (paths[paths.length-1].contains(".")) && (getRequestMethod().equals("GET")) )
                        {
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
    }
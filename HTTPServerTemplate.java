/*
* An empty template for your HTTP Server.
*
* Everything in this template is what you can override to
* fit your needs. Everything has comments.
*
* Brenden Dane (brdane) 2025
* brdane@gmail.com
 */

import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServerTemplate extends SimpleHTTPServer
{

    @Override
    //This is the FIRST function is called RIGHT when a new client is requesting to connect.
    //There is a pointer to the Socket and ServerSocket. You can do whatever
    //authentication or security checks here, if you want.
    // to access these in later functions, use getConnection() and getSocket().
    //If you want the request-method, use getRequestMethod().
    // Calling super. is HIGHLY Recommended because it processes your blacklist entries.
    //If you wnat to take that into your own hands, remove it.
    public boolean PreProcess(Socket inConnection, ServerSocket inSocket)
    {
        super.PreProcess(inConnection, inSocket);

    }

    //This function is called when the URL requested is either "example.com",
    //"example.com/index", "example.com/index.htm" or "example.com/index.html"
    @Override
    public void homePage()
    {
        //This function will send a response to the client/client's browser and closes
        //the connection. Specify the content-type in the first parameter, followed by the
        //actual contents in the second parameter.
        SendOutput(contentType.Text, "Welcome to The Website.");
    }

    //This function is called when the URL requested is something like:
    //"example.com/path/to/the/thing.ext", or in other words, when
    //the client is requesting a file. You use this for every file
    //including those requested in the web-page's HTML... so every
    //JS, CSS, picture, video, audio file, etc.
    @Override
    public void processRequestForFile(String[] file)
    {
        super.processRequestForFile(file);
    }

    //This function is called when the URL requested is something like:
    //"example.com/path/to/the/thing", or in other words, when the
    //the client is requesting to go to a certain section/path of the
    //website.
    @Override
    public void processPath(String[] path)
    {
        super.processPath(path);

    }

    //This function is called when the URL requested is something like:
    //"example.com/path/to/the/thing?value1=45.2344&value2=textgoeshere"
    //or in other words, when the client is requesting a path/file and
    //they specify parameters and values. All parameters are loaded in the
    //'parameter' String variable. each index is a parameter. The 'value'
    //array holds the values, in the same corresponding indexes as 'parameter'.
    @Override
    public void processParameterOfPath(String[] path, String[] parameter, String[] value)
    {
        super.processParameterOfPath(path, parameter, value);
    }

    //Your main function only needs to look like this. The only two things you need to change:
    public static void main(String... args) throws Exception
    {
        //This starts listening on a port of your choice. If it launches, the second parameter will print to your log.
        //If your server fails to start, the third parameter will print to long. In the event that it fails to start,
        //you have an option to include the trace stack leading up to the failure by setting the last parameter to true.
        StartServer(8080,"HTTP Template up and running.", "HTTP Template didn't start!", false);

        while(true){new HTTPServerTemplate().processNextRequest();} //Change the class here to yours,
                                          //otherwise your overridden functions
                                         //will not be called. Have this function
                                        //loop forever.
    }

}

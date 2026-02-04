# ASHS - Absolute Simple HTTP(S) Server

This is exactly what it is called. A simple, single-class, Java-based template for you to make a backend server for your website. Its parent class has all functions pre-made to handle different URL requests. These include home-page, sub-pages, file requests, and parameters/values. This is as barebones as it can get for handling requests to your URL. There are no baked-in parent-directory navigation, wp-content, timymce, SQL, or anything that a lot of web-hosting providers include in their services, which is good because this means there is no chance for hackers taking advantage of vulnerabilities and backdoors in these platforms. You seriously have 100% control on who connects, what their hostname is, IP, what they are requesting, and make your own decisions on what to do with every connect attempt. Be sure to disable every port on your firewall that is not 443 so nobody can backdoor into your system that way.

In addition to the main class you will find, 'SimpleHTTPServer', from which you will extend to create your own, I also included a very simple example template class extending it that is plug-and-play so you can see how it works.


## How to use:
### 1) Copy the "HTTPServerTemplate.java" file.
### 2) Rename it, along with the class inside the copied file, to whatever your new class.
### 3) There you will see empty, overridden functions for you to put your code into.
### 4) In the main() function of your new class, you will see a line that looks like the following:
```
while(true){new HTTPServerTemplate().processNextRequest();} 
```
This function will always run to process the next connection attempt. Replace 'HTTPServerTemplate' with your new class name. 
Additonally, you may see on the StartServer() function above it, which looks like this:
```
StartServer(8080,"HTTP Template up and running.", "HTTP Template didn't start!", false);
```
The first parameter is the port that your web-server will be launched on. By default, it is 8080. You may change this to
whatever tickles your fancy. The first parameter is a custom message that prints on your console to let you know that the
server has launched, the third parameter is a message that prints when something went wrong and it failed. For the fourth
parameter, if it is 'true' then the stack trace will print on the console if the server failed to start, so you can debug
the issue. If the parameter is 'false' it'll simply give you the fail message in the third parameter.


### 5) Scroll down to the bottom of your new server class and find the function BaseDirectory(). It should look like this:
```
public static String BaseDirectory(String addOnDir)
    {
        return "C:\\Users\\Administrator\\Desktop\\web_servers\\example\\".concat(addOnDir);
        //return "/home/brenden/Documents/web_servers/example/".concat(addOnDir);
    }

```
Change the string that it returns to the directory that your server's folder or appropriate folder that holds all of your
server's stuff. This way, you can easily refer to it.

6) Inside of your specified base directory, create the following files:
```
banned.ip
banned.urls
bannedhostnam.es
```
These are configureable files, let's refer to these as blacklist files.  

banned.ip - Add entries to this list for preventing clients with certain IP addresses from connecting to your server.
Currently does not accept wildcards but instead do something like "1.1.1" for blocking all IP addresses that start with
those three digits. One entry per line.

bannedhostnam.es - Add entries to this list for preventing clients with certain hostnames from connecting to your server.
This is EXTREMELY handy for preventing web-crawlers and data-scrapers from connecting to your server. You can make 
vague entries to block hostnames that contain a certain keyword, like 'microsoft' or 'aws'.  One entry per line.

banned.urls - Add entries to this list for preventing clients from connecting to your server by specifying certain URLs.
This is EXTREMELY handy for preventing web-crawlers and data-scrapers from connecting to your server. You can make 
vague entries to block URLs that contain certain keywords, like '/logininformation'.  One entry per line.

When your server prevents connections, nothing will print on your console to let you know. This is intentional because 
there are ALWAYS web-crawlers and data-scrapers that are constantly sending pre-programmed requests to random IP addresses
until any of them bite and return sensitive information. You will notice this when you first launch your server with nothing
in your blacklist files. The requests will honestly be harmless since this is a bare-bones server, and nothing they request in
the URL will return anything to them. BUT, it is always good practice to add the entries you don't want to see in your log, 
especially since it will flood your log daily if they are not blocked.

### 6) Compile and run from Terminal or Command Prompt.

## Output to expect in the console when running:

When connection attempts are made to your server, they will look similar to this:
```
[04-02-2026 11:19:17.538][example-hostname.co.uk (192.104.34.34) Attempt to access path: 'search-page'
```
Each attempt will be logged as: date, time, hostname, IP address, and the action that is being taken to serve the client's request.


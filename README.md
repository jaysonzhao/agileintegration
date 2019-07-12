This demo will build a multi module Maven project that deploys to Standalone Fuse 6.2.1

The project is organized like a repository should be on a real engagement. For this reason there are some empty or unused folders, but they should all have READMEs to explain what they would be used for.

It is assumed you already have Java 1.8 and Maven 3+ installed.

Start by downloading Fuse 6.2.1 from access.redhat.com and extracting it to your local machine. Go to the $FUSE_HOME/etc directory and edit the users.properties file and uncomment the last line which will enable the 'admin' user. Then go to the $FUSE_HOME/bin directory and run "./fuse.sh" (or "fuse.bat" if you are on Windows). The Fuse script will start Fuse in the foreground and give you access to the Karaf shell. In a production environment you would run the Start script instead which runs Fuse in the background, and use the client script to get access to the Karaf shell.

In another terminal window (since Fuse is in the foreground and will stop if you exit the shell) we should build this code. Start by going to the 'parent' directory and running:
~~~
mvn clean install
~~~
This will build the pom file and install it in your local .m2 directory.

Once the parent pom is built, you can go to the 'core' directory and run
~~~
mvn clean install
~~~
This will build all of the projects below it including:

 * Artifacts - which has all of the files necessary for starting the web services.
 * Services  - which has the NextGate test server and an OSGi service connection pool to the AMQ broker
 * Inbound   - which runs a REST server and sends the message to a broker
 * Xlate     - which translates the message from the Person format to the NextGate format
 * Outbound  - which reads the final message from the broker and sends it to the NextGate sever
 * Features  - which builds a features file for easy installation of the entire project on Fuse

Once the build is complete go back to the Fuse Karaf shell and run the command:
~~~
features:addurl mvn:com.customer.app/customer-features/1.0-SNAPSHOT/xml/features
~~~
This command tells Fuse where in the Maven repo to find the feature that we created. Then to install the customer app features file run the command:
~~~
features:install customer-features
~~~
This will install all of the core bundles and their dependencies. To see all of the bundles that were installed and if they started properly you can run the command:
~~~
osgi:list
~~~
If you're interested in what this feature file looks like, check it out at core/customer-features/target/feature/feature.xml.

You should be able to go to 127.0.0.1:8181 to log into the Fuse web console. Use the username/password combo "admin/admin". Under the 'Camel' tab you should see three Contexts. One from inbound, one from xlate, and one from outbound. If you expand them at look under the Routes folder you should see one or two under each. Click on them and then select "Route Diagram" from the right pane and see what they look like. You should see a visual representation of all of the routes that we deployed. You can select "Source" to see the Camel route in xml. Once we start sending messages you can go back to the route diagram and you'll see a number on each component for each message that successfully passes through them. This can be very useful for making sure your routes are working and seeing where messages are being routed.

Now go to https://www.soapui.org/downloads/soapui.html and download the OpenSource version of SoapUI. Once it is open hit "Control+I" or go to File->Import Project to import a new project. A project is included in the 'tooling' directory of this repo. It should load a project called "Customer App Demo". Open all of the levels until you get to the lowest 'Match' request and run it. It should do a POST to http://127.0.0.1:9098/cxf/demos/match with the SimplePatient.xml request. You should see an ESBResponse that contains a random BusinessKey to verify the request is unique and a Published field that is always true and a Comment that tells you if the given name matches the required value of 'First'. So your result should be "MATCH". You can now either change the given name or open the 'No Match' request and run it. You should now see the result "NO MATCH". You can play with the other fields, but they will have no affect on the response, only in the logs, which we will look at now.

Return to the Fuse web console and go to the 'Logs' tab. Each request should generate 12 logs from different sources as the message moves through Fuse. The first log should be from "handleRest" and should say "The REST method is: match". The last log should be from "sendToNextGate" and will start with "Response from CXF". Obviously the response will be different based on the return code but for a Match it should be "Xlatefalsefalse1". If you click on any of the logs you will receive way more details about them. Click on the last log and you'll see the fully formatted XML response from the NextGate service.

We can also go to the 'ActiveMQ' tab and see the "customer" and "nextgate" queues that have been created. Click on one of them and look at the number of messages in the 'Enqueue count' and 'Dequeue count'. They should be equal to each other and equal to the total number of messages you have sent. From here we can actually do something fun. Select the "nextgate" queue and go to the 'Send' tab in the right section. Change the payload format to XML and paste in the contents of core/outbound/src/test/data/soapText.xml then hit "Send message". Go back to the logs and you should see 7 logs starting with "Connector vm://amq started" and ending with "Response from CXF: XMLfalsefalse1". This basically let us hijack the set of routes and just place a message in the broker for the Outbound service. Obivously we don't get a response here other than logs, but this can be very useful for testing when you don't have all of the peices.

If you'd like to tweak the code a little more, check out the readme in the Inbound service on how to make it an asynchronous call instead of a synchronous one! In the Karaf shell run the command:
~~~
dev:watch *
~~~
That way anytime you make a change to a SNAPSHOT project it will automatically be reloaded by Fuse. This is a dangerous command and should only be used in a DEV environment. But it will make it so you don't have to uninstall and re-install the feautures and bundles every time you make a code change in this demo.

Do take a moment to go through all of the code, there isn't very much of it. Especially pay attention to the src/main/resources/META-INF/spring/camelContext.xml files in the Inbound, Xlate, and Outbound projects.

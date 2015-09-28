# FSDemoApp
FSDemoApp stands for *Forward Secrecy Demo Application*.  It was written to support a presentation that I original gave for Worldpay at a [Java MeetUp](http://www.meetup.com/pt/Worldpay-Developers-engineers-and-testers/events/224421037/) event.
The application consists of:

 1. Java code.
 2. Batch files (shell scripts coming soon).
 3. Pre-built resources.
 4. Documentation.

If you want to build and run the app then you'll need a [Java 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and [Maven 3.x](https://maven.apache.org/download.cgi) installed and configured correctly.
Once you've got these then simple run `build.bat` or `mvn dependency:copy-dependencies package` then the following batch files can be used:

Item     | Description
-------- | ---
`build.bat` | Invokes the Maven build to compile all the source code and copy dependencies in to the `target` directory.
`createKeyStore.bat`|Creates a private key store and public trust (certificate) store using the Java SDK `keytool` utility.  There's a pre-built version of this as part of the Git repository, but you can use this to rebuild to edit the files to roll your own.
`keytool_priv.bat`| Performs a `keytool` operation on the private key store.  Used by `addKeyType.bat`.  Using this means filenames and passwords are consistent.
`keytool_pub.bat`| Performs a `keytool` operation on the the public key store.  Used by `addKeyType.bat`.  Using this means filenames and passwords are consistent.
`addKeyType.bat`|Adds a private key and certificate combination to the private key store and public trust store.  Allows the caller to fiddle with the key generation algorithm used.  Used by `createKeyStore.bat`.
`server.bat`|Runs the server application.  See the comments in the [batch file](https://github.com/andybrodie/fsdemoapp/blob/master/server.bat) or [Javadoc](https://github.com/andybrodie/fsdemoapp/blob/master/src/main/java/com/worldpay/fsdemoapp/programs/Server.java) for the Server class for more information.
`client.bat`|Runs the client application.  See the comments in the [batch file](https://github.com/andybrodie/fsdemoapp/blob/master/client.bat) or [Javadoc](https://github.com/andybrodie/fsdemoapp/blob/master/src/main/java/com/worldpay/fsdemoapp/programs/Client.java) for the Server class for more information.
There are other executable programs.  To find them, check out the contents of the [com.worldpay.fsdemoapp.programs](https://github.com/andybrodie/fsdemoapp/tree/master/src/main/java/com/worldpay/fsdemoapp/programs) package.
Use a classpath like this: `target\SecurityTest-0.0.1-SNAPSHOT.jar;target\dependency\slf4j-api-1.6.1.jar;target\dependency\slf4j-simple-1.6.1.jar` relative to the root of the repository.

For example:
`C:\Users\andy\Projects\FSDemoApp>java -cp target\SecurityTest-0.0.1-SNAPSHOT.jar;target\dependency\slf4j-api-1.6.1.jar;target\dependency\slf4j-simple-1.6.1.jar com.worldpay.fsdemoapp.programs.CombinedServerAndClient`

## Other Resources
Some of the material available in the repository may be found in the [docs](https://github.com/andybrodie/fsdemoapp/tree/master/docs) directory:
Filename|Description
--------|-----------
`Cipher Suite Test Results.xlsx`|Results of running all the different ciphers available in TLS with different types of keys, recording whether they worked or not.  Java source in [TryAllCiphers.java](https://github.com/andybrodie/fsdemoapp/blob/master/src/main/java/com/worldpay/fsdemoapp/programs/TryAllCiphers.java).
`Forward Secrecy in Java.docx`|Word document that explains Forward Secrecy from first principles, avoiding maths wherever possible.
`Forward Secrecy in Java.pptx`|PowerPoint presentation explaining forward secrecy with no maths at all!|
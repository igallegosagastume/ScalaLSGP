# ScalaLSGP
Scala Latin Square Generation Package


Compile instructions:

1) You can compile the project with maven with the following command:

mvn eclipse:eclipse

2) Go to eclipse and refresh the project

3) Then you have to change the generated .classpath file to correct the source folders to include .scala files (chage "java" to "scala" in the following entries):

<classpathentry kind="src" path="src/main/scala" including="**/*.scala"/>
<classpathentry kind="src" path="src/test/scala" output="target/test-classes" including="**/*.scala"/>

4) Replace the ".project" file by "dotproject.txt" file to add scala nature and builder

5) Refresh, clean project and compile from eclipse.

6) You can run the GraphicJacoMattTest (as a ScalaTest Suite or JUnit test) to see the Jacobson & Matthews' algorithm in action, and the OpenGL graphics in a new Window.


Write an email to bluemontag@gmail.com if you have any questions.



If you liked the project you can support it by donating:

<a href="https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=ignaciogallego%40gmail%2ecom&lc=AR&item_name=Blue%20Montag%20Software&item_number=github%2dbutton&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted">
<img src="https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif" border="0" alt="PayPal - The safer, easier way to pay online!">
</a>

Blue Montag Software

package factory;

import base.Configuration;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class RSACrackerFactory {
    public static Object build() {
        if (Checker.isComponentAccepted("components/rsa/rsa_cracker/rsa_cracker.jar")) {

            Object RSACrackerPort = null;

            try {
                URL[] urls = {new File(Configuration.instance.pathToRSACrackerJavaArchive).toURI().toURL()};
                URLClassLoader urlClassLoader = new URLClassLoader(urls, RSACrackerFactory.class.getClassLoader());
                Class RSACracker = Class.forName("RSACracker", true, urlClassLoader);

                Object RSACrackerInstance = RSACracker.getMethod("getInstance").invoke(null);

                RSACrackerPort = RSACracker.getDeclaredField("port").get(RSACrackerInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return RSACrackerPort;
        }else {
            return null;
        }
    }
}


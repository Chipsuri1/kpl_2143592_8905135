package factory;

import base.Configuration;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class RSAFactory {
    public static Object build() {
//        if (Checker.isComponentAccepted("components/rsa/rsa/rsa.jar")) {

            Object RSAPort = null;

            try {
                URL[] urls = {new File(Configuration.instance.pathToRSAJavaArchive).toURI().toURL()};
                URLClassLoader urlClassLoader = new URLClassLoader(urls, RSAFactory.class.getClassLoader());
                Class RSAClass = Class.forName("RSA", true, urlClassLoader);

                Object RSAInstance = RSAClass.getMethod("getInstance").invoke(null);

                RSAPort = RSAClass.getDeclaredField("port").get(RSAInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return RSAPort;
//        }else {
//            return null;
//        }
    }
}


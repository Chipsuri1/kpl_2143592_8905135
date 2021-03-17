package factory;

import base.Configuration;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ShiftFactory {
    public static Object build() {
        if (Checker.isComponentAccepted("components/shift/shift/shift.jar")) {

            Object ShiftPort = null;

            try {
                URL[] urls = {new File(Configuration.instance.pathToShiftJavaArchive).toURI().toURL()};
                URLClassLoader urlClassLoader = new URLClassLoader(urls, ShiftFactory.class.getClassLoader());
                Class ShiftClass = Class.forName("Shift", true, urlClassLoader);

                Object ShiftInstance = ShiftClass.getMethod("getInstance").invoke(null);

                ShiftPort = ShiftClass.getDeclaredField("port").get(ShiftInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ShiftPort;
        }else {
            return null;
        }
    }
}


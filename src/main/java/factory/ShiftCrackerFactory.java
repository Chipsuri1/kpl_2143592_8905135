package factory;

import base.Configuration;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ShiftCrackerFactory {
    public static Object build() {
        Object ShiftCrackerPort = null;

        try {
            URL[] urls = {new File(Configuration.instance.pathToShiftCrackerJavaArchive).toURI().toURL()};
            URLClassLoader urlClassLoader = new URLClassLoader(urls, ShiftCrackerFactory.class.getClassLoader());
            Class ShiftCrackerClass = Class.forName("ShiftCracker", true, urlClassLoader);

            Object ShiftCrackerInstance = ShiftCrackerClass.getMethod("getInstance").invoke(null);

            ShiftCrackerPort = ShiftCrackerClass.getDeclaredField("port").get(ShiftCrackerInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ShiftCrackerPort;
    }
}


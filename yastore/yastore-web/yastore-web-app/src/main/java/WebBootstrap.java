/*
 * Copyright 2014 Denis Morozov.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author dmorozov
 */
public class WebBootstrap {

    private static final String WEB_XML = "WEB-INF/web.xml";
    private static final String PATH_JAR_PREFIX = "jar:";
    private static final String PATH_FILE_PREFIX = "file:";
    private static final String MAIN_CLASS = "App-Class";

    private URL getResource(String aResource) {
        return Thread.currentThread().getContextClassLoader()
                .getResource(aResource);
    }

    private String getShadedWarUrl() {
        String _urlStr = getResource(WEB_XML).toString();
        _urlStr = _urlStr.substring(0, _urlStr.length() - WEB_XML.length() - 2);

        if (_urlStr.startsWith(PATH_JAR_PREFIX)) {
            _urlStr = _urlStr.substring(PATH_JAR_PREFIX.length());
        }
        if (_urlStr.startsWith(PATH_FILE_PREFIX)) {
            _urlStr = _urlStr.substring(PATH_FILE_PREFIX.length());
        }
        return _urlStr;
    }

    public String getMainClassName(String jarPath) throws IOException {
        URL u = new URL("jar", "", "file:" + jarPath + "!/");
        JarURLConnection uc = (JarURLConnection) u.openConnection();
        Attributes attr = uc.getMainAttributes();
        return attr != null ? attr.getValue(new Attributes.Name(MAIN_CLASS)) : null;
    }

    private String getBasePath(String warPath) {
        int pos = warPath.lastIndexOf("/");
        if (pos < 0) {
            pos = warPath.lastIndexOf("\\");
        }

        return pos > 0 ? warPath.substring(0, pos) : warPath;
    }

    private String getLibName(String warPath) {
        int pos = warPath.lastIndexOf("/");
        if (pos < 0) {
            pos = warPath.lastIndexOf("\\");
        }

        return pos > 0 ? warPath.substring(pos + 1) : warPath;
    }

    private String getLibPath(String basePath, String libName,
            InputStream libContent) throws IOException {

        String outputFile = basePath + "/libs/" + libName;
        byte[] buffer = new byte[8 * 1024];

        try (OutputStream output = new FileOutputStream(outputFile)) {
            int bytesRead;
            while ((bytesRead = libContent.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } finally {
            libContent.close();
        }

        return "file:" + outputFile;
    }

    private void clearLibFolder(final String basePath) {
        File baseFolder = new File(basePath, "libs");
        if (!baseFolder.exists()) {
            baseFolder.mkdirs();
        }
        for (File file : baseFolder.listFiles()) {
            file.delete();
        }
    }

    public void start(String[] args) {
        try {
            final String warPath = getShadedWarUrl();
            final String basePath = getBasePath(warPath);
            final String mainClass = getMainClassName(warPath);
            clearLibFolder(basePath);
            
            List<URL> list = new ArrayList<>();
            final ZipFile zipFile = new ZipFile(warPath);
            try {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    String entryName = entry.getName();

                    // if the entry is not directory and matches relative file then extract it
                    // FIXME: how to avoid double loading for application libs? like one has persistence declaration?
                    if (!entry.isDirectory() && entryName.startsWith("WEB-INF/bootstrap")) {
                        String libPath = getLibPath(basePath, getLibName(entryName), zipFile.getInputStream(entry));
                        URL webLibUrl = new URL(libPath);
                        list.add(webLibUrl);
                    }
                }
            } finally {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    System.err.println("Unable to locate war file: " + warPath);
                    System.exit(-1);
                }
            }

            try (URLClassLoader classLoader = new URLClassLoader(list.toArray(new URL[]{}))) {
                final Class<?> clazz = classLoader.loadClass(mainClass);

                Thread.currentThread().setContextClassLoader(classLoader);

                final Method method = clazz.getMethod("main", String[].class);
                method.invoke(null, new Object[]{args});

            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                System.exit(-1);
            }

        } catch (IOException e) {
            System.err.println("Application terminated with errors");
            System.exit(-1);
        }

        System.out.println("I'm alive!");
    }

    public static void main(String[] args) {
        WebBootstrap bootstrap = new WebBootstrap();
        bootstrap.start(args);
    }
}

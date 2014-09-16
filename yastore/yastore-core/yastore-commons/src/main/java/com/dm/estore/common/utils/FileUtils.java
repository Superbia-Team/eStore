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
package com.dm.estore.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dm.estore.common.config.Cfg;

/**
 *
 * @author dmorozov
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    /**
     * Default encoding.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Platform dependant line separator symbol.
     */
    private static final String NEW_LINE_SEP = System.getProperty("line.separator");

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Private constructor to prevent instantiation.
     */
    private FileUtils() {
    }

    /**
     *
     * @param inputStream Input stream to read data from.
     * @param outputName Target file name to write data in.
     */
    public static void saveFile(InputStream inputStream, String outputName) {
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(outputName), DEFAULT_ENCODING);
            final BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine = inputReader.readLine();
            while (inputLine != null) {
                out.append(inputLine);
                inputLine = inputReader.readLine();
                if (inputLine != null) {
                    out.append(NEW_LINE_SEP);
                }
            }
            inputReader.close();
        } catch (Exception e) {
            LOG.error("Unable to write file: " + outputName, e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    LOG.trace("Unable to close file: " + outputName, e);
                }
            }
        }
    }

    /**
     * Ensure that all folders in path are exist.
     *
     * @param filePath Path to file or folder
     */
    public static void ensurePathExist(String filePath) {
        File configFile = new File(filePath);
        if (!configFile.exists()) {
            if (!configFile.isDirectory()) {
                final String folderPath = filePath.substring(0, filePath.lastIndexOf(File.separator));
                configFile = new File(folderPath);
            }

            if (!configFile.exists()) {
                configFile.mkdirs();
            }
        }
    }

    /**
     * Helper visitor interface for text stream processing.
     *
     * @author dmorozov
     */
    public interface TextStreamProcessor {

        /**
         * Process and return processed text block.
         *
         * @param inputText Input text block
         * @return Processed text block
         */
        String process(String inputText);
    }

    /**
     * Save text file from classpath to file.
     *
     * @param resource Classpath text resource
     * @param outputFile Output file
     * @param processor Optional line processor
     * @param encoding Text encoding
     * @throws IOException Exception
     */
    public static void saveTextFileFromClassPath(String resource,
            String outputFile, String encoding, TextStreamProcessor processor) throws IOException {

        final InputStream inputStream = FileUtils.class.getResourceAsStream(resource);
        final Scanner scanner = new Scanner(inputStream, encoding);
        final Writer out = new OutputStreamWriter(new FileOutputStream(outputFile), encoding);
        try {

            while (scanner.hasNextLine()) {
                final String nextLine = scanner.nextLine();
                out.write(processor != null ? processor.process(nextLine) : nextLine);
                out.write(NEW_LINE_SEP);
            }

        } finally {
            out.close();
            scanner.close();
        }

    }
    
    public static void createFilesFromClasspath(final String destFoder, List<String> resources, 
    		List<String> resourcesToIgnoreIfExist) {
    	
    	// create all required parent folders
        FileUtils.ensurePathExist(destFoder);
    	
    	for (String fileName : resources) {
            final String configFileName = destFoder + File.separator + fileName;
            FileUtils.ensurePathExist(configFileName);
            final File configFile = new File(configFileName);
            if (!resourcesToIgnoreIfExist.contains(fileName) || !configFile.exists()) {
                
                // re-create default configuration file
                if (configFile.exists()) {
                    configFile.delete();
                }
                
                final String classPath = File.separator + fileName;
                try {
                    final InputStream fileStream = Cfg.class.getResourceAsStream(classPath);
                    FileUtils.saveFile(fileStream, configFileName);
                } catch (Exception e) {
                    LOG.error("Unable to create default configuration.", e);
                }
            }
        }
    }
}

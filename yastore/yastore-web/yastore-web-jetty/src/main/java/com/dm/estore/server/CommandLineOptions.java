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

package com.dm.estore.server;

import com.dm.estore.common.config.Cfg;
import com.dm.estore.common.constants.CommonConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author denis
 * 
 * Additional command line options:
 * -Dconfig.resource=/dev.conf Configure Akka configuration
 */
public class CommandLineOptions {

    private static final String CMD_HELP = "help";
    private static final String CMD_PORT = "p";
    private static final String CMD_PORTSSL = "ssl";
    private static final String CMD_PROPS = "o";
    private static final String CMD_MINTHREADS = "tmin";
    private static final String CMD_MAXTHREADS = "tmax";
        
    private static final String PARAM_HOME = "home";
    private static final String PARAM_PORT = "port";
    private static final String PARAM_COUNT = "count";

    private final Map<String, String> propMap = new HashMap<>();

    private Option createOption(final String argument, final String description, final String option) {
        OptionBuilder.withArgName(argument);
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(description);
        return OptionBuilder.create(option);
    }
    
    protected Options generateOptions() {
        Options options = new Options();

        final Option help = new Option(CMD_HELP, "print this message");
        addOption(options, help, null, null);
        
        final Option cfg = new Option(Cfg.CMD_PRINT_CONFIG, "print current configuration");
        addOption(options, cfg, null, null);

        final Option props = createOption(PARAM_HOME, "home folder", CMD_PROPS);
        addOption(options, props, CMD_PROPS, CommonConstants.Cfg.CFG_HOME_FOLDER);

        // port
        final Option port = createOption(PARAM_PORT, "server port", CMD_PORT);
        addOption(options, port, CMD_PORT, CommonConstants.Server.PROP_PORT);
        // ssl port
        final Option sslport = createOption(PARAM_PORT, "server HTTPS port", CMD_PORTSSL);
        addOption(options, sslport, CMD_PORTSSL, CommonConstants.Server.PROP_PORT_SSL);
        
        // min server threads count
        Option minthreads = createOption(PARAM_COUNT, "min server HTTP threads count", CMD_MINTHREADS);
        addOption(options, minthreads, CMD_MINTHREADS, CommonConstants.Server.PROP_MIN_THREADS);
        // max server threads count		
        Option maxthreads = createOption(PARAM_COUNT, "max server HTTP threads count", CMD_MAXTHREADS);
        addOption(options, maxthreads, CMD_MAXTHREADS, CommonConstants.Server.PROP_MAX_THREADS);
        
        return options;
    }

    protected void addOption(Options options, Option option, String cmdName, String propName) {
        options.addOption(option);
        if (!StringUtils.isEmpty(cmdName) && !StringUtils.isEmpty(propName)) {
            propMap.put(cmdName, propName);
        }
    }

    public Properties parse(final String executableName, final String[] args) {

        Properties properties = null;

        Options options = generateOptions();
        CommandLineParser parser = new BasicParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            // validate that block-size has been set
            if (line.hasOption(CMD_HELP)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(executableName, options, true);
                return null;
            } else {
                properties = new Properties();

                @SuppressWarnings("unchecked")
                Iterator<Option> iter = line.iterator();
                while (iter != null && iter.hasNext()) {
                    Option o = iter.next();
                    String value = o.getValue();
		        	// if empty then set it to true to mark that it was specified
		        	if (value == null) value = Boolean.TRUE.toString();
		        	
                    if (propMap.containsKey(o.getOpt())) {
                        properties.put(propMap.get(o.getOpt()), value);
                    } else {
                        properties.put(o.getOpt(), value);
                    }
                }
            }
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Unable to parse arguments. " + exp.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(executableName, options, true);
        }

        return properties;
    }
}

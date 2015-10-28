package com.github.robyrobot.cliwe;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * CliWe a very simple command line wrapper
 * that uses Apache Commons CLI
 * @author robyrobot
 *
 */
public final class CliWe {

	private static final HelpFormatter help = new HelpFormatter();
	private static final String SCRIPT_NAME = "cliw.program.name";

	public static final String HELP_FLAG_LONG = "help";
	public static final String HELP_FLAG_SHORT = "h";

	private PrintStream stdOut = System.out;
	private PrintStream stdErr = System.err;

	private String header = "";
	private String footer = "";
	
	private Options options = new Options();
	private String defProgramName = "";
	private ArgumentManager aMgr = new ArgumentManager();

	public CliWe() {
		// add common options
		options.addOption(Option.builder(HELP_FLAG_SHORT).longOpt(HELP_FLAG_LONG).desc("print this help").build());
	}

	public CliWe(String defaultProgramName) {
		this();
		setDefaultProgramName(defaultProgramName);
	}

	public CliWe setDefaultProgramName(String programName) {
		this.defProgramName = programName;
		return this;
	}
	
	public CliWe setHeader(String txt) {
		this.header = txt;
		return this;
	}
	
	public CliWe setFooter(String txt) {
		this.footer = txt;
		return this;
	}

	/**
	 * setup the output stream
	 * 
	 * @param s
	 * @return
	 */
	public CliWe setStdOut(PrintStream s) {
		this.stdOut = s;
		return this;
	}

	/**
	 * setup the error stream
	 * 
	 * @param s
	 * @return
	 */
	public CliWe setStdErr(PrintStream s) {
		this.stdErr = s;
		return this;
	}

	private void printUsageOnError(String msg) {
		String err = "\nERROR: " + msg + "\n";
		stdErr.println(err);
		printUsage(stdErr, err);
	}

	private void printUsage(PrintStream where, String h) {
		PrintWriter pw = new PrintWriter(where, true);
		h = (h != null) ? h : header;
		pw.println("");
		help.printHelp(pw, -1, _getProgramName(), h + "\n\n", options, 2, 2, "\n" + footer, true);
	}
	
	public CliWe addMandatoryOption(String name, String longName, String argName, String desc) {
		return _addOption(name, longName, argName, desc, null, true);
	}

	public CliWe addOption(String name, String longName, String argName, String desc, String def) {
		return _addOption(name, longName, argName, desc, def, false);
	}

	public CliWe addFlag(String name, String longName, String desc) {
		return _addOption(name, longName, null, desc, null, false);
	}
	
	public void execute(String[] args, ICommandBlock block) {

		try {
			CommandLine cmd = new DefaultParser().parse(options, args);

			if (cmd.hasOption(HELP_FLAG_SHORT)) {
				printUsage(stdOut, header);
			}

			block.executeCode(aMgr.setCommandLine(cmd));
			
		} catch (ParseException e) {
			// print usage
			printUsageOnError(e.getMessage());
		}
	}

	// internal structures

	private String _getProgramName() {
		return System.getProperty(SCRIPT_NAME, this.defProgramName);
	}

	private String _descWithDefault(String desc, Object def) {
		String defPart = "";
		if (def != null) {
			defPart = String.format(" (default value: %s) ", def.toString());
		}
	
		return desc + defPart;
	}

	private static boolean _isNullOrEmpty(String s) {
		return (s == null || s.isEmpty());
	}

	private CliWe _addOption(String name, String longName, String argName, String desc, String def, boolean required) {
	
		if (options.hasOption(longName)) {
			throw new IllegalArgumentException(String.format("long name argument %s already added", longName));
		}
	
		if (options.hasOption(name)) {
			throw new IllegalArgumentException(String.format("argument name %s already added", name));
		}
	
		Option o = Option.builder(name).longOpt(longName).build();
		if (!_isNullOrEmpty(argName)) {
			o.setArgName(argName);
			o.setArgs(1);
		}
	
		if (!_isNullOrEmpty(desc)) {
			if (def != null) {
				o.setDescription(_descWithDefault(desc, def));
			} else {
				o.setDescription(desc);
			}
		}
	
		o.setRequired(required);
	
		// setup defaults
		aMgr.setDefault(name, def);
	
		// add to options
		options.addOption(o);
	
		return this;
	}

	/**
	 * interface for main block execution
	 * 
	 * @author robyrobot
	 *
	 */
	public interface ICommandBlock {

		/**
		 * execute a code block exposing an argument manager interface to deal
		 * whit the script parameters
		 * 
		 * @param am
		 */
		void executeCode(ArgumentManager am);
	}

	/**
	 * manager for argument from command line
	 * 
	 * @author robyrobot
	 *
	 */
	public class ArgumentManager {
		private Map<String, String> defaults = new HashMap<>();
		private CommandLine cmd;

		public ArgumentManager setCommandLine(CommandLine cmd) {
			this.cmd = cmd;
			return this;
		}

		void setDefault(String name, String def) {
			defaults.put(name, def);
		}

		String getDefault(String name) {
			return defaults.get(name);
		}

		public boolean hasArgument(String name) {
			return cmd.hasOption(name);
		}

		public String getArgument(String name) {
			String val = cmd.getOptionValue(name);
			if (_isNullOrEmpty(val)) {
				val = getDefault(name);
			}

			return val;
		}

		public String getArgument(String name, String def) {
			String r = getArgument(name);
			return _isNullOrEmpty(r) ? def : r;
		}
	}
}

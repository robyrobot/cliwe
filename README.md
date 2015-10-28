# CliWe
A simple command line interface wrapper for your Java console application.

# Usage
The main method of the console application will look like this:

```Java
public static void main(String[] args) {
		new CliWe("my_program_name")
			.addFlag("f", "flag", "this is a flag!")
			.addMandatoryOption("r", "required-option", "ARG", "a required option.")
			.addOption("o", "other-option", "ARG", "option not mandatory with a default value set", "VALUE")
			.execute(args, (ArgumentManager amgr) -> {
				
				if (amgr.hasArgument("f")) {
					System.out.println("yeah the 'f' flag is on!");
				}
			});
	}
```
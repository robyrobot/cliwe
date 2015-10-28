package com.github.robyrobot.test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.junit.Test;

import com.github.robyrobot.cliwe.CliWe;
import com.github.robyrobot.cliwe.CliWe.ArgumentManager;

public class TestCliWe {

	@Test
	public void test() throws FileNotFoundException {
		TestCliWe t = new TestCliWe();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream s = new PrintStream(bos);
		System.setOut(s);
		t.main(new String[]{"-f", "-r", "value"});
		assertEquals("yeah the 'f' flag is on!", bos.toString().trim());
	}
	
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

}

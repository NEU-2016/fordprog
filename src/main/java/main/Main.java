package main;

import logic.Interpreter;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {

		Interpreter i = new Interpreter();
		try {
			i.execute("test.java");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

}

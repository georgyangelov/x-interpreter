package net.gangelov.x.cli;

import net.gangelov.x.evaluator.Evaluator;
import org.apache.commons.cli.*;

import java.io.*;

public class CLI {
    public static void main(String[] args) throws IOException {
        Options options = new Options();

        options.addOption("h", "help", false, "Print this help");
        options.addOption("i", "interactive", false, "Run REPL");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("x", options);

            System.exit(1);
            return;
        }

        if (cmd.hasOption("help")) {
            formatter.printHelp("x", options);
        } else {
            Evaluator evaluator = new Evaluator();

            if (cmd.getArgList().size() > 0) {
                for (String fileName : cmd.getArgList()) {
                    new Eval(evaluator, fileName, new FileInputStream(fileName)).run();
                }
            } else if (!cmd.hasOption("interactive")) {
                new Eval(evaluator, "STDIN", System.in).run();
            }

            if (cmd.hasOption("interactive")) {
                new REPL(evaluator).run();
            }
        }
    }
}

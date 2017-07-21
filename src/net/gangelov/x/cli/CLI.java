package net.gangelov.x.cli;

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
        } else if (cmd.hasOption("interactive")) {
            new REPL().run();
        } else {
            if (cmd.getArgList().size() > 0) {
                String file = cmd.getArgList().get(0);

                new Eval(new FileInputStream(file)).run();
            } else {
                new Eval(System.in).run();
            }
        }
    }
}

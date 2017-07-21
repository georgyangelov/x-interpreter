package net.gangelov.x.cli;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.evaluator.Evaluator;
import net.gangelov.x.evaluator.XErrorException;
import net.gangelov.x.parser.Lexer;
import net.gangelov.x.parser.Parser;
import net.gangelov.x.runtime.Value;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class REPL {
    private final Evaluator evaluator = new Evaluator();

    public void run() throws IOException {
        Terminal terminal = TerminalBuilder.terminal();
        LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

        while (true) {
            String line = null;
            try {
                StringBuilder source = new StringBuilder();

                line = lineReader.readLine("x > ");

                while (true) {
                    source.append(line).append("\n");

                    if (eval(source.toString())) break;

                    line = lineReader.readLine("... ");
                }
            } catch (UserInterruptException e) {
                // Ignore...
            } catch (EndOfFileException e) {
                return;
            } catch (Lexer.LexerException e) {
                System.err.println("Lexer error: " + e.getMessage());
            } catch (Parser.ParserException e) {
                System.err.println("Parser error: " + e.getMessage());
            }
        }
    }

    private boolean eval(String source) throws IOException, Lexer.LexerException, Parser.ParserException {
        InputStream in = new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8));
        Lexer lexer = new Lexer("REPL", in);
        Parser parser = new Parser(lexer);

        try {
            List<ASTNode> nodes = new ArrayList<>();
            while (parser.hasMoreTokens()) {
                nodes.add(parser.parseNext());
            }

            if (nodes.size() > 0) {
                List<Value> results = evaluator.evaluate(nodes);

                System.out.println("=> " + results.get(results.size() - 1).inspect());
            }

            return true;
        } catch (Parser.ParserException e) {
            if (!e.isIncomplete) {
                throw e;
            }
        } catch (XErrorException e) {
            System.err.println("!> " + e.getMessage());
            return true;
        } catch (Evaluator.RuntimeError e) {
            System.err.println("Error: " + e.getMessage());
            return true;
        }

        return false;
    }
}

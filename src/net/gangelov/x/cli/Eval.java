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

public class Eval {
    private final Evaluator evaluator = new Evaluator();
    private final InputStream in;

    public Eval(InputStream in) {
        this.in = in;
    }

    public void run() throws IOException {
        Lexer lexer = new Lexer("REPL", in);
        Parser parser = new Parser(lexer);

        try {
            List<ASTNode> nodes = new ArrayList<>();
            while (parser.hasMoreTokens()) {
                nodes.add(parser.parseNext());
            }

            evaluator.evaluate(nodes);
        } catch (Lexer.LexerException e) {
            System.err.println("Lexer error: " + e.getMessage());
        } catch (Parser.ParserException e) {
            System.err.println("Parse error: " + e.getMessage());
        } catch (XErrorException e) {
            System.err.println("!> " + e.getMessage());
        } catch (Evaluator.RuntimeError e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

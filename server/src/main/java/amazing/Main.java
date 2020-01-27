/*
 * MIT License
 *
 * Copyright (c) 2020 aMAZEing-Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package amazing;

import amazing.controller.AmazeingSocketServer;
import amazing.controller.Error;
import amazing.controller.Error.ErrorFactory;
import amazing.controller.Result;
import amazing.controller.Result.ResultFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public final class Main {

    private static final Options OPTIONS = new Options();

    static {
        Main.OPTIONS.addOption(
                Option.builder(Opt.PORT.getOpt()).longOpt(Opt.PORT.getLongOpt()).hasArg(true)
                        .argName("port").type(Integer.class).build());
        Main.OPTIONS.addOption(Option.builder(null).longOpt(Opt.REMOTE.getLongOpt()).desc(
                "enables remote mode (this will ignore any shut down commands and requires manual "
                        + "termination - only use, if you know what you are doing)").build());
        Main.OPTIONS.addOption(
                Option.builder(null).longOpt(Opt.DEBUG.getLongOpt()).desc("enables debug mode").build());
        Main.OPTIONS.addOption(
                Option.builder(Opt.HELP.getOpt()).longOpt(Opt.HELP.getLongOpt()).desc("print help")
                        .build());
        Main.OPTIONS.addOption(
                Option.builder(Opt.VERSION.getOpt()).longOpt(Opt.VERSION.getLongOpt()).desc("print version")
                        .build());
        Main.OPTIONS
                .addOption(Option.builder(null).longOpt(Opt.DRY.getLongOpt()).desc("dry run").build());
    }

    @Contract(pure = true)
    private Main() {
        // empty
    }

    public static void main(@NotNull final String[] args) {
        Main.main(args.clone(), new PrintWriter(System.out, true, StandardCharsets.UTF_8));
    }

    static void main(final String[] args, final PrintWriter printWriter) {
        final CliParser cliParser = new CliParser(args);
        final Result<String, ?> result = cliParser.parse();
        if (result.isError()) {
            new HelpFormatter()
                    .printHelp(printWriter, 80, "java -jar <path>", "", Main.OPTIONS, 2, 3, "", true);
        } else if (result.ok().isPresent()) {
            printWriter.println(result.ok().get());
        } else {
            if (cliParser.getDry()) {
                printWriter.print(String
                        .format("%d,%s,%s", cliParser.getPort(), cliParser.getRemote(), cliParser.getDebug()));
            } else {
                final AmazeingSocketServer server =
                        new AmazeingSocketServer(new InetSocketAddress(cliParser.getPort()),
                                cliParser.getRemote());
                server.setReuseAddr(true);
                server.run();
            }
        }
    }

    enum Opt {
        HELP('h', "help"),
        PORT('p', "port", "8081"),
        DEBUG("debug"),
        REMOTE("remote"),
        VERSION('v', "version"),
        DRY("dry");
        private final Optional<String> defaultValue;
        Optional<String> longOpt;
        Optional<Character> opt;

        Opt(final String longOpt) {
            this.opt = Optional.empty();
            this.longOpt = Optional.ofNullable(longOpt);
            this.defaultValue = Optional.empty();
        }

        Opt(final Character opt, final String longOpt) {
            this.opt = Optional.ofNullable(opt);
            this.longOpt = Optional.ofNullable(longOpt);
            this.defaultValue = Optional.empty();
        }

        Opt(final Character opt, final String longOpt, final String defaultValue) {
            this.opt = Optional.ofNullable(opt);
            this.longOpt = Optional.ofNullable(longOpt);
            this.defaultValue = Optional.ofNullable(defaultValue);
        }

        public String getDefaultValue() {
            return this.defaultValue.orElse(null);
        }

        public String getLongOpt() {
            return this.longOpt.orElseThrow();
        }

        @SuppressFBWarnings("OI_OPTIONAL_ISSUES_USES_IMMEDIATE_EXECUTION")
        public String getOpt() {
            return this.opt.map(Objects::toString).orElse(this.longOpt.orElseThrow());
        }
    }

    static class CliParser {

        String[] arguments;
        boolean dry;
        boolean debug;
        int port;
        boolean remote;

        CliParser(final String[] arguments) {
            this.arguments = arguments;
        }

        boolean getDebug() {
            return this.debug;
        }

        int getPort() {
            return this.port;
        }

        boolean getRemote() {
            return this.remote;
        }

        boolean getDry() {
            return this.dry;
        }

        Result<String, Error> parse() {
            try {
                final CommandLineParser parser = new DefaultParser();
                final CommandLine cli = parser.parse(Main.OPTIONS, this.arguments);
                this.dry = cli.hasOption(Opt.DRY.getOpt());
                this.debug = cli.hasOption(Opt.DEBUG.getOpt());
                this.remote = cli.hasOption(Opt.REMOTE.getOpt());
                if (cli.hasOption(Opt.HELP.getOpt())) {
                    return ResultFactory.createError(ErrorFactory.createCliError());
                } else if (cli.hasOption(Opt.VERSION.getOpt())) {
                    return ResultFactory.createOk(Config.VERSION);
                } else {
                    this.port =
                            Integer.parseInt(cli.getOptionValue(Opt.PORT.getOpt(), Opt.PORT.getDefaultValue()));
                }
            } catch (final ParseException ignored) {
                return ResultFactory.createError(ErrorFactory.createCliError());
            }
            return ResultFactory.createOk();
        }
    }
}
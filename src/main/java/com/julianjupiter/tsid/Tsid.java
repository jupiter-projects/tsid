package com.julianjupiter.tsid;

import io.hypersistence.tsid.TSID;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * @author Julian Jupiter
 */
@Command(name = "tsid", mixinStandardHelpOptions = true, version = "tsid 0.0.1", description = "Generates TSID (Time-Sorted Identifier).", headerHeading = "Copyright (c) 2024 Julian Jupiter%n")
public class Tsid implements Callable<Integer> {
    private static final String TABLE = "table";
    private static final String JSON = "json";
    @Option(names = {"-f", "--from"}, description = "Long or string value of TSID.")
    private String from;
    @Option(names = {"-d", "--display"}, description = "Display format: table (default) or json")
    private String display = TABLE;
    @Option(names = {"-c", "--count"}, description = "Number of identifiers to be generated.")
    private int count = -1;

    @Override
    public Integer call() {
        return this.generate();
    }

    private int generate() {
        List<TSID> tsidList = new ArrayList<>();
        if (from != null && !from.isBlank()) {
            try {
                var tsid = this.generateFrom();
                tsidList.add(tsid);
            } catch (Exception e) {
                err.println(e.getMessage());
                return 1;
            }

            var list = IntStream.range(0, count)
                    .mapToObj(i -> TSID.Factory.getTsid())
                    .toList();
            tsidList.addAll(list);
        } else {
            count = (count < 0) ? 1 : count;
            var list = IntStream.range(0, count)
                    .mapToObj(i -> TSID.Factory.getTsid())
                    .toList();
            tsidList.addAll(list);
        }

        print(tsidList);

        return 0;
    }

    private void print(List<TSID> tsidList) {
        if (display.equalsIgnoreCase(JSON)) {
            this.json(tsidList);
        } else {
            this.table(tsidList);
        }
    }

    private TSID generateFrom() {
        TSID tsid = null;
        if (from != null && !from.isBlank()) {
            try {
                tsid = TSID.from(Long.parseLong(from));
            } catch (Exception e1) {
                tsid = TSID.from(from);
            }
        }

        return tsid;
    }

    private void json(List<TSID> tsidList) {
        if (tsidList.isEmpty()) {
            out.println("[]");
            return;
        }

        int size = tsidList.size();
        boolean multiple = size > 1;
        var template = """
                    {
                        "string": "%s",
                        "long": %d,
                        "instant": "%s"
                    }%s
                """;
        var sb = new StringBuilder();
        sb.append("[\n");
        for (var i = 0; i < size; i++) {
            var tsid = tsidList.get(i);
            var object = template.formatted(
                    tsid,
                    tsid.toLong(),
                    tsid.getInstant(),
                    multiple && (i < (size - 1)) ? "," : ""
            );
            sb.append(object);
        }
        sb.append("]");
        out.println(sb);
    }

    private void table(List<TSID> tsidList) {
        var stringColumn = "----------------------";
        var longColumn = "---------------------------";
        var instantColumn = "--------------------------------";
        var stringFormatter = "+%-22s+%-27s+%-27s+%n";
        out.printf(stringFormatter, stringColumn, longColumn, instantColumn);
        out.printf("| %-20s | %-25s | %-30s |%n", "string", "long", "instant");
        out.printf(stringFormatter, stringColumn, longColumn, instantColumn);
        if (!tsidList.isEmpty()) {
            tsidList.forEach(tsid -> out.printf("| %-20s | %-25d | %-30s |%n", tsid, tsid.toLong(), tsid.getInstant()));
            out.printf(stringFormatter, stringColumn, longColumn, instantColumn);
        }
    }

    public static Tsid create() {
        return new Tsid();
    }

    public static void main(String... args) {
        int exitStatus = new CommandLine(create()).execute(args);
        System.exit(exitStatus);
    }

    @Override
    public String toString() {
        return UUID.randomUUID().toString();
    }
}

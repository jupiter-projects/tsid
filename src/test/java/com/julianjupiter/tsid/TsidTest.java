package com.julianjupiter.tsid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TsidTest {
    private final CommandLine cmd;

    TsidTest() {
        var tsid = Tsid.create();
        this.cmd = new CommandLine(tsid);
    }

    @Test
    void testWithoutOptions() {
        int exitStatus = cmd.execute();
        assertEquals(0, exitStatus);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-f=567651578926096470", "--from=0FR5M39Z18R2P"})
    void testWithValidOption_from(String from) {
        int exitStatus = cmd.execute(from);
        assertEquals(0, exitStatus);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-f=567651578926096470.5", "-f=0FR5M39Z18R2P."})
    void testWithInvalidOption_from(String from) {
        int exitStatus = cmd.execute(from);
        assertEquals(1, exitStatus);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-c=3", "--count=2"})
    void testWithOption_count(String count) {
        int exitStatus = cmd.execute(count);
        assertEquals(0, exitStatus);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-d=json", "--display=json", "-d=table", "--display=table"})
    void testWithOption_display(String display) {
        int exitStatus = cmd.execute(display);
        assertEquals(0, exitStatus);
    }

    @Test
    void testWithOptions_from_and_display() {
        int exitStatus = cmd.execute("-f=567651578926096470", "-d=json");
        assertEquals(0, exitStatus);
    }

    @Test
    void testWithOptions_count_and_displayJson() {
        int exitStatus = cmd.execute("-c=3", "-d=json");
        assertEquals(0, exitStatus);
    }

    @Test
    void testWithOptions_count_and_displayTable() {
        int exitStatus = cmd.execute("-c=3", "-d=table");
        assertEquals(0, exitStatus);
    }

    @Test
    void testWithOptions_from_count_and_display() {
        int exitStatus = cmd.execute("-f=567651578926096470", "-c=2", "-d=json");
        assertEquals(0, exitStatus);
    }
}

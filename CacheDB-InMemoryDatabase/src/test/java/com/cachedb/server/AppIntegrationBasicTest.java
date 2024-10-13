package com.cachedb.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppIntegrationBasicTest {

    private void assertAppOutput(String input, String expectedOutput) {
        // Set up input stream
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Set up output stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Run the main method
        App.main(new String[]{});

        // Capture and process the output
        String actualOutput = out.toString().replaceAll("\\r\\n", "\n").trim();
        String formattedExpectedOutput = expectedOutput.replaceAll("\\r\\n", "\n").trim();

        if (!formattedExpectedOutput.equals(actualOutput)) {
            // Split the expected and actual outputs into lines
            String[] expectedLines = formattedExpectedOutput.split("\n");
            String[] actualLines = actualOutput.split("\n");

            // Determine the maximum length of the expected and actual output lines for proper alignment
            int maxExpectedLength = 0;
            int maxActualLength = 0;
            for (String line : expectedLines) {
                if (line.length() > maxExpectedLength) {
                    maxExpectedLength = line.length();
                }
            }
            for (String line : actualLines) {
                if (line.length() > maxActualLength) {
                    maxActualLength = line.length();
                }
            }

            // Determine the width of the header and format
            int columnWidth = Math.max(maxExpectedLength, maxActualLength) + 5;
            String expectedHeader = "===EXPECTED OUTPUT===";
            String actualHeader = "===ACTUAL OUTPUT===";
            String header = String.format("%-" + columnWidth + "s | %s", expectedHeader, actualHeader);

            // ANSI escape codes for coloring
            String redColor = "\u001B[31m";
//            String blue = "\u001B[34m";
            String magentaColor = "\u001B[35m";
            String resetColor = "\u001B[0m";

            // Build the failure message with aligned columns
            StringBuilder failureMessage = new StringBuilder();
            failureMessage.append(redColor).append("Test Failed For:\n").append(resetColor);
//            failureMessage.append(blue).append("====INPUT SEQUENCE====\n").append(input.trim()).append(resetColor).append("\n");
            failureMessage.append(magentaColor).append("====INPUT SEQUENCE====\n").append(resetColor).append(input.trim()).append("\n");
            failureMessage.append(magentaColor).append(header).append(resetColor).append("\n");

            int maxLines = Math.max(expectedLines.length, actualLines.length);
            for (int i = 0; i < maxLines; i++) {
                String expectedLine = i < expectedLines.length ? expectedLines[i] : "";
                String actualLine = i < actualLines.length ? actualLines[i] : "";

                // Add spaces to the expected line to align with the actual output column
                String formattedExpectedLine = String.format("%-" + columnWidth + "s", expectedLine);

                if (!expectedLine.equals(actualLine)) {
                    failureMessage.append(redColor).append(formattedExpectedLine).append(" | ").append(actualLine).append(resetColor).append("\n");
                } else {
                    failureMessage.append(formattedExpectedLine).append(" | ").append(actualLine).append("\n");
                }
            }

            Assertions.fail(failureMessage.toString());
        }

        // Assert equality with detailed message
        assertEquals(formattedExpectedOutput, actualOutput);
    }


    @Test
    public void testExample1() {
        String input =
                "PUT user123 { username: JohnDoe, userdata: SampleData }\n" +
                        "PUT user456 { username: JaneDoe, userdata: MoreData } 100\n" +
                        "GET user123\n" +
                        "GET user789\n" +
                        "SAVE user123\n" +
                        "DEL user123\n" +
                        "GET user123\n" +
                        "EXIT";
        String expectedOutput =
                "SUCCESS\n" +
                        "SUCCESS\n" +
                        "JohnDoe\n" +
                        "UNDEFINED\n" +
                        "SUCCESS\n" +
                        "SUCCESS\n" +
                        "UNDEFINED\n" +
                        "Adios!\n";

        assertAppOutput(input, expectedOutput);
    }

    @Test
    public void testExample2() {
        String input =
                "MPUT [ user789 { username: Captain, userdata: Shield }, " +
                        "user101 { username: TonyStark, userdata: IronMan }, " +
                        "user202 { username: Thor, userdata: Thunder } ] 200\n" +
                        "MGET [ user789, user101, user303 ]\n" +
                        "MDEL [ user789, user404, user101 ]\n" +
                        "SAVE user789\n" +
                        "EXIT";
        String expectedOutput =
                "SUCCESS,SUCCESS,SUCCESS\n" +
                        "Captain,TonyStark,UNDEFINED\n" +
                        "SUCCESS,SUCCESS,SUCCESS\n" +
                        "UNDEFINED\n" +
                        "Adios!\n";

        assertAppOutput(input, expectedOutput);
    }

    @Test
    public void testExample3() {
        String input =
                "PUT user001 { username: BlackWidow, userdata: NatashaRomanoff } 300\n" +
                        "SAVE user001\n" +
                        "DEL user001\n" +
                        "GET user001\n" +
                        "SAVE user001\n" +
                        "EXIT";
        String expectedOutput =
                "SUCCESS\n" +
                        "SUCCESS\n" +
                        "SUCCESS\n" +
                        "UNDEFINED\n" +
                        "UNDEFINED\n" +
                        "Adios!\n";

        assertAppOutput(input, expectedOutput);
    }

    @Test
    public void testExample4() {
        String input =
                "PUT user001 { username: SpiderMan, userdata: PeterParker } 999\n" +
                        "MPUT [ user002 { username: IronMan, userdata: TonyStark }, " +
                        "user003 { username: Hulk, userdata: BruceBanner } ]\n" +
                        "GET user001\n" +
                        "PUT user001 { username: SpiderMan } 300\n" +
                        "POP user004 { username: Thor, userdata: ThunderGod }\n" +
                        "DEL user005\n" +
                        "SAVE user003\n" +
                        "EXIT";
        String expectedOutput =
                "SUCCESS\n" +
                        "SUCCESS,SUCCESS\n" +
                        "SpiderMan\n" +
                        "SUCCESS\n" +
                        "INVALID_COMMAND\n" +
                        "SUCCESS\n" +
                        "SUCCESS\n" +
                        "Adios!\n";

        assertAppOutput(input, expectedOutput);
    }

    @Test
    public void testExample5() {
        String input =
                "PUT user001 { username: WonderWoman, userdata: DianaPrince } 200\n" +
                        "GET user002\n" +
                        "MPUT user003 { username: Batman, userdata: BruceWayne }\n" +
                        "PUT user004 username: Superman, userdata: ClarkKent\n" +
                        "SAVE user 004 to a File\n" +
                        "DEL_user\n" +
                        "EXIT";
        String expectedOutput =
                "SUCCESS\n" +
                        "UNDEFINED\n" +
                        "INVALID_COMMAND\n" +
                        "INVALID_COMMAND\n" +
                        "INVALID_COMMAND\n" +
                        "INVALID_COMMAND\n" +
                        "Adios!\n";

        assertAppOutput(input, expectedOutput);
    }

}

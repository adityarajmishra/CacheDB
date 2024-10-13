package com.cachedb.core;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class CommandProcessor {
    private final CacheDB cacheDB;
    private final FileManager fileManager;

    private static final Pattern PUT_PATTERN = Pattern.compile("PUT (\\S+) \\{ username: ([^,]+)(?:, userdata: ([^}]+))? \\}(?: (\\d+))?");
    private static final Pattern MPUT_PATTERN = Pattern.compile("MPUT \\[\\s*((?:\\S+ \\{ username: [^,]+(?:, userdata: [^}]+)?\\},?\\s*)+)\\] ?(\\d+)?");
    private static final Pattern GET_PATTERN = Pattern.compile("GET (\\S+)");
    private static final Pattern MGET_PATTERN = Pattern.compile("MGET \\[\\s*((?:\\S+,?\\s*)+)\\]");
    private static final Pattern DEL_PATTERN = Pattern.compile("DEL (\\S+)");
    private static final Pattern MDEL_PATTERN = Pattern.compile("MDEL \\[\\s*(.+?)\\s*\\]");
    private static final Pattern SAVE_PATTERN = Pattern.compile("SAVE\\s+(\\S+)");
    private static final Pattern POP_PATTERN = Pattern.compile("POP (\\S+) \\{ username: ([^,]+)(?:, userdata: ([^}]+))? \\}");

    public CommandProcessor(CacheDB cacheDB) {
        this.cacheDB = cacheDB;
        this.fileManager = new FileManager();
    }

    public String processCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return "INVALID_COMMAND";
        }
        command = command.trim();
        if (command.startsWith("PUT ")) {
            return processPut(command);
        } else if (command.startsWith("MPUT ")) {
            return processMPut(command);
        } else if (command.startsWith("GET ")) {
            return processGet(command);
        } else if (command.startsWith("MGET ")) {
            return processMGet(command);
        } else if (command.startsWith("DEL ")) {
            return processDel(command);
        } else if (command.startsWith("MDEL ")) {
            return processMDel(command);
        } else if (command.startsWith("SAVE ")) {
            return processSave(command);
        } else if (command.startsWith("POP ")) {
            return processPop(command);
        } else {
            return "INVALID_COMMAND";
        }
    }

    private String processPut(String command) {
        Matcher matcher = PUT_PATTERN.matcher(command);
        if (matcher.matches()) {
            String key = matcher.group(1);
            String username = matcher.group(2).trim();
            String userdata = matcher.group(3) != null ? matcher.group(3).trim() : "";
            long ttl = matcher.group(4) != null ? Long.parseLong(matcher.group(4)) : 300;

            if (ttl <= 0) {
                return "INVALID_TTL"; // Validate TTL
            }

            cacheDB.put(key, new CacheEntry(username, userdata, ttl));
            return "SUCCESS";
        } else {
            return "INVALID_COMMAND";
        }
    }

    private String processMPut(String command) {
        Matcher matcher = MPUT_PATTERN.matcher(command);
        if (matcher.matches()) {
            long ttl = matcher.group(2) != null ? Long.parseLong(matcher.group(2)) : 300;

            String[] entries = matcher.group(1).split(",\\s*(?=\\S+ \\{)");
            List<String> results = new ArrayList<>();

            for (String entry : entries) {
                Matcher entryMatcher = Pattern.compile("(\\S+) \\{ username: ([^,]+)(?:, userdata: ([^}]+))? \\}").matcher(entry.trim());
                if (entryMatcher.matches()) {
                    String key = entryMatcher.group(1);
                    String username = entryMatcher.group(2).trim();
                    String userdata = entryMatcher.group(3) != null ? entryMatcher.group(3).trim() : "";

                    cacheDB.put(key, new CacheEntry(username, userdata, ttl));
                    results.add("SUCCESS");
                } else {
                    results.add("INVALID_COMMAND");
                }
            }

            return String.join(",", results);
        } else {
            return "INVALID_COMMAND";
        }
    }



    private String processGet(String command) {
        Matcher matcher = GET_PATTERN.matcher(command);
        if (matcher.matches()) {
            String key = matcher.group(1);
            CacheEntry entry = cacheDB.get(key);
            return entry != null ? entry.getUsername() : "UNDEFINED";
        } else {
            return "INVALID_COMMAND";
        }
    }

    private String processMGet(String command) {
        Matcher matcher = MGET_PATTERN.matcher(command);
        if (matcher.matches()) {
            String[] keys = matcher.group(1).split(",\\s*");
            return Arrays.stream(keys)
                    .map(key -> {
                        CacheEntry entry = cacheDB.get(key.trim());
                        return (entry != null) ? entry.getUsername() : "UNDEFINED";
                    })
                    .collect(Collectors.joining(","));
        } else {
            return "INVALID_COMMAND";
        }
    }



    private String processDel(String command) {
        Matcher matcher = DEL_PATTERN.matcher(command);
        if (matcher.matches()) {
            String key = matcher.group(1);
            cacheDB.delete(key);
            return "SUCCESS";
        } else {
            return "INVALID_COMMAND";
        }
    }

    private String processMDel(String command) {
        Matcher matcher = MDEL_PATTERN.matcher(command);
        if (matcher.matches()) {
            String[] keys = matcher.group(1).split("\\s*,\\s*");
            return Arrays.stream(keys)
                    .map(key -> {
                        cacheDB.delete(key);
                        return "SUCCESS";
                    })
                    .collect(Collectors.joining(","));
        } else {
            return "INVALID_COMMAND";
        }
    }

    private String processSave(String command) {
        Matcher matcher = SAVE_PATTERN.matcher(command);
        if (matcher.matches()) {
            String key = matcher.group(1);
            CacheEntry entry = cacheDB.get(key);
            if (entry != null) {
                fileManager.saveToFile(key, entry);
                return "SUCCESS";
            } else {
                return "UNDEFINED";
            }
        } else {
            return "INVALID_COMMAND";
        }
    }

    private String processPop(String command) {
        Matcher matcher = POP_PATTERN.matcher(command);
        if (matcher.matches()) {
            String key = matcher.group(1);
            String username = matcher.group(2).trim();
            String userdata = matcher.group(3) != null ? matcher.group(3).trim() : "";
            CacheEntry entry = cacheDB.get(key);

            if (entry == null) {
                return "INVALID_COMMAND";
            } else if (entry.getUsername().equals(username) && entry.getUserdata().equals(userdata)) {
                cacheDB.delete(key);
                return "SUCCESS";
            } else {
                return "UNDEFINED";
            }
        } else {
            return "INVALID_COMMAND";
        }
    }
}

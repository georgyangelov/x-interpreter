package net.gangelov.x.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class EscapeSequences {
    private static final Map<Integer, Integer> map = new HashMap<>();
    private static final Map<Integer, Integer> reverseMap;

    static {
        map.put((int)'"', (int)'"');
        map.put((int)'n', (int)'\n');
        map.put((int)'t', (int)'\t');
        map.put((int)'\\', (int)'\\');

        reverseMap = map.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getValue,
                Map.Entry::getKey
            ));
    };

    public static int fromSequenceChar(int c) {
        return map.getOrDefault(c, c);
    }

    public static String convertToSequences(String str) {
        StringBuilder result = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++) {
            int c = str.codePointAt(i);
            Integer seqChar = reverseMap.get(c);

            if (seqChar != null) {
                result.append("\\").appendCodePoint(seqChar);
            } else {
                result.appendCodePoint(c);
            }
        }

        return result.toString();
    }
}

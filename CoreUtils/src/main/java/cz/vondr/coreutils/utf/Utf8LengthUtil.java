package cz.vondr.coreutils.utf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Utf8LengthUtil {
    //http://stackoverflow.com/questions/8511490/calculating-length-in-utf-8-of-java-string-without-actually-encoding-it

    public static List<String> splitByUtf8Length(String input, int byteLimit) {
        validateInputParam(byteLimit);
        if (input == null) {
            return Collections.EMPTY_LIST;
        }
        if (input.isEmpty()) {
            return Arrays.asList("");
        }
        List<String> result = new ArrayList<String>();
        int startIdx = 0;
        int currentLength = 0;
        for (int currentIdx = 0; currentIdx < input.length(); currentIdx++) {
            char ch = input.charAt(currentIdx);
            int endIdx = currentIdx;
            int utf8CharLength;
            if (ch <= 0x7F) {
                utf8CharLength = 1;
            } else if (ch <= 0x7FF) {
                utf8CharLength = 2;
            } else if (Character.isHighSurrogate(ch)) {
                utf8CharLength = 4;
                currentIdx++;
            } else {
                utf8CharLength = 3;
            }
            if (currentLength + utf8CharLength > byteLimit) {
                result.add(input.substring(startIdx, endIdx));
                startIdx = currentIdx;
                currentLength = utf8CharLength;
                continue;
            }
            currentLength += utf8CharLength;
        }
        if (startIdx < input.length()) {
            result.add(input.substring(startIdx));
        }
        return result;
    }


//    public static List<String> splitByUtf8Length(String input, int byteLimit) {
//        validateInputParam(byteLimit);
//        if (input == null) {
//            return Collections.EMPTY_LIST;
//        }
//        if (input.isEmpty()) {
//            return Arrays.asList("");
//        }
//        List<String> result = new ArrayList<String>();
//        int startIdx = 0;
//        int currentLength = 0;
//        for (int currentIdx = 0; currentIdx < input.length(); currentIdx++) {
//            int endIdx = currentIdx;
//            int utf8CharLength = length(input.charAt(currentIdx));
//            if (utf8CharLength == 4) {
//                currentIdx++;
//            }
//            if (currentLength + utf8CharLength > byteLimit) {
//                result.add(input.substring(startIdx, endIdx));
//                startIdx = currentIdx;
//                currentLength = utf8CharLength;
//                continue;
//            }
//            currentLength += utf8CharLength;
//        }
//        if (startIdx < input.length()) {
//            result.add(input.substring(startIdx));
//        }
//        return result;
//    }

    private static void validateInputParam(int byteLimit) {
        if (byteLimit < 4) {
            throw new IllegalArgumentException("byteLimit '" + byteLimit + "' is not allowed. It must be at least 4 bytes.");
        }

    }

    /** Return number of bytes in UTF-8 encoding */
    public static int length(char c) {
        if (c <= 0x7F) {
            return 1;
        } else if (c <= 0x7FF) {
            return 2;
        } else if (Character.isHighSurrogate(c)) {
            return 4;
        } else {
            return 3;
        }
    }
}

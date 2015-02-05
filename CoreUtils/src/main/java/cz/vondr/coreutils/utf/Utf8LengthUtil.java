package cz.vondr.coreutils.utf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//http://stackoverflow.com/questions/8511490/calculating-length-in-utf-8-of-java-string-without-actually-encoding-it
public class Utf8LengthUtil {

    /**
     * Return how many bytes will have CharSequence in in UTF-8.
     * This is approximately 6 times faster, than converting String into UTF-8
     */
    public static int countNumberOfBytesInUtf8(CharSequence sequence) {
        if (sequence == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0, len = sequence.length(); i < len; i++) {
            char ch = sequence.charAt(i);
            if (ch <= 0x7F) {
                count++;
            } else if (ch <= 0x7FF) {
                count += 2;
            } else if (Character.isHighSurrogate(ch)) {
                count += 4;
                ++i;
            } else {
                count += 3;
            }
        }
        return count;
    }

    /**
     * Will split 'input' into multiple strings. Each string will have 'byteLimit' bytes in utf-8 or less
     */
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

    private static void validateInputParam(int byteLimit) {
        if (byteLimit < 4) {
            throw new IllegalArgumentException("byteLimit '" + byteLimit + "' is not allowed. It must be at least 4 bytes.");
        }

    }

}

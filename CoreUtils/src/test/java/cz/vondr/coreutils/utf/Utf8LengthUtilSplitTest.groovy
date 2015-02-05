package cz.vondr.coreutils.utf

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class Utf8LengthUtilSplitTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none()

    @Test
    void 'split by Utf8 length'() {
        def testSample = 'abcdefghij¶‱\uD834\uDD61#'
        def result = Utf8LengthUtil.splitByUtf8Length(testSample, 10)

        assert result[0] == "abcdefghij"
        def combined = result[0] + result[1]
        assert combined.length() == testSample.length()
        assert combined == testSample
    }

    @Test
    public void 'cannot split string by 3 bytes'() {
        expectedException.expect(IllegalArgumentException.class)
        expectedException.expectMessage("byteLimit '3' is not allowed. It must be at least 4 bytes.")

        List<String> splitResult = Utf8LengthUtil.splitByUtf8Length("String to split", 3)
    }

    @Test
    public void 'cannot split string by -5 bytes'() {
        expectedException.expect(IllegalArgumentException.class)
        expectedException.expectMessage("byteLimit '-5' is not allowed. It must be at least 4 bytes.")

        List<String> splitResult = Utf8LengthUtil.splitByUtf8Length("String to split", -5)
    }

    @Test
    public void 'split empty string'() {
        List<String> splitResult = Utf8LengthUtil.splitByUtf8Length("", 20)

        assert splitResult == [""]
    }

    @Test
    public void 'split null string'() {
        List<String> splitResult = Utf8LengthUtil.splitByUtf8Length(null, 20)

        assert splitResult == []
    }

}

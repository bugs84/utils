package cz.vondr.coreutils.utf

import org.junit.Test

class Utf8LengthUtilCountTest {

    @Test
    public void 'count null string'() {
        def numberOfBytes = Utf8LengthUtil.countNumberOfBytesInUtf8(null)

        assert numberOfBytes == 0
    }

    @Test
    public void 'count empty string'() {
        def numberOfBytes = Utf8LengthUtil.countNumberOfBytesInUtf8("")

        assert numberOfBytes == 0
    }

    @Test
    public void 'count one byte character'() {
        def numberOfBytes = Utf8LengthUtil.countNumberOfBytesInUtf8("a")

        assert numberOfBytes == 1
    }

    @Test
    public void 'count two bytes chararacter'() {
        def numberOfBytes = Utf8LengthUtil.countNumberOfBytesInUtf8("ž")

        assert numberOfBytes == 2
    }

    @Test
    public void 'count three bytes chararacter'() {
        def numberOfBytes = Utf8LengthUtil.countNumberOfBytesInUtf8("‱")

        assert numberOfBytes == 3
    }

    @Test
    public void 'count surrogate chararacter'() {
        def numberOfBytes = Utf8LengthUtil.countNumberOfBytesInUtf8("\uD834\uDD62")

        assert numberOfBytes == 4
    }

    @Test
    public void 'count complex string length'() {
        def testString = 'abcdefghij¶‱\uD834\uDD61#'

        def numberOfBytes = Utf8LengthUtil.countNumberOfBytesInUtf8(testString)

        assert numberOfBytes == 20
    }
}

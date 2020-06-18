import org.junit.Before;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CodeCodeLineCounterTest {

    CodeLineCounter counter;

    @Before
    public void setUp(){
        counter = new CodeLineCounter();
    }

    @Test
    public void checkEmptyLines() {
        String[] lines = {""};

        int actual = counter.countLines(lines);

        assertEquals(0, actual);
    }

    @Test
    public void checkCodeLines() {
        String[] lines = {"public"};

        int actual = counter.countLines(lines);

        assertEquals(1, actual);
    }

    @Test
    public void noCountedIfCommented() {
        String[] lines = {"// import   "};

        int actual = counter.countLines(lines);

        assertEquals(0, actual);
    }

    @Test
    public void countedIfCommentedInsideLine() {
        String[] lines = {"String line // int   "};

        int actual = counter.countLines(lines);

        assertEquals(1, actual);
    }

    @Test
    public void noCountForCommentBlock() {
        String[] lines = {"/*some stuff*/"};

        int actual = counter.countLines(lines);

        assertEquals(0, actual);
    }

    @Test
    public void noCountIfMultipleComentedLines() {
        String[] lines = {"/**",
                "   * multiple lines",
                "   */"};

        int actual = counter.countLines(lines);

        assertEquals(0, actual);
    }

    @Test
    public void noCountIftwoCommentsInALine() {
        String[] lines = {"/*some*//*stuff*//*here*/"};

        int actual = counter.countLines(lines);

        assertEquals(0, actual);
    }

    @Test
    public void countIfLineBetweenTwoCommente() {
        String[] lines = {"/*some*/stuff/*here*/"};

        int actual = counter.countLines(lines);

        assertEquals(1, actual);
    }

}
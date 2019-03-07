package net.jsrbc;

import net.jsrbc.frame.annotation.database.Param;
import net.jsrbc.utils.SystemUtils;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, "123", "124", "225");
        Map<String, List<String>> map = list.stream().collect(Collectors.groupingBy(s->s.substring(0, 1)));
    }

    public void doSomething(@Param("asd") String text, String value) {}
}
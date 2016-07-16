package de.herrlock.manga.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.herrlock.manga.exceptions.MDRuntimeException;

public final class TestCtrlUtils {

    @Test
    public void test1() {
        StackTraceElement[] stackTrace = new MDRuntimeException( "some message" ).getStackTrace();
        List<String> stringIterable = CtrlUtils.convertStackTraceElementsToStringIterable( stackTrace );
        String regex = "^de\\.herrlock\\.manga\\.util\\.TestCtrlUtils\\.test1\\(.+\\.java:\\d+\\)$";
        String string = stringIterable.get( 0 );
        Assert.assertTrue( string.matches( regex ) );
    }

    @Test
    public void test2() {
        StackTraceElement[] stackTrace = new MDRuntimeException( "some message" ).getStackTrace();
        String string = CtrlUtils.convertStackTraceElementsToSingleNewlineString( stackTrace );
        String regex = "^de\\.herrlock\\.manga\\.util\\.TestCtrlUtils\\.test2\\(.+\\.java:\\d+\\)\\n[\\s\\S]+";
        Assert.assertTrue( string.matches( regex ) );
    }

}

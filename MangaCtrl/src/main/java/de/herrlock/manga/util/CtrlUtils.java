package de.herrlock.manga.util;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.herrlock.javafx.Dialogs;
import javafx.scene.control.TextArea;

public final class CtrlUtils {
    private static final Joiner NEWLINE_JOINER = Joiner.on( '\n' );

    public static List<String> convertStackTraceElementsToStringIterable( final StackTraceElement... elements ) {
        List<StackTraceElement> elementList = Arrays.asList( elements );
        return Lists.transform( elementList, Functions.toStringFunction() );
    }

    public static String convertStackTraceElementsToSingleNewlineString( final StackTraceElement... elements ) {
        List<String> strings = convertStackTraceElementsToStringIterable( elements );
        return NEWLINE_JOINER.join( strings );
    }

    public static void showErrorDialog( final Exception ex ) {
        String joinedStackTrace = convertStackTraceElementsToSingleNewlineString( ex.getStackTrace() );
        TextArea textArea = new TextArea( joinedStackTrace );
        textArea.setEditable( false );
        Dialogs.showErrorDialog( null, textArea, ex.getMessage() );
    }

    private CtrlUtils() {
        // not used
    }
}

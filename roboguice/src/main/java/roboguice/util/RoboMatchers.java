package roboguice.util;

import android.content.Context;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

public class RoboMatchers {
    public static Matcher<? super TypeLiteral<?>> subclassesOfContext() {
        return new AbstractMatcher<TypeLiteral<?>>() {
                    @Override
                    public boolean matches(TypeLiteral<?> typeLiteral) {
                        return Context.class.isAssignableFrom(typeLiteral.getRawType());
                    }
                };
    }
}

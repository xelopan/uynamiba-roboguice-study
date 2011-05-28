package roboguice.util;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.*;
import com.google.inject.spi.TypeConverterBinding;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static org.junit.Assert.assertSame;


@RunWith(RobolectricTestRunner.class)
public class GuiceExperimentsTest {

    @Ignore("Okay, so that didn't work")
    @Test
    public void shouldGetChildInjectorFromProviderInParent() {
        final Injector parent = Guice.createInjector( new ParentModule() );
        final Injector child  = parent.createChildInjector( new ChildModule() );
        final ContextSurrogateProvider contextProvider = child.getInstance(ContextSurrogateProvider.class);

        assertSame(contextProvider.injectorProvider.get(), child);
    }


    @Ignore("Not sure how to do this")
    @Test
    public void customInjector() {

    }

    //@Ignore("yeah no, this isn't going to work")
    @Test
    public void maybeScopeWillWork() {
        final ContextSurrogateScope scope = new ContextSurrogateScope();

        final Injector parent = Guice.createInjector( new AbstractModule() {
            @Override
            protected void configure() {
                bindScope(MyScope.class, scope);
            }
        });

        final Injector child = parent.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(A.class).in(scope);
            }
        });


        final Provider<A> pA = child.getProvider(A.class);
        System.out.println( "BOOGA " + pA.getClass() );
        System.err.println( "BOOGA " + pA.getClass() );

        // Not sure where i'm going with this


    }

    @Target(TYPE) @Retention(RUNTIME) @ScopeAnnotation
    public @interface MyScope {}


    public static class A {}

    public static class ContextSurrogateScope implements Scope {

        @Override
        public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
            return null;
        }
    }



    @Ignore("Okay, so that didn't work either")
    @Test
    public void shouldGetChildInjectorFromProviderProviderInParent() {
        final Injector parent = Guice.createInjector( new ParentModule() );
        final Injector child  = parent.createChildInjector( new ChildModule() );
        final Provider<ContextSurrogateProvider> contextProviderProvider = child.getProvider(ContextSurrogateProvider.class);

        assertSame(contextProviderProvider.get().injectorProvider.get(), child);
    }




    public static class ContextSurrogateInjector implements Injector {
        protected Injector parent;
        protected ContextSurrogate contextSurrogate;

        public ContextSurrogateInjector(Injector parent, ContextSurrogate contextSurrogate ) {
            this.parent = parent;
            this.contextSurrogate = contextSurrogate;
        }


        @Override
        public <T> T getInstance(Key<T> key) {
            return parent.getInstance(key);
        }

        @Override
        public <T> T getInstance(Class<T> type) {
            return parent.getInstance(type);
        }

        @Override
        public Injector getParent() {
            return parent.getParent();
        }

        @Override
        public <T> com.google.inject.Provider<T> getProvider(Key<T> key) {
            return parent.getProvider(key);
        }

        @Override
        public <T> com.google.inject.Provider<T> getProvider(Class<T> type) {
            return parent.getProvider(type);
        }

        @Override
        public void injectMembers(Object instance) {
            parent.injectMembers(instance);
        }



















        @Override
        public Injector createChildInjector(Iterable<? extends Module> modules) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Injector createChildInjector(Module... modules) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<Key<?>, Binding<?>> getAllBindings() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Binding<T> getBinding(Key<T> key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Binding<T> getBinding(Class<T> type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<Key<?>, Binding<?>> getBindings() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Binding<T> getExistingBinding(Key<T> key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<TypeConverterBinding> getTypeConverterBindings() {
            throw new UnsupportedOperationException();
        }

    }



    public static class ParentModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ContextSurrogateProvider.class);

        }
    }

    public static class ChildModule extends AbstractModule {
        @Override
        protected void configure() {

        }
    }

    public static class ContextSurrogateProvider implements Provider<ContextSurrogate> {
        @Inject protected Provider<Injector> injectorProvider;

        @Override
        public ContextSurrogate get() {
            return null; // doesn't matter
        }
    }


    public static class ContextSurrogate {

    }


}

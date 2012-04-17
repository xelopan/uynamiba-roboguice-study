package roboguice.util;

public class Finalizer {
    protected NotifyingWeakHashMap<Object,Finalizer.Callback<?>> map = new NotifyingWeakHashMap<Object,Finalizer.Callback<?>>() {
        @Override
        protected void onExpunge(Entry<Object, Finalizer.Callback<?>> e) {
            final Object key = e.getKey();
            Ln.d("Finalizing %s", key);
            //noinspection unchecked
            ((Finalizer.Callback<Object>)e.getValue()).call(key);
        }
    };

    public <T> void onFinalize( T t, Finalizer.Callback<T> callback ) {
        map.put(t,callback);
    }


    public static interface Callback<T> {
        public void call(T t);
    }
}

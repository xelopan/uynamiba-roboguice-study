package roboguice.astroboy;

import roboguice.astroboy.bean.DateExtraConverter;
import roboguice.astroboy.bean.DateTwiceExtraConverter;
import roboguice.astroboy.bean.Person;
import roboguice.astroboy.bean.PersonExtraConverter;
import roboguice.config.DefaultApplicationRoboModule;
import roboguice.inject.ExtraConverter;
import roboguice.inject.SharedPreferencesName;

import android.app.Application;

import com.google.inject.TypeLiteral;

import java.util.Date;

public class AstroboyApplicationModule extends DefaultApplicationRoboModule {

    public AstroboyApplicationModule(Application application) {
        super(application);
    }

    @Override
    public void configure() {
        super.configure();
        
        bind(new TypeLiteral<ExtraConverter<String, Person>>(){}).to(PersonExtraConverter.class);
        bind(new TypeLiteral<ExtraConverter<Long, Date>>(){}).to(DateExtraConverter.class);
        bind(new TypeLiteral<ExtraConverter<Integer, Date>>(){}).to(DateTwiceExtraConverter.class);

        // BUG need a better way to set default preferences context
        bindConstant().annotatedWith(SharedPreferencesName.class).to("roboguice.astroboy");
    }
}

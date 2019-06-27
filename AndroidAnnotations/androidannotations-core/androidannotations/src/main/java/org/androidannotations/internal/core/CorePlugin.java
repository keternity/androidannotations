/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.internal.core;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.Option;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.internal.core.handler.*;
import org.androidannotations.internal.core.model.AndroidRes;
import org.androidannotations.plugin.AndroidAnnotationsPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorePlugin extends AndroidAnnotationsPlugin {

    private static final String NAME = "AndroidAnnotations";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Option> getSupportedOptions() {
        return Arrays.asList(TraceHandler.OPTION_TRACE, SupposeThreadHandler.OPTION_THREAD_CONTROL);
    }

    @Override
    public List<AnnotationHandler<?>> getHandlers(AndroidAnnotationsEnvironment androidAnnotationEnv) {
        List<AnnotationHandler<?>> annotationHandlers = new ArrayList<>();
        annotationHandlers.add(new EApplicationHandler(androidAnnotationEnv));
        annotationHandlers.add(new EActivityHandler(androidAnnotationEnv));
        annotationHandlers.add(new EProviderHandler(androidAnnotationEnv));
        annotationHandlers.add(new EReceiverHandler(androidAnnotationEnv));
        annotationHandlers.add(new EServiceHandler(androidAnnotationEnv));
        annotationHandlers.add(new EIntentServiceHandler(androidAnnotationEnv));
        annotationHandlers.add(new EFragmentHandler(androidAnnotationEnv));
        annotationHandlers.add(new EBeanHandler(androidAnnotationEnv));
        annotationHandlers.add(new EViewGroupHandler(androidAnnotationEnv));
        annotationHandlers.add(new ViewArgHandler(androidAnnotationEnv));
        annotationHandlers.add(new EViewHandler(androidAnnotationEnv));
        annotationHandlers.add(new SharedPrefHandler(androidAnnotationEnv));
        annotationHandlers.add(new PrefHandler(androidAnnotationEnv));
        annotationHandlers.add(new ViewByIdHandler(androidAnnotationEnv));
        annotationHandlers.add(new ViewsByIdHandler(androidAnnotationEnv));
        annotationHandlers.add(new FragmentByIdHandler(androidAnnotationEnv));
        annotationHandlers.add(new FragmentByTagHandler(androidAnnotationEnv));
        annotationHandlers.add(new FromHtmlHandler(androidAnnotationEnv));
        annotationHandlers.add(new ClickHandler(androidAnnotationEnv));
        annotationHandlers.add(new LongClickHandler(androidAnnotationEnv));
        annotationHandlers.add(new TouchHandler(androidAnnotationEnv));
        annotationHandlers.add(new FocusChangeHandler(androidAnnotationEnv));
        annotationHandlers.add(new CheckedChangeHandler(androidAnnotationEnv));
        annotationHandlers.add(new ItemClickHandler(androidAnnotationEnv));
        annotationHandlers.add(new ItemSelectHandler(androidAnnotationEnv));
        annotationHandlers.add(new ItemLongClickHandler(androidAnnotationEnv));
        annotationHandlers.add(new EditorActionHandler(androidAnnotationEnv));
        for (AndroidRes androidRes : AndroidRes.values()) {
            if (androidRes == AndroidRes.ANIMATION) {
                annotationHandlers.add(new AnimationResHandler(androidAnnotationEnv));
            } else if (androidRes == AndroidRes.COLOR) {
                annotationHandlers.add(new ColorResHandler(androidAnnotationEnv));
            } else if (androidRes == AndroidRes.COLOR_STATE_LIST) {
                annotationHandlers.add(new ColorStateListResHandler(androidAnnotationEnv));
            } else if (androidRes == AndroidRes.DRAWABLE) {
                annotationHandlers.add(new DrawableResHandler(androidAnnotationEnv));
            } else if (androidRes == AndroidRes.HTML) {
                annotationHandlers.add(new HtmlResHandler(androidAnnotationEnv));
            } else {
                annotationHandlers.add(new DefaultResHandler(androidRes, androidAnnotationEnv));
            }
        }
        annotationHandlers.add(new TransactionalHandler(androidAnnotationEnv));
        annotationHandlers.add(new FragmentArgHandler(androidAnnotationEnv));
        annotationHandlers.add(new SystemServiceHandler(androidAnnotationEnv));

        annotationHandlers.add(new NonConfigurationInstanceHandler(androidAnnotationEnv));
        annotationHandlers.add(new AppHandler(androidAnnotationEnv));
        annotationHandlers.add(new BeanHandler(androidAnnotationEnv));
        annotationHandlers.add(new InjectMenuHandler(androidAnnotationEnv));
        annotationHandlers.add(new OptionsMenuHandler(androidAnnotationEnv));
        annotationHandlers.add(new OptionsMenuItemHandler(androidAnnotationEnv));
        annotationHandlers.add(new OptionsItemHandler(androidAnnotationEnv));
        annotationHandlers.add(new CustomTitleHandler(androidAnnotationEnv));
        annotationHandlers.add(new FullscreenHandler(androidAnnotationEnv));
        annotationHandlers.add(new RootContextHandler(androidAnnotationEnv));
        annotationHandlers.add(new RootFragmentHandler(androidAnnotationEnv));
        annotationHandlers.add(new ExtraHandler(androidAnnotationEnv));
        annotationHandlers.add(new BindingObjectHandler(androidAnnotationEnv));
        annotationHandlers.add(new BeforeTextChangeHandler(androidAnnotationEnv));
        annotationHandlers.add(new TextChangeHandler(androidAnnotationEnv));
        annotationHandlers.add(new AfterTextChangeHandler(androidAnnotationEnv));
        annotationHandlers.add(new SeekBarProgressChangeHandler(androidAnnotationEnv));
        annotationHandlers.add(new SeekBarTouchStartHandler(androidAnnotationEnv));
        annotationHandlers.add(new SeekBarTouchStopHandler(androidAnnotationEnv));
        annotationHandlers.add(new KeyDownHandler(androidAnnotationEnv));
        annotationHandlers.add(new KeyLongPressHandler(androidAnnotationEnv));
        annotationHandlers.add(new KeyMultipleHandler(androidAnnotationEnv));
        annotationHandlers.add(new KeyUpHandler(androidAnnotationEnv));
        annotationHandlers.add(new ServiceActionHandler(androidAnnotationEnv));
        annotationHandlers.add(new InstanceStateHandler(androidAnnotationEnv));
        annotationHandlers.add(new HttpsClientHandler(androidAnnotationEnv));
        annotationHandlers.add(new HierarchyViewerSupportHandler(androidAnnotationEnv));
        annotationHandlers.add(new WindowFeatureHandler(androidAnnotationEnv));
        annotationHandlers.add(new ReceiverHandler(androidAnnotationEnv));
        annotationHandlers.add(new ReceiverActionHandler(androidAnnotationEnv));
        annotationHandlers.add(new OnActivityResultHandler(androidAnnotationEnv));
        annotationHandlers.add(new PageScrolledHandler(androidAnnotationEnv));
        annotationHandlers.add(new PageScrollStateChangedHandler(androidAnnotationEnv));
        annotationHandlers.add(new PageSelectedHandler(androidAnnotationEnv));

        annotationHandlers.add(new IgnoreWhenHandler(androidAnnotationEnv));

        annotationHandlers.add(new AfterInjectHandler(androidAnnotationEnv));
        annotationHandlers.add(new AfterExtrasHandler(androidAnnotationEnv));
        annotationHandlers.add(new AfterViewsHandler(androidAnnotationEnv));

        annotationHandlers.add(new PreferenceScreenHandler(androidAnnotationEnv));
        annotationHandlers.add(new PreferenceHeadersHandler(androidAnnotationEnv));
        annotationHandlers.add(new PreferenceByKeyHandler(androidAnnotationEnv));
        annotationHandlers.add(new PreferenceChangeHandler(androidAnnotationEnv));
        annotationHandlers.add(new PreferenceClickHandler(androidAnnotationEnv));
        annotationHandlers.add(new AfterPreferencesHandler(androidAnnotationEnv));

        annotationHandlers.add(new DataBoundHandler(androidAnnotationEnv));

        annotationHandlers.add(new TraceHandler(androidAnnotationEnv));

        /*
         * WakeLockHandler must be after TraceHandler but before UiThreadHandler and
         * BackgroundHandler
         */
        annotationHandlers.add(new WakeLockHandler(androidAnnotationEnv));

        /*
         * UIThreadHandler and BackgroundHandler must be after TraceHandler and
         * IgnoreWhen
         */
        annotationHandlers.add(new UiThreadHandler(androidAnnotationEnv));
        annotationHandlers.add(new BackgroundHandler(androidAnnotationEnv));

        /*
         * SupposeUiThreadHandler and SupposeBackgroundHandler must be after all
         * handlers that modifies generated method body
         */
        annotationHandlers.add(new SupposeUiThreadHandler(androidAnnotationEnv));
        annotationHandlers.add(new SupposeBackgroundHandler(androidAnnotationEnv));

        return annotationHandlers;
    }
}

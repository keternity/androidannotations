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
package org.androidannotations.internal.core.handler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import com.helger.jcodemodel.*;
import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.DataBound;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.api.x2c.IViewCreator;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EViewGroupHolder;
import org.androidannotations.internal.x2c.X2CManager;
import org.androidannotations.rclass.IRClass;

public class EViewGroupHandler extends CoreBaseGeneratingAnnotationHandler<EViewGroupHolder> {

    public EViewGroupHandler(AndroidAnnotationsEnvironment environment) {
        super(EViewGroup.class, environment);
    }

    @Override
    public EViewGroupHolder createGeneratedClassHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
        return new EViewGroupHolder(environment, annotatedElement);
    }

    @Override
    public void validate(Element element, ElementValidation validation) {
        super.validate(element, validation);

        validatorHelper.extendsViewGroup(element, validation);

        validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.ALLOW_NO_RES_ID, validation);

        coreValidatorHelper.checkDataBoundAnnotation(element, validation);
    }

    @Override
    public void process(Element element, EViewGroupHolder holder) {
        JFieldRef contentViewId = annotationHelper.extractOneAnnotationFieldRef(element, IRClass.Res.LAYOUT, false);
        if (contentViewId == null) {
            return;
        }
        EViewGroup eViewGroup = element.getAnnotation(EViewGroup.class);
        if (element.getAnnotation(DataBound.class) != null) {
            holder.getSetContentViewBlock().assign(holder.getDataBindingField(), holder.getDataBindingInflationExpression(contentViewId, JExpr._this(), true));
        } else {
            if (eViewGroup.useX2C()) {
                Elements elements = getProcessingEnvironment().getElementUtils();
                X2CManager.savePackageName = elements.getPackageOf(element).getQualifiedName().toString();
                X2CManager.process(getProcessingEnvironment().getFiler(), contentViewId.name());
                String name = X2CManager.getSaveViewName(contentViewId.name());
                AbstractJClass viewCreator = getJClass(IViewCreator.class);
                AbstractJClass x2cView = getJClass(X2CManager.savePackageName + ".x2c.views." + name);
                JBlock contentViewBlock = holder.getSetContentViewBlock();
                JVar viewCreatorVar = contentViewBlock.decl(viewCreator, "viewCreator");
                contentViewBlock.assign(viewCreatorVar, JExpr._new(x2cView));
                contentViewBlock.add(viewCreatorVar.invoke("createView").arg(JExpr._this()));
            } else {
                holder.getSetContentViewBlock().add(JExpr.invoke("inflate").arg(holder.getContextRef()).arg(contentViewId).arg(JExpr._this()));
            }
        }
    }
}

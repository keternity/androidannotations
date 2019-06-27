/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.holder;

import static com.helger.jcodemodel.JMod.PUBLIC;

import javax.lang.model.element.TypeElement;

import com.helger.jcodemodel.*;
import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.api.builder.ViewBuilder;
import org.androidannotations.helper.ModelConstants;

public class EViewGroupHolder extends EViewHolder {

    private JBlock setContentViewBlock;
    private JDefinedClass viewBuilderClass;
    private AbstractJClass narrowBuilderClass;
    private JFieldRef viewArgumentsBuilderField;
    private JMethod injectArgsMethod;
    private JVar injectBundleArgs;
    private JBlock injectArgsBlock;

    public EViewGroupHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
        super(environment, annotatedElement);
        this.setViewBuilder();
    }

    protected void setOnFinishInflate() {
        onFinishInflate = generatedClass.method(PUBLIC, getCodeModel().VOID, "onFinishInflate");
        onFinishInflate.annotate(Override.class);
        onFinishInflate.javadoc().append(ALREADY_INFLATED_COMMENT);

        JBlock ifNotInflated = onFinishInflate.body()._if(getAlreadyInflated().not())._then();
        ifNotInflated.assign(getAlreadyInflated(), JExpr.TRUE);

        setContentViewBlock = ifNotInflated.blockSimple();

        getInit();
        viewNotifierHelper.invokeViewChanged(ifNotInflated);

        onFinishInflate.body().invoke(JExpr._super(), "onFinishInflate");
    }

    public JBlock getSetContentViewBlock() {
        if (setContentViewBlock == null) {
            setOnFinishInflate();
        }
        return setContentViewBlock;
    }

    public JBlock getInjectArgsBlock() {
        if (this.injectArgsBlock == null) {
            this.setInjectArgs();
        }
        return this.injectArgsBlock;
    }

    public JVar getInjectBundleArgs() {
        if (this.injectBundleArgs == null) {
            this.setInjectArgs();
        }

        return this.injectBundleArgs;
    }

    public JMethod getInjectArgsMethod() {
        if (this.injectArgsMethod == null) {
            this.setInjectArgs();
        }

        return this.injectArgsMethod;
    }

    public JDefinedClass getBuilderClass() {
        return this.viewBuilderClass;
    }

    public JFieldRef getBuilderArgsField() {
        return this.viewArgumentsBuilderField;
    }

    private void setInjectArgs() {
        this.injectArgsMethod = this.generatedClass.method(4, this.getCodeModel().VOID, "injectViewArguments" + ModelConstants.generationSuffix());
        JBlock injectExtrasBody = this.injectArgsMethod.body();
        this.injectBundleArgs = injectExtrasBody.decl(this.getClasses().BUNDLE, "args_", JExpr.cast(this.getClasses().BUNDLE, JExpr.invoke("getTag")));
        this.injectArgsBlock = injectExtrasBody._if(this.injectBundleArgs.ne(JExpr._null()))._then();
        this.getInitBodyInjectionBlock().invoke(this.injectArgsMethod);
    }

    private void setViewBuilder() throws JClassAlreadyExistsException {
        this.viewBuilderClass = this.generatedClass._class(17, "ViewBuilder" + ModelConstants.generationSuffix());
        this.narrowBuilderClass = this.narrow(this.viewBuilderClass);
        this.codeModelHelper.generify(this.viewBuilderClass, this.annotatedElement);
        AbstractJClass superClass = this.getJClass(ViewBuilder.class);
        superClass = superClass.narrow(this.narrowBuilderClass, this.getAnnotatedClass());
        this.viewBuilderClass._extends(superClass);
        this.viewArgumentsBuilderField = JExpr.ref("args");
        this.setViewBuilderBuild();
        this.setViewBuilderCreate();
    }

    private void setViewBuilderBuild() {
        JMethod method = this.viewBuilderClass.method(1, this.generatedClass._extends(), "build");
        JVar context = method.param(getClasses().CONTEXT, "context");
        method.annotate(Override.class);
        JBlock body = method.body();

        JVar view = body.decl(this.generatedClass._extends(), "view_", this.generatedClass.staticInvoke("build").arg(context));
        body.add(view.invoke("setTag").arg(this.viewArgumentsBuilderField));
        body._return(view);
    }

    private void setViewBuilderCreate() {
        JMethod method = this.generatedClass.method(17, this.narrowBuilderClass, "builder");
        this.codeModelHelper.generify(method, this.annotatedElement);
        method.body()._return(JExpr._new(this.narrowBuilderClass));
    }
}

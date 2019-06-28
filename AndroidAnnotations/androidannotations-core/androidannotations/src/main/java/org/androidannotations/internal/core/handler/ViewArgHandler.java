package org.androidannotations.internal.core.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewArg;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EViewGroupHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class ViewArgHandler extends BaseAnnotationHandler<EViewGroupHolder>
		implements MethodInjectionHandler<EViewGroupHolder>, MethodInjectionHandler.AfterAllParametersInjectedHandler<EViewGroupHolder> {
	private final InjectHelper<EViewGroupHolder> injectHelper;

	public ViewArgHandler(AndroidAnnotationsEnvironment environment) {
		super(ViewArg.class, environment);
		this.injectHelper = new InjectHelper<>(this.validatorHelper, this);
	}

	public void validate(Element element, ElementValidation validation) {
		this.injectHelper.validate(ViewArg.class, element, validation);
		if (validation.isValid()) {
			this.validatorHelper.isNotPrivate(element, validation);
			Element param = this.injectHelper.getParam(element);
			this.validatorHelper.canBePutInABundle(param, validation);
		}
	}

	public void process(Element element, EViewGroupHolder holder) {
		this.injectHelper.process(element, holder);
	}

	public JBlock getInvocationBlock(EViewGroupHolder holder) {
		return holder.getInjectArgsBlock();
	}

	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EViewGroupHolder holder, Element element, Element param) {
		String fieldName = element.getSimpleName().toString();
		String argKey = this.extractArgKey(element, fieldName);
		if (element.getKind() != ElementKind.PARAMETER) {
			this.createBuilderInjectionMethod(holder, element, new ArgHelper(param, argKey));
		}

		TypeMirror actualType = this.codeModelHelper.getActualTypeOfEnclosingElementOfInjectedElement(holder, param);
		AbstractJClass elementClass = this.codeModelHelper.typeMirrorToJClass(actualType);
		BundleHelper bundleHelper = new BundleHelper(this.getEnvironment(), actualType);
		JVar bundle = holder.getInjectBundleArgs();
		JMethod injectExtrasMethod = holder.getInjectArgsMethod();
		JFieldVar extraKeyStaticField = this.getOrCreateStaticArgField(holder, argKey, fieldName);
		IJExpression restoreMethodCall = bundleHelper.getExpressionToRestoreFromBundle(elementClass, bundle, extraKeyStaticField, injectExtrasMethod);
		JConditional conditional = targetBlock._if(JExpr.invoke(bundle, "containsKey").arg(extraKeyStaticField));
		conditional._then().add(fieldRef.assign(restoreMethodCall));
	}

	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasAnnotation(EViewGroup.class, element, valid);
	}

	public void afterAllParametersInjected(EViewGroupHolder holder, ExecutableElement method, List<InjectHelper.ParamHelper> parameterList) {
		List<ArgHelper> argHelpers = new ArrayList<>();
		for (InjectHelper.ParamHelper paramHelper : parameterList) {
			Element param = paramHelper.getParameterElement();
			String fieldName = param.getSimpleName().toString();
			String argKey = this.extractArgKey(param, fieldName);
			argHelpers.add(new ArgHelper(param, argKey));
		}
		this.createBuilderInjectMethod(holder, method, argHelpers);
	}

	private String extractArgKey(Element element, String fieldName) {
		ViewArg annotation = element.getAnnotation(ViewArg.class);
		String argKey = annotation.value();
		if (argKey.isEmpty()) {
			argKey = fieldName;
		}
		return argKey;
	}

	private JFieldVar getOrCreateStaticArgField(EViewGroupHolder holder, String argKey, String fieldName) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(null, fieldName, "Arg");
		JFieldVar staticExtraField = holder.getGeneratedClass().fields().get(staticFieldName);
		if (staticExtraField == null) {
			staticExtraField = holder.getGeneratedClass().field(25, this.getClasses().STRING, staticFieldName, JExpr.lit(argKey));
		}
		return staticExtraField;
	}

	private void createBuilderInjectionMethod(EViewGroupHolder holder, Element element, ArgHelper argHelper) {
		this.createBuilderInjectMethod(holder, element, Collections.singletonList(argHelper));
	}

	public void createBuilderInjectMethod(EViewGroupHolder holder, Element element, List<ArgHelper> argHelpers) {
		JDefinedClass builderClass = holder.getBuilderClass();
		JFieldRef builderArgsField = holder.getBuilderArgsField();
		JMethod builderMethod = builderClass.method(1, holder.narrow(builderClass), element.getSimpleName().toString());
		String docComment = this.getProcessingEnvironment().getElementUtils().getDocComment(element);
		this.codeModelHelper.addTrimmedDocComment(builderMethod, docComment);
		for (ArgHelper argHelper : argHelpers) {
			String fieldName = argHelper.param.getSimpleName().toString();
			TypeMirror actualType = this.codeModelHelper.getActualTypeOfEnclosingElementOfInjectedElement(holder, argHelper.param);
			BundleHelper bundleHelper = new BundleHelper(this.getEnvironment(), actualType);
			JFieldVar argKeyStaticField = this.getOrCreateStaticArgField(holder, argHelper.argKey, fieldName);
			AbstractJClass paramClass = this.codeModelHelper.typeMirrorToJClass(actualType);
			JVar arg = builderMethod.param(paramClass, fieldName);
			builderMethod.body().add(bundleHelper.getExpressionToSaveFromField(builderArgsField, argKeyStaticField, arg));
			builderMethod.javadoc().addParam(fieldName).append("value for this View argument");
		}
		builderMethod.javadoc().addReturn().append("the ViewBuilder to chain calls");
		builderMethod.body()._return(JExpr._this());
	}

	private static class ArgHelper {
		private final Element param;
		private final String argKey;

		ArgHelper(Element param, String argKey) {
			this.param = param;
			this.argKey = argKey;
		}
	}
}

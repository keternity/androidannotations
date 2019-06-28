package org.androidannotations.internal.core.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.OnFragmentResult;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.HasParameterHandlers;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EFragmentHolder;
import org.androidannotations.holder.OnFragmentResultDelegate;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JOp;
import com.helger.jcodemodel.JVar;

public class OnFragmentResultHandler extends BaseAnnotationHandler<EFragmentHolder> implements HasParameterHandlers<EFragmentHolder> {
	private ExtraHandler extraHandler;
	private Map<EFragmentHolder, OnFragmentResultDelegate> onFragmentResultDelegateMap = new HashMap<>();

	public OnFragmentResultHandler(AndroidAnnotationsEnvironment environment) {
		super(OnFragmentResult.class, environment);
		this.extraHandler = new ExtraHandler(environment);
	}

	public Iterable<AnnotationHandler> getParameterHandlers() {
		List<AnnotationHandler> list = new ArrayList<>(1);
		list.add(extraHandler);
		return list;
	}

	public void validate(Element element, ElementValidation validation) {
		this.validatorHelper.enclosingElementHasEFragment(element, validation);
		this.validatorHelper.isNotPrivate(element, validation);
		this.validatorHelper.doesntThrowException(element, validation);
		OnFragmentResult onResultAnnotation = element.getAnnotation(OnFragmentResult.class);
		this.validatorHelper.annotationValuePositiveAndInAShort(onResultAnnotation.value(), validation);
		ExecutableElement executableElement = (ExecutableElement) element;
		this.validatorHelper.returnTypeIsVoid(executableElement, validation);
		this.validatorHelper.param.anyOrder().type("android.os.Bundle").optional().primitiveOrWrapper(TypeKind.INT).optional().annotatedWith(OnFragmentResult.Extra.class).multiple().optional()
				.validate(executableElement, validation);
	}

	public void process(Element element, EFragmentHolder holder) throws Exception {
		OnFragmentResultDelegate onFragmentResultDelegate;
		if (onFragmentResultDelegateMap.containsKey(holder)) {
			onFragmentResultDelegate = onFragmentResultDelegateMap.get(holder);
		} else {
			onFragmentResultDelegate = new OnFragmentResultDelegate(holder);
			onFragmentResultDelegateMap.put(holder, onFragmentResultDelegate);
		}
		String methodName = element.getSimpleName().toString();
		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();
		int requestCode = (executableElement.getAnnotation(OnFragmentResult.class)).value();
		JBlock onResultBlock = onFragmentResultDelegate.getCaseBlock(requestCode).blockSimple();
		IJExpression activityRef = holder.getGeneratedClass().staticRef("this");
		JInvocation onResultInvocation = JExpr.invoke(activityRef, methodName);
		JVar bundle = onFragmentResultDelegate.getDataParam();
		JVar extras = null;
		for (VariableElement parameter : parameters) {
			TypeMirror parameterType = parameter.asType();
			if (parameter.getAnnotation(OnFragmentResult.Extra.class) != null) {
				if (extras == null) {
					extras = onResultBlock.decl(this.getClasses().BUNDLE, "extras_", JOp.cond(bundle.ne(JExpr._null()), bundle, JExpr._new(this.getClasses().BUNDLE)));
				}

				IJExpression extraParameter = this.extraHandler.getExtraValue(parameter, extras, onResultBlock, holder);
				onResultInvocation.arg(extraParameter);
			} else if ("android.os.Bundle".equals(parameterType.toString())) {
				onResultInvocation.arg(bundle);
			} else if (parameterType.getKind().equals(TypeKind.INT) || CanonicalNameConstants.INTEGER.equals(parameterType.toString())) {
				onResultInvocation.arg(onFragmentResultDelegate.getResultCodeParam());
			}
		}
		onResultBlock.add(onResultInvocation);
	}

	private class ExtraHandler extends ExtraParameterHandler {
		ExtraHandler(AndroidAnnotationsEnvironment environment) {
			super(OnFragmentResult.Extra.class, OnFragmentResult.class, environment);
		}

		public String getAnnotationValue(VariableElement parameter) {
			return (parameter.getAnnotation(OnFragmentResult.Extra.class)).value();
		}

		public IJExpression getExtraValue(VariableElement parameter, JVar extras, JBlock block, EFragmentHolder holder) {
			OnFragmentResultDelegate onFragmentResultDelegate;
			if (onFragmentResultDelegateMap.containsKey(holder)) {
				onFragmentResultDelegate = onFragmentResultDelegateMap.get(holder);
			} else {
				onFragmentResultDelegate = new OnFragmentResultDelegate(holder);
				onFragmentResultDelegateMap.put(holder, onFragmentResultDelegate);
			}
			return super.getExtraValue(parameter, extras, block, onFragmentResultDelegate.getMethod(), holder);
		}
	}
}

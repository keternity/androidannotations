package org.androidannotations.holder;

import static com.helger.jcodemodel.JExpr._super;

import java.util.HashMap;
import java.util.Map;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCase;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JSwitch;
import com.helger.jcodemodel.JVar;

public class OnFragmentResultDelegate extends GeneratedClassHolderDelegate<EComponentHolder> {
	private JMethod method;
	private JBlock afterSuperBlock;
	private JSwitch zwitch;
	private JVar requestCodeParam;
	private JVar dataParam;
	private JVar resultCodeParam;
	private Map<Integer, JBlock> caseBlocks = new HashMap<>();

	public OnFragmentResultDelegate(EComponentHolder holder) {
		super(holder);
	}

	public JMethod getMethod() {
		if (this.method == null) {
			this.setOnFragmentResult();
		}

		return this.method;
	}

	public JVar getRequestCodeParam() {
		if (this.requestCodeParam == null) {
			this.setOnFragmentResult();
		}

		return this.requestCodeParam;
	}

	public JVar getDataParam() {
		if (this.dataParam == null) {
			this.setOnFragmentResult();
		}

		return this.dataParam;
	}

	public JVar getResultCodeParam() {
		if (this.dataParam == null) {
			this.setOnFragmentResult();
		}

		return this.resultCodeParam;
	}

	public JBlock getCaseBlock(int requestCode) {
		JBlock onFragmentResultCaseBlock = this.caseBlocks.get(requestCode);
		if (onFragmentResultCaseBlock == null) {
			onFragmentResultCaseBlock = this.createCaseBlock(requestCode);
			this.caseBlocks.put(requestCode, onFragmentResultCaseBlock);
		}

		return onFragmentResultCaseBlock;
	}

	private JBlock createCaseBlock(int requestCode) {
		JCase onFragmentResultCase = this.getSwitch()._case(JExpr.lit(requestCode));
		JBlock onFragmentResultCaseBlock = onFragmentResultCase.body().blockSimple();
		onFragmentResultCase.body()._break();
		return onFragmentResultCaseBlock;
	}

	public JSwitch getSwitch() {
		if (this.zwitch == null) {
			this.setSwitch();
		}

		return this.zwitch;
	}

	private void setSwitch() {
		this.zwitch = this.getAfterSuperBlock()._switch(this.getRequestCodeParam());
	}

	public JBlock getAfterSuperBlock() {
		if (this.afterSuperBlock == null) {
			this.setOnFragmentResult();
		}

		return this.afterSuperBlock;
	}

	private void setOnFragmentResult() {
		this.method = (this.holder).getGeneratedClass().method(1, this.codeModel().VOID, "onFragmentResult");
		this.method.annotate(Override.class);
		this.requestCodeParam = this.method.param(this.codeModel().INT, "requestCode");
		this.resultCodeParam = this.method.param(this.codeModel().INT, "resultCode");
		this.dataParam = this.method.param(this.getClasses().BUNDLE, "bundle");
		JBlock body = this.method.body();
		body.add(_super().invoke(this.method).arg(this.requestCodeParam).arg(this.resultCodeParam).arg(this.dataParam));
		this.afterSuperBlock = body.blockSimple();
	}
}

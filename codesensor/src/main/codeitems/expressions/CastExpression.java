package main.codeitems.expressions;

import main.codeitems.CodeItem;

public class CastExpression extends Expression {
	
	Expression castTarget = null;
	Expression castExpression = null;
	
	@Override
	public void addChild(CodeItem expression)
	{
		if(castTarget == null){
			castTarget = (Expression) expression;
		}else{
			castExpression = (Expression) expression;
		}
	}

	@Override
	public int getChildCount()
	{
		int childCount = 0;
		if(castTarget != null) childCount++;
		if(castExpression != null) childCount++;
		return childCount;
	}

	@Override
	public CodeItem getChild(int i)
	{
		if(i == 0) return castTarget;
		return castExpression;
	}

	public CodeItem getCastTarget()
	{
		return castTarget;
	}

}
package com.dubture.symfony.annotation.parser.antlr;



/**
 * 
 * 
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public interface IAnnotationNodeVisitor {
	
	
	void beginVisit(AnnotationCommonTree node);
	void endVisit(AnnotationCommonTree node);
}

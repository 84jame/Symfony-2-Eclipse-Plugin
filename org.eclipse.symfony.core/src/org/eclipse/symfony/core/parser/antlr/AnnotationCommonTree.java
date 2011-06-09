package org.eclipse.symfony.core.parser.antlr;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.eclipse.symfony.core.visitor.AnnotationVisitor;


/**
 * 
 * The {@link AnnotationCommonTree} is used to traverse
 * the Tree created by the {@link SymfonyAnnotationParser}.
 * 
 * @see AnnotationVisitor
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class AnnotationCommonTree extends CommonTree {

	
    public AnnotationCommonTree(Token payload) {
    	super(payload);
	}

	@Override
    public AnnotationCommonTree getChild(int i) {
    	
        if (children == null || i >= children.size()) {
            return null;
        }
        return (AnnotationCommonTree)children.get(i);
    }
    
    @Override
    public CommonToken getToken() {
        return (CommonToken)token;
    }

    /**
     * Traverse the annotation tree.
     * 
     * @param visitor
     */
	public void accept(IAnnotationNodeVisitor visitor) {

		visitor.beginVisit(this);
		
		for (int i = 0; i < getChildCount(); i++) {
			AnnotationCommonTree child = (AnnotationCommonTree) getChild(i);
			child.accept(visitor);
			
		}
		
		visitor.endVisit(this);
		
	}
}
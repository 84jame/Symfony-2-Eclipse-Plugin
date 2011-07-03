package org.eclipse.symfony.test;

import java.util.Map;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.symfony.core.parser.antlr.AnnotationCommonTree;
import org.eclipse.symfony.core.parser.antlr.AnnotationCommonTreeAdaptor;
import org.eclipse.symfony.core.parser.antlr.AnnotationLexer;
import org.eclipse.symfony.core.parser.antlr.AnnotationNodeVisitor;
import org.eclipse.symfony.core.parser.antlr.AnnotationParser;
import org.eclipse.symfony.test.reporter.DebugErrorReporter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 *  
 * Tests the {@link AnnotationLexer} and {@link AnnotationParser}
 * classes.
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class AnnotationParserTest extends TestCase {

	
	private DebugErrorReporter reporter = new DebugErrorReporter();	
	private AnnotationNodeVisitor root;
	
	
	@Before
	public void setUp() throws Exception {
		
		reporter.reset();
	}

	@After
	public void tearDown() throws Exception {
	
		reporter.reset();
		
	}
	
	
	@Test
	public void testSyntaxErrors() {
		
		
		root = getRootNode("@ManyToManyPersister('shall\". fo)", false);
		
		
	}

	
	@Test
	public void testTemplatePath() {
		
		root = getRootNode("* @Template(\"DemoBundle:Welcome:index.html.twig\")", false);

	}
	
	@Test
	public void testRoute() {
		
		root = getRootNode("* @Route('/blog', name='_blog')", false);		
		Map<String, String> args = root.getArguments();		
		assertNotNull(args);		
		String route = args.get("name");		
		assertNotNull(route);
		assertEquals("'_blog'", route);		
		
	}

	
	@Test
	public void testEmpty() {
		
		root = getRootNode("* @Route()", false);
		
		assertNotNull(root);
		assertEquals("Route", root.getClassName());			
		assertTrue(root.getNamespace().length() == 0);
		
	}
	
	@Test
	public void testNSEmpty() {
		
		root = getRootNode("* @ORM\\Foo()", false);
		
		assertEquals("Foo", root.getClassName());
		assertEquals("ORM\\", root.getNamespace());
		assertEquals("ORM\\Foo", root.getFullyQualifiedName());
		
	}

	@Test
	public void testJson() {
		
		root = getRootNode("@Route(\"/hello\", defaults={\"name\"=\"World\"})", false);
		
		assertNotNull(root);
		assertEquals("Route",root.getClassName());		
		assertFalse(reporter.hasErrors());
		
	}
	
	@Test
	public void testRouteVariable() {
		
		root = getRootNode("* @Route(\"/hello/{name}\", name=\"_demo_secured_hello\")", false);
		
		assertNotNull(root);
		assertEquals("Route",root.getClassName());		
		assertFalse(reporter.hasErrors());
		
	}
	
	
	@Test
	public void testSyntaxError() {

		root = getRootNode("* @Ro+--ute(name='test\")", true);			
		assertNotNull(root);
		assertTrue(reporter.hasErrors());
		
	}

	@Test
	public void testMissingArgument() {
		
		root = getRootNode("* @Route(name=)", true);
		assertNotNull(root);
		assertTrue(reporter.hasErrors());		
		
	}
	
	@Test
	public void testMissingComma() {
		
		root = getRootNode("* @Template(name='foo' bar)", true);
		assertNotNull(root);
		assertTrue(reporter.hasErrors());		
		
	}
	
	@Test
	public void testUnclosedQuote() {
		
		root = getRootNode("* @Template(aho=')", true);
		assertNotNull(root);
		assertTrue(reporter.hasErrors());				
		
	}
	
	
	/**
	 * Parse an annotation and return the NodeVisitor to test against.
	 *  
	 * @param line
	 * @param expectFail if the parsing process is expected to fail
	 * @return {@link AnnotationNodeVisitor}
	 */
	private AnnotationNodeVisitor getRootNode(String line, boolean expectFail) {

		
		try {
			
			int start = line.indexOf('@');
			int end = line.length()-1;
			
			String annotation = line.substring(start, end+1);
			CharStream content = new ANTLRStringStream(annotation);
			
			AnnotationLexer lexer = new AnnotationLexer(content, reporter);
			
			AnnotationParser parser = new AnnotationParser(new CommonTokenStream(lexer));
			parser.setErrorReporter(reporter);
			
			parser.setTreeAdaptor(new AnnotationCommonTreeAdaptor());
			AnnotationParser.annotation_return root;
			
			root = parser.annotation();
			AnnotationCommonTree tree = (AnnotationCommonTree) root.getTree();
			AnnotationNodeVisitor visitor = new AnnotationNodeVisitor();
			tree.accept(visitor);
				
			if (expectFail && reporter.hasErrors() == false) {
				
				System.err.println(line + " " + reporter.hasErrors());
				fail();
			}
				
			
			return visitor;
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			// we should never be here, if the parser fails it should
			// still finish normally but log syntax errors
			fail();
						
		}

		if (!expectFail && reporter.hasErrors())
			fail();
		
		return null;		
		
	}
}
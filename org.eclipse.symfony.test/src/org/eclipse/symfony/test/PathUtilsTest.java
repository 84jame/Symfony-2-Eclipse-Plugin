package org.eclipse.symfony.test;


import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.symfony.core.util.PathUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PathUtilsTest extends TestCase {

	@Before
	protected void setUp() throws Exception {
		super.setUp();		

	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void test() {

		IPath fieldPath = new Path("/Symfony2/src/Acme/DemoBundle/Controller/DemoController.php");		
		String controller = PathUtils.getControllerFromFieldPath(fieldPath);
		
		assertEquals("DemoController", controller);
		
	}
}
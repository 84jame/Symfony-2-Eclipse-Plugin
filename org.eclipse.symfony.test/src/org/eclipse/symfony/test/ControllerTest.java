package org.eclipse.symfony.test;


import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.index2.IIndexerParticipant;
import org.eclipse.dltk.core.index2.IIndexingRequestor;
import org.eclipse.dltk.core.index2.search.ModelAccess;
import org.eclipse.php.core.tests.AbstractPDTTTest;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.symfony.core.SymfonyLanguageToolkit;
import org.eclipse.symfony.core.index.SymfonyIndexerParticipant;
import org.eclipse.symfony.core.index.SymfonyIndexingParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("restriction")
public class ControllerTest extends AbstractPDTTTest {

	
	protected static final Map<PHPVersion, String[]> TESTS = new LinkedHashMap<PHPVersion, String[]>();
	static {
		TESTS.put(PHPVersion.PHP5_3,
				new String[] { "/workspace/model_structure/php53" });
	};
	
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
				
		try {
			
			String data = "<?php $foo = 'bar';";
			
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
					"ModelStructureTests");
			
			IFile testFile = project.getFile("test.php");
			testFile.create(new ByteArrayInputStream(data.getBytes()), true, null);						
			
			IIndexerParticipant indexer = ModelAccess.getIndexerParticipant(SymfonyLanguageToolkit.getDefault());			
			assertTrue(indexer instanceof SymfonyIndexerParticipant);
			
			SymfonyIndexingParser parser = (SymfonyIndexingParser) indexer.getIndexingParser();			
			assertTrue(parser instanceof SymfonyIndexingParser);
			
			ISourceModule source = DLTKCore.createSourceModuleFrom(testFile);
			parser.parseSourceModule(source, new IIndexingRequestor() {
				
				@Override
				public void addReference(ReferenceInfo info) {
					
				}
				
				@Override
				public void addDeclaration(DeclarationInfo info) {

					
				}
			});

			
			
		} catch (Exception e) {

			e.printStackTrace();
			fail();
		}
	}

}

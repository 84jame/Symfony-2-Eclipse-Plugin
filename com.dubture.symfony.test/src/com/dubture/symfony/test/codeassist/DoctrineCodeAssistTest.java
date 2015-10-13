package com.dubture.symfony.test.codeassist;

import org.junit.Ignore;
import org.junit.Test;

import com.dubture.symfony.test.AbstractCodeAssistTest;

@Ignore
public class DoctrineCodeAssistTest extends AbstractCodeAssistTest {

	public DoctrineCodeAssistTest() {
		super("doctrine");
	}
	
	@Test
	public void testEntityCompletion() throws Exception {
		runPdttTest("testEntityCompletion.pdtt");
	}

	@Test
	public void testNamespaceCompletion() throws Exception {
		runPdttTest("testNamespaceCompletion.pdtt");
	}
	
	@Test
	public void testRepositoryCompletion() throws Exception {
		runPdttTest("testRepositoryCompletion.pdtt");
	}
}

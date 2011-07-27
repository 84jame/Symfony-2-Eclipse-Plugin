package com.dubture.symfony.test;


import java.io.File;
import java.io.FileInputStream;
import java.util.Stack;

import junit.framework.TestCase;

import org.junit.Test;

import com.dubture.symfony.core.parser.YamlRoutingParser;
import com.dubture.symfony.index.dao.Route;

public class RoutingParserTest extends TestCase {

	@Test
	public void test() {

		try {
			
			// see http://code.google.com/p/snakeyaml/issues/detail?id=78
			// and http://code.google.com/p/yedit/issues/detail?id=32
			String dir = System.getProperty("user.dir") + "/Resources/config/routing.yml";
			FileInputStream input;

			input = new FileInputStream(new File(dir));
			
			YamlRoutingParser parser = new YamlRoutingParser(input);
			parser.parse();
			
			Stack<Route> routes = parser.getRoutes();
			
			assertEquals(1, routes.size());
			
			Route route = routes.pop();
			
			assertEquals("blog", route.name);
			assertEquals("/blog", route.pattern);
			assertEquals("AcmeBlogBundle:Blog:index", route.controller);
			
		} catch (Exception e) {

			e.printStackTrace();
			fail();
			
		}				
	}
}